package com.heb.togglr.api.client.cache.memory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.heb.togglr.api.client.cache.TogglrCache;
import com.heb.togglr.api.client.model.response.FeatureResponse;
import com.heb.togglr.api.client.service.TogglrUpdateNotifier;

@Service
@ConditionalOnProperty(
        value="heb.togglr.client.cache-type",
        havingValue = "in-memory",
        matchIfMissing = true)
public class InMemoryCache extends TogglrCache {

    private Map<String, List<FeatureResponse>> cache;

    public InMemoryCache(TogglrUpdateNotifier updateNotifier) {
        super(updateNotifier);
        this.cache = new HashMap<>();
    }

    @Override
    protected  List<FeatureResponse> getCachedFeatures(String cacheId) {
        return cache.get(cacheId);
    }

    @Override
    protected  void setCachedFeatures(String cacheId, List<FeatureResponse> featureResponses) {
        this.cache.put(cacheId, featureResponses);
    }
}
