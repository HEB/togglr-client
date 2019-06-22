package com.heb.togglr.api.client.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.heb.togglr.api.client.model.response.RedisAvailableFeatureList;
import com.heb.togglr.api.client.service.RedisService;
import com.heb.togglr.api.client.service.TogglrUpdateNotifier;

@Service
@ConditionalOnProperty(
        value="heb.togglr.client.cache-type",
        havingValue = "redis")
public class RedisUpdateNotifier implements TogglrUpdateNotifier {

    private RedisService redisService;
    private static Logger logger = LoggerFactory.getLogger(RedisUpdateNotifier.class);

    public RedisUpdateNotifier(RedisService redisService){
        this.redisService = redisService;
    }

    @Override
    public void registerNewUpdate() {
        logger.debug("Registering new update.");
        long currentVersion = this.redisService.getCurrentVersion();
        this.redisService.setCurrentVersion(++currentVersion);
    }

    @Override
    public boolean doesClientNeedUpdate(String cacheId) {
        logger.debug("Getting current client version from Redis.");
        RedisAvailableFeatureList redisAvailableFeatureList = this.redisService.getCachedFeatures(cacheId);

        long currentVersion = this.redisService.getCurrentVersion();
        if(redisAvailableFeatureList == null || redisAvailableFeatureList.getLastCachedVersion() < currentVersion){
            return true;
        }
        return false;
    }

    @Override
    public void updateUserVersion(String cacheId) {
        logger.debug("Updating Redis User version.");
        RedisAvailableFeatureList redisAvailableFeatureList = this.redisService.getCachedFeatures(cacheId);
        redisAvailableFeatureList.setLastCachedVersion(redisAvailableFeatureList.getLastCachedVersion() + 1);
    }

    @Override
    public void clearCache() {
        return;
    }
}
