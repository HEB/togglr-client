package com.heb.togglr.api.client;

import com.heb.togglr.api.client.cache.memory.TogglrInMemoryCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        value = "heb.togglr.client.cache-type",
        havingValue = "in-memory",
        matchIfMissing = true)
public class TogglrInMemoryClient extends TogglrClient {


    public TogglrInMemoryClient(TogglrInMemoryCache togglrCache) {
        super(togglrCache);
    }

}
