package com.heb.togglr.api.client.service.impl;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
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

    public RedisUpdateNotifier(RedisService redisService){
        this.redisService = redisService;
    }

    @Override
    public void registerNewUpdate() {
        long currentVersion = this.redisService.getCurrentVersion();
        this.redisService.setCurrentVersion(++currentVersion);
    }

    @Override
    public boolean doesClientNeedUpdate(String cacheId) {
        RedisAvailableFeatureList redisAvailableFeatureList = this.redisService.getCachedFeatures(cacheId);

        long currentVersion = this.redisService.getCurrentVersion();
        if(redisAvailableFeatureList == null || redisAvailableFeatureList.getLastCachedVersion() < currentVersion){
            return true;
        }
        return false;
    }

    @Override
    public void updateUserVersion(String cacheId) {
        RedisAvailableFeatureList redisAvailableFeatureList = this.redisService.getCachedFeatures(cacheId);
        redisAvailableFeatureList.setLastCachedVersion(redisAvailableFeatureList.getLastCachedVersion() + 1);
    }

    @Override
    public void clearCache() {
        return;
    }
}
