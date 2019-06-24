package com.heb.togglr.api.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.heb.togglr.api.client.cache.TogglrCache;
import com.heb.togglr.api.client.model.requests.ActiveFeaturesRequest;

@Service
public class TogglrClient {

    private static Logger logger = LoggerFactory.getLogger(TogglrClient.class);

    private TogglrCache togglrCache;

    public TogglrClient(TogglrCache togglrCache){
        this.togglrCache = togglrCache;
    }

    /**
     * Manually clear the cache.
     */
    public void clearCache(){
        this.togglrCache.clearCache();
    }

    /**
     * Get the list of ActiveFeatures, returned as GrantedAuthorities for a config.
     * @param activeFeaturesRequest List of active Features.
     * @param cacheId Identifier used for caching the Features.
     * @return
     */
    public List<GrantedAuthority> getFeaturesForConfig(ActiveFeaturesRequest activeFeaturesRequest, String cacheId) {

        return this.togglrCache.getFeaturesForConfig(activeFeaturesRequest, cacheId);

    }
}
