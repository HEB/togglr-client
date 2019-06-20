package com.heb.togglr.api.client;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.heb.togglr.api.client.model.requests.ActiveFeaturesRequest;
import com.heb.togglr.api.client.model.response.AvailableFeaturesList;
import com.heb.togglr.api.client.model.response.FeatureResponse;
import com.heb.togglr.api.client.service.TogglrUpdateNotifier;
import redis.clients.jedis.Jedis;

@Service
public class TogglrClient {

    private static Logger logger = LoggerFactory.getLogger(TogglrClient.class);

    @Value("${heb.togglr.client.app-id}")
    private String applicationId;

    @Value("${heb.togglr.client.server-url}")
    private String togglrUrl;

    private TogglrUpdateNotifier togglrUpdateNotifier;
    private RestTemplate restTemplate;

    private Map<String, List<FeatureResponse>> cache;

    private Jedis jedis = new Jedis("localhost", 6379);

    public TogglrClient(TogglrUpdateNotifier togglrUpdateNotifier){
        this.togglrUpdateNotifier = togglrUpdateNotifier;
        this.restTemplate = new RestTemplate();
        this.cache = new HashMap<>();
    }


    public List<GrantedAuthority> getFeaturesForConfig(ActiveFeaturesRequest activeFeaturesRequest, String userId){

        List<FeatureResponse> features = null;

        if(!this.togglrUpdateNotifier.doesClientNeedUpdate(userId)){
            features = cache.get(userId);
        }

        if(features == null) {
            try {
                AvailableFeaturesList availableFeaturesList = this.restTemplate.postForObject(this.togglrUrl, activeFeaturesRequest, AvailableFeaturesList.class);

                if (availableFeaturesList != null) {

                    features = availableFeaturesList.getAvailableFeatures();
                    this.cache.put(userId, availableFeaturesList.getAvailableFeatures());
                    this.togglrUpdateNotifier.updateUserVersion(userId);
                }

            } catch (Exception e) {
                logger.error("Could not update Togglr Configuration");

                features = cache.get(userId);

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
}
