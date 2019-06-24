package com.heb.togglr.api.client.service;



public interface TogglrUpdateNotifier {

    public void registerNewUpdate();

    public boolean doesClientNeedUpdate(String cacheId);

    public void updateUserVersion(String cacheId);

    public void clearCache();
}