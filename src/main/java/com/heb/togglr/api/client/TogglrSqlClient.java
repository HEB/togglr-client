package com.heb.togglr.api.client;

import com.heb.togglr.api.client.cache.sql.TogglrSqlCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;


/**
 * A Togglr Client that uses a Sql backend.
 */
@Service
@ConditionalOnProperty(
        value = "heb.togglr.client.cache-type",
        havingValue = "sql",
        matchIfMissing = true)
public class TogglrSqlClient extends TogglrClient {

    public TogglrSqlClient(TogglrSqlCache togglrCache) {
        super(togglrCache);
    }

}
