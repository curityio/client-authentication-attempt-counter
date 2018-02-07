/*
 *  Copyright 2018 Curity AB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.curity.identityserver.plugin.events.listeners;

import io.curity.identityserver.plugin.events.listeners.config.EventListenerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.data.events.FailureClientAuthenticationEvent;
import se.curity.identityserver.sdk.event.EventListener;
import se.curity.identityserver.sdk.service.Bucket;

import java.util.Collections;
import java.util.Map;

import static io.curity.identityserver.plugin.events.listeners.util.Constants.CLIENT_AUTH_COUNTER_PURPOSE;
import static io.curity.identityserver.plugin.events.listeners.util.Constants.FAIL_COUNTER_ATTRIBUTE_NAME;

public class ClientAuthFailureListener implements EventListener<FailureClientAuthenticationEvent>
{
    private static final Logger _logger = LoggerFactory.getLogger(ClientAuthFailureListener.class);

    private final EventListenerConfiguration _configuration;

    public ClientAuthFailureListener(EventListenerConfiguration configuration)
    {
        _configuration = configuration;
    }

    @Override
    public Class<FailureClientAuthenticationEvent> getEventType()
    {
        return FailureClientAuthenticationEvent.class;
    }

    @Override
    public void handle(FailureClientAuthenticationEvent event)
    {
        String clientId = event.getClientId();

        _logger.trace("Incrementing authentication attempt counter after failed authentication of client {}", clientId);

        Bucket bucket = _configuration.getBucket();
        Map<String, Object> attributes = bucket.getAttributes(clientId, CLIENT_AUTH_COUNTER_PURPOSE);

        int incrementedCounter = nullToZero((Integer) attributes.get(FAIL_COUNTER_ATTRIBUTE_NAME)) + 1;

        if (incrementedCounter > 1)
        {
            if (incrementedCounter < 10)
            {
                _logger.info("{} consecutive failed authentication attempts for client {}",
                        incrementedCounter, clientId);
            }
            else
            {
                _logger.warn("{} consecutive failed authentication attempts for client {}",
                        incrementedCounter, clientId);
            }
        }

        bucket.storeAttributes(clientId, CLIENT_AUTH_COUNTER_PURPOSE,
                Collections.singletonMap(FAIL_COUNTER_ATTRIBUTE_NAME, incrementedCounter));
    }

    private static int nullToZero(@Nullable Integer value)
    {
        return value == null ? 0 : value;
    }
}