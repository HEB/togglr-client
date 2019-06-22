package com.heb.togglr.api.client.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heb.togglr.api.client.model.response.RedisAvailableFeatureList;
import redis.clients.jedis.Jedis;


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

    private Jedis jedis;
    private ObjectMapper objectMapper;

    public RedisService(@Value("${heb.togglr.redis.host}") String redisServer, @Value("${heb.togglr.redis.port}") String redisPort){
        logger.error("Redis Configuration:  \n   Host: " + redisServer + "\n   Port: " + redisPort);
        int port = Integer.parseInt(redisPort);
        this.jedis = new Jedis(redisServer, port);
        this.objectMapper = new ObjectMapper();
    }

    public long getCurrentVersion(){
        String currentVersionString = this.jedis.get(TOGGLR_VERSION_KEY);


        if(currentVersionString == null){
            this.setCurrentVersion(0);
            return 0;
        }else {

            long currentVersion = Long.parseLong(currentVersionString);

            return currentVersion;
        }
    }

    public void setCurrentVersion(long version){
        this.jedis.set(TOGGLR_VERSION_KEY, version + "");
    }

    public RedisAvailableFeatureList getCachedFeatures(String cacheId){
        String featuresJson = this.jedis.get(TOGGLR_USER_KEY + cacheId);
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
    }

    public void setCachedFeatures(String cacheId, RedisAvailableFeatureList redisAvailableFeatureList){
        try {
            String featureJson = this.objectMapper.writeValueAsString(redisAvailableFeatureList);
            if(this.cacheTime > 0){
                this.jedis.setex(TOGGLR_USER_KEY + cacheId, ((int)(this.cacheTime / 1000)), featureJson );
            }else {
                this.jedis.set(TOGGLR_USER_KEY + cacheId, featureJson);
            }
        } catch (JsonProcessingException e) {
            logger.error("Could not serialize user settings.");
            e.printStackTrace();
        }
    }
}
