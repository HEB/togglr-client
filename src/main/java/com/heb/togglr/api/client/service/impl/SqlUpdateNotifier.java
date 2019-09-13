package com.heb.togglr.api.client.service.impl;


import com.heb.togglr.api.client.cache.sql.TogglrDatabaseService;
import com.heb.togglr.api.client.model.database.CachedEntry;
import com.heb.togglr.api.client.service.TogglrUpdateNotifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        value = "heb.togglr.client.cache-type",
        havingValue = "sql")
public class SqlUpdateNotifier implements TogglrUpdateNotifier {


    private TogglrDatabaseService togglrDatabaseService;

    public SqlUpdateNotifier(TogglrDatabaseService togglrDatabaseService) {
        this.togglrDatabaseService = togglrDatabaseService;
    }

    @Override
    public void registerNewUpdate() {
        this.togglrDatabaseService.updateCurrentCacheVersion();
    }

    @Override
    public boolean doesClientNeedUpdate(String cacheId) {
        long cacheVersion = this.togglrDatabaseService.getCurrentCacheVersion();
        CachedEntry cachedEntry = this.togglrDatabaseService.getCachedEntryByCacheId(cacheId);

        return (cachedEntry.getCacheVersion() < cacheVersion);
    }

    @Override
    public void updateUserVersion(String cacheId) {
        CachedEntry cachedEntry = this.togglrDatabaseService.getCachedEntryByCacheId(cacheId);
        cachedEntry.setCacheVersion(cachedEntry.getCacheVersion() + 1);
        this.togglrDatabaseService.updateCachedEntry(cachedEntry);
    }

    @Override
    public void clearCache() {
        return;
    }
}
