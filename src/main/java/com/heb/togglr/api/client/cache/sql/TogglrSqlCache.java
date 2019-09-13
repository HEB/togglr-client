package com.heb.togglr.api.client.cache.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heb.togglr.api.client.cache.TogglrCache;
import com.heb.togglr.api.client.model.database.CachedEntry;
import com.heb.togglr.api.client.model.response.FeatureResponse;
import com.heb.togglr.api.client.service.TogglrUpdateNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@ConditionalOnProperty(
        value = "heb.togglr.client.cache-type",
        havingValue = "sql")
public class TogglrSqlCache extends TogglrCache {

    private static Logger logger = LoggerFactory.getLogger(TogglrSqlCache.class);

    private TogglrDatabaseService togglrDatabaseService;
    private ObjectMapper objectMapper;

    public TogglrSqlCache(TogglrUpdateNotifier updateNotifier, TogglrDatabaseService togglrDatabaseService) {
        super(updateNotifier);
        this.togglrDatabaseService = togglrDatabaseService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected List<FeatureResponse> getCachedFeatures(String cacheId) {
        CachedEntry cachedEntry = this.togglrDatabaseService.getCachedEntryByCacheId(cacheId);

        if (cachedEntry != null) {
            try {
                return Arrays.asList(this.objectMapper.readValue(cachedEntry.getJsonData(), FeatureResponse[].class));
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        return null;
    }

    @Override
    protected void setCachedFeatures(String cacheId, List<FeatureResponse> featureResponses) {
        try {
            CachedEntry cachedEntry = new CachedEntry();
            cachedEntry.setCacheVersion(this.togglrDatabaseService.getCurrentCacheVersion());
            cachedEntry.setLastUpdate(new Timestamp(new Date().getTime()));
            cachedEntry.setJsonData(this.objectMapper.writeValueAsString(featureResponses));
            this.togglrDatabaseService.updateCachedEntry(cachedEntry);
        } catch (JsonProcessingException e) {
            logger.error("Could not update Cached Data");
            e.printStackTrace();
        }
    }
}
