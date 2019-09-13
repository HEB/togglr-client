package com.heb.togglr.api.client.cache.sql;

import com.heb.togglr.api.client.model.database.CachedEntry;

public interface TogglrDatabaseService {


    /**
     * Gets the current CachedEntry for a given cacheId
     *
     * @param cacheId
     * @return
     */
    public CachedEntry getCachedEntryByCacheId(String cacheId);

    /**
     * Save the CachedEntry into the database
     *
     * @param cachedEntry
     */
    public void updateCachedEntry(CachedEntry cachedEntry);

    /**
     * Returns the current cached version Id
     *
     * @return
     */
    public long getCurrentCacheVersion();

    /**
     * Increments the cached version Id
     */
    public void updateCurrentCacheVersion();

}
