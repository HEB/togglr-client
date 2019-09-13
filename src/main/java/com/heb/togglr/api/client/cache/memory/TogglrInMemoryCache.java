package com.heb.togglr.api.client.cache.memory;

import com.heb.togglr.api.client.cache.TogglrCache;
import com.heb.togglr.api.client.model.response.FeatureResponse;
import com.heb.togglr.api.client.service.TogglrUpdateNotifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(
        value = "heb.togglr.client.cache-type",
        havingValue = "in-memory",
        matchIfMissing = true)
public class TogglrInMemoryCache extends TogglrCache {

    private Map<String, List<FeatureResponse>> cache;

    public TogglrInMemoryCache(TogglrUpdateNotifier updateNotifier) {
        super(updateNotifier);
        this.cache = new HashMap<>();
    }

    @Override
    protected List<FeatureResponse> getCachedFeatures(String cacheId) {
        return cache.get(cacheId);
    }

    @Override
    protected void setCachedFeatures(String cacheId, List<FeatureResponse> featureResponses) {
        this.cache.put(cacheId, featureResponses);
    }
}
