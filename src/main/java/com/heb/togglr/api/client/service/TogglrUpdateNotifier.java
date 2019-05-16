package com.heb.togglr.api.client.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class TogglrUpdateNotifier {

    private long updateCount = 0;
    private Map<String, Long> lastUpdateReceivedd;

    public TogglrUpdateNotifier(){
        this.lastUpdateReceivedd = new HashMap<>();
    }

    public void registerNewUpdate(){
        this.updateCount++;
    }

    /**
     * Returns True if the user needs to have their togglr items refreshed.
     * @param userId
     * @return
     */
    public boolean doesClientNeedUpdate(String userId){
        Long lastUpdate = lastUpdateReceivedd.get(userId);

        if(lastUpdate == null){
            return true;
        }

        if(lastUpdate != updateCount){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Stores the current update version for the user.
     * @param userId
     */
    public void updateUserVersion(String userId){
        this.lastUpdateReceivedd.put(userId, this.updateCount);
    }
}
