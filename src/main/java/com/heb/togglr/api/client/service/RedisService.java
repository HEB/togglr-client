package com.heb.togglr.api.client.service;

import java.io.IOException;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heb.togglr.api.client.exception.RedisException;
import com.heb.togglr.api.client.model.response.RedisAvailableFeatureList;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Service
@ConditionalOnProperty(
        value="heb.togglr.client.cache-type",
        havingValue = "redis")
public class RedisService {

    @Value("${heb.togglr.cache-time:#{0}}")
    private long cacheTime;

    private static Logger logger = LoggerFactory.getLogger(RedisService.class);

    private static final String TOGGLR_VERSION_KEY = "togglr-current-version";
    private static final String TOGGLR_USER_KEY = "togglr-user-";

    private final ObjectMapper objectMapper;

    private static final JedisPoolConfig poolConfig = buildPoolConfig();
    private static JedisPool jedisPool;

    public RedisService(@Value("${heb.togglr.redis.host}") String redisServer, @Value("${heb.togglr.redis.port}") String redisPort){
        logger.error("Redis Configuration:  \n   Host: " + redisServer + "\n   Port: " + redisPort);
        int port = Integer.parseInt(redisPort);

        jedisPool = new JedisPool(poolConfig, redisServer, port);
        this.objectMapper = new ObjectMapper();
    }

    public long getCurrentVersion() throws RedisException {
        try (Jedis jedis = jedisPool.getResource()) {
            String currentVersionString = jedis.get(TOGGLR_VERSION_KEY);


            if (currentVersionString == null) {
                this.setCurrentVersion(0);
                return 0;
            } else {

                long currentVersion = Long.parseLong(currentVersionString);

                return currentVersion;
            }
            // TODO: Need to see what exception to catch.
        }catch(Exception e){
            throw new RedisException("Unable to connect to Redis." + e.getMessage());
        }
    }

    public void setCurrentVersion(long version) throws RedisException {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(TOGGLR_VERSION_KEY, version + "");
        }catch(Exception e){
            throw new RedisException("Unable to connect to Redis." + e.getMessage());
        }
    }

    public RedisAvailableFeatureList getCachedFeatures(String cacheId) throws RedisException {
        try (Jedis jedis = jedisPool.getResource()) {
            String featuresJson = jedis.get(TOGGLR_USER_KEY + cacheId);
            if(featuresJson != null) {
                try {
                    RedisAvailableFeatureList featureList = objectMapper.readValue(featuresJson, RedisAvailableFeatureList.class);
                    return featureList;
                } catch (IOException e) {
                    logger.error("Could not Map value from Cache");
                    e.printStackTrace();
                    return null;
                }
            }else{
                return null;
            }
        }catch(Exception e){
            throw new RedisException("Unable to connect to Redis." + e.getMessage());
        }
    }

    public void setCachedFeatures(String cacheId, RedisAvailableFeatureList redisAvailableFeatureList) throws RedisException {
        try (Jedis jedis = jedisPool.getResource()) {
            String featureJson = this.objectMapper.writeValueAsString(redisAvailableFeatureList);
            if(this.cacheTime > 0){
                jedis.setex(TOGGLR_USER_KEY + cacheId, ((int)(this.cacheTime / 1000)), featureJson );
            }else {
                jedis.set(TOGGLR_USER_KEY + cacheId, featureJson);
            }
        }catch(Exception e){
            throw new RedisException("Unable to connect to Redis." + e.getMessage());
        }
    }

    private static JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }
}
