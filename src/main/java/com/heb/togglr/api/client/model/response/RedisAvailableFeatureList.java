package com.heb.togglr.api.client.model.response;

public class RedisAvailableFeatureList extends AvailableFeaturesList {

    private long lastCachedVersion;

    public long getLastCachedVersion() {
        return lastCachedVersion;
    }

    public void setLastCachedVersion(long lastCachedVersion) {
        this.lastCachedVersion = lastCachedVersion;
    }
}
