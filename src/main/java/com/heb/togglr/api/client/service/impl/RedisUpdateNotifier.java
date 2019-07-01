package com.heb.togglr.api.client.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.heb.togglr.api.client.exception.RedisException;
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
    public void registerNewUpdate() throws RedisException {
        try {
            logger.debug("Registering new update.");
            long currentVersion = this.redisService.getCurrentVersion();
            this.redisService.setCurrentVersion(++currentVersion);
        }catch (RedisException e) {
            logger.error("Could not register redis update: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean doesClientNeedUpdate(String cacheId) {
        try {
            logger.debug("Getting current client version from Redis.");
            RedisAvailableFeatureList redisAvailableFeatureList = this.redisService.getCachedFeatures(cacheId);

            long currentVersion = this.redisService.getCurrentVersion();
            if (redisAvailableFeatureList == null || redisAvailableFeatureList.getLastCachedVersion() < currentVersion) {
                return true;
            }
            return false;
        }catch (RedisException e){
            //If Redis errors, we want to register that we need an update.
            logger.error("Could not get current user version: " + e.getMessage());
            return true;
        }
    }

    @Override
    public void updateUserVersion(String cacheId) {
        try {
            logger.debug("Updating Redis User version.");
            RedisAvailableFeatureList redisAvailableFeatureList = this.redisService.getCachedFeatures(cacheId);
            redisAvailableFeatureList.setLastCachedVersion(redisAvailableFeatureList.getLastCachedVersion() + 1);
        }catch (RedisException e){
            logger.error("Could not update user version: " + e.getMessage());
            //If we get here, no big deal.  They'll update next time.
        }
    }

    @Override
    public void clearCache() {
        return;
    }
}
