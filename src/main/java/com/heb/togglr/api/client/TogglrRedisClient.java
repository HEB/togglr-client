package com.heb.togglr.api.client;

import com.heb.togglr.api.client.cache.redis.TogglrRedisCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        value = "heb.togglr.client.cache-type",
        havingValue = "redis",
        matchIfMissing = true)
public class TogglrRedisClient extends TogglrClient {

    public TogglrRedisClient(TogglrRedisCache togglrCache) {
        super(togglrCache);
    }
}
