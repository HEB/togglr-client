package com.heb.togglr.api.client.cache;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.client.RestTemplate;

import com.heb.togglr.api.client.model.requests.ActiveFeaturesRequest;
import com.heb.togglr.api.client.model.response.AvailableFeaturesList;
import com.heb.togglr.api.client.model.response.FeatureResponse;
import com.heb.togglr.api.client.service.TogglrUpdateNotifier;

public abstract class TogglrCache {

    private static Logger logger = LoggerFactory.getLogger(TogglrCache.class);

    @Value("${heb.togglr.client.app-id}")
    private int applicationId;

    @Value("${heb.togglr.client.server-url}")
    private String togglrUrl;

    private TogglrUpdateNotifier updateNotifier;

    private RestTemplate restTemplate;

    public TogglrCache(TogglrUpdateNotifier updateNotifier){
        this.updateNotifier = updateNotifier;
        this.restTemplate = new RestTemplate();
    }

    public List<GrantedAuthority> getFeaturesForConfig(ActiveFeaturesRequest activeFeaturesRequest, String cacheId){

        List<FeatureResponse> features = null;

        activeFeaturesRequest.setAppId(this.applicationId);

        logger.debug("Handling feature request for " + cacheId);
        logger.trace(activeFeaturesRequest.toString());

        if(!this.updateNotifier.doesClientNeedUpdate(cacheId)){
            logger.debug(cacheId + " does not require update.");
            features = this.getCachedFeatures(cacheId);
        }

        if(features == null) {
            logger.trace(cacheId + " has no cached features.");
            try {
                logger.trace("Making rest call to " + this.togglrUrl);
                AvailableFeaturesList availableFeaturesList = this.restTemplate.postForObject(this.togglrUrl, activeFeaturesRequest, AvailableFeaturesList.class);

                logger.debug("Rest call to " + this.togglrUrl + " completed");
                if (availableFeaturesList != null) {
                    logger.debug("Got a valid value as a response.");
                    logger.trace(availableFeaturesList.toString());
                    features = availableFeaturesList.getAvailableFeatures();
                    this.setCachedFeatures(cacheId, availableFeaturesList.getAvailableFeatures());
                    this.updateNotifier.updateUserVersion(cacheId);
                }

            } catch (Exception e) {
                logger.error("Could not update Togglr Configuration");
                logger.error(e.getMessage());
                features = this.getCachedFeatures(cacheId);

                if(features != null){
                    List<GrantedAuthority> activeFeatures = new ArrayList<>();

                    for (FeatureResponse featureResponse : features) {
                        activeFeatures.add(new SimpleGrantedAuthority(featureResponse.getDescr()));
                    }

                    return activeFeatures;
                }
            }
        }

        if(features == null) {
            return new ArrayList<>();
        }else{
            List<GrantedAuthority> activeFeatures = new ArrayList<>();

            for (FeatureResponse featureResponse : features) {
                activeFeatures.add(new SimpleGrantedAuthority(featureResponse.getDescr()));
            }

            return activeFeatures;
        }
    }

    public void clearCache(){
        this.updateNotifier.clearCache();
    }

    protected abstract List<FeatureResponse> getCachedFeatures(String cacheId);

    protected abstract void setCachedFeatures(String cacheId, List<FeatureResponse> featureResponses);

}
