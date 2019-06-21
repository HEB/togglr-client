package com.heb.togglr.api.client.cache.redis;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.heb.togglr.api.client.cache.TogglrCache;
import com.heb.togglr.api.client.model.response.FeatureResponse;
import com.heb.togglr.api.client.model.response.RedisAvailableFeatureList;
import com.heb.togglr.api.client.service.RedisService;
import com.heb.togglr.api.client.service.TogglrUpdateNotifier;

@Service
@ConditionalOnProperty(
        value="heb.togglr.client.cache-type",
        havingValue = "redis")
public class RedisCache extends TogglrCache {

    private static Logger logger = LoggerFactory.getLogger(RedisCache.class);

    @Value("${heb.togglr.client.app-id}")
    private int applicationId;

    @Value("${heb.togglr.client.server-url}")
    private String togglrUrl;

    private RedisService redisService;

    public RedisCache(TogglrUpdateNotifier updateNotifier, RedisService redisService) {
        super(updateNotifier);
    }


    @Override
    public List<FeatureResponse> getCachedFeatures(String cacheId) {
        return this.redisService.getCachedFeatures(cacheId).getAvailableFeatures();
    }

    @Override
    public void setCachedFeatures(String cacheId, List<FeatureResponse> featureResponses) {
        RedisAvailableFeatureList featureList = new RedisAvailableFeatureList();
        featureList.setAvailableFeatures(featureResponses);
        featureList.setLastCachedVersion(this.redisService.getCurrentVersion());

        this.redisService.setCachedFeatures(cacheId, featureList);
    }
}
