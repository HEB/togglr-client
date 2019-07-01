package com.heb.togglr.api.client.service;


import com.heb.togglr.api.client.exception.RedisException;

public interface TogglrUpdateNotifier {

    public void registerNewUpdate() throws RedisException;

    public boolean doesClientNeedUpdate(String cacheId);

    public void updateUserVersion(String cacheId);

    public void clearCache();
}