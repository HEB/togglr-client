package com.heb.togglr.api.client.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class TogglrUpdateNotifier {

    private static Logger logger = LoggerFactory.getLogger(TogglrUpdateNotifier.class);

    @Value("${heb.togglr.cache-time:#{0}}")
    private long cacheTime;

    private long updateCount = 0;
    private Map<String, Long> lastUpdateReceivedd;
    private Map<String, Long> lastUpdateTime;

    public TogglrUpdateNotifier(){
        this.lastUpdateReceivedd = new HashMap<>();
        this.lastUpdateTime = new HashMap<>();
    }

    public void registerNewUpdate(){
        logger.info("Registered a Togglr Config Update.");
        this.updateCount++;
        this.clearCache();
    }

    /**
     * Returns True if the user needs to have their togglr items refreshed.
     * @param cacheId
     * @return
     */
    public boolean doesClientNeedUpdate(String cacheId){
        Long lastUpdate = this.lastUpdateReceivedd.get(cacheId);
        Long lastUpdateTime = this.lastUpdateTime.get(cacheId);


        if(lastUpdate == null || lastUpdate < updateCount || Instant.now().toEpochMilli() - lastUpdateTime > cacheTime){
            logger.debug(cacheId + " requires a feature update.");
            return true;
        }

        logger.debug(cacheId + " does not requires a feature update.");
        return false;
    }

    /**
     * Stores the current update version for the user.
     * @param cacheId
     */
    public void updateUserVersion(String cacheId){
        logger.debug("Updating Togglr version for " + cacheId);
        this.lastUpdateReceivedd.put(cacheId, this.updateCount);
        this.lastUpdateTime.put(cacheId, Instant.now().toEpochMilli());
    }

    /**
     * Reset the maps used for caching.
     */
    public void clearCache(){
        logger.info("Togglr cache cleared.");
        this.lastUpdateReceivedd.clear();
        this.lastUpdateTime.clear();
    }
}