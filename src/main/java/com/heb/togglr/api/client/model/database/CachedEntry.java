package com.heb.togglr.api.client.model.database;

import java.sql.Timestamp;

public class CachedEntry {

    private Timestamp lastUpdate;
    private Long cacheVersion;
    private String jsonData;

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getCacheVersion() {
        return cacheVersion;
    }

    public void setCacheVersion(Long cacheVersion) {
        this.cacheVersion = cacheVersion;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }
}
