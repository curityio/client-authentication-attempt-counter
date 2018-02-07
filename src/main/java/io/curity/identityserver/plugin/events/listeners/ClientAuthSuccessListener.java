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
import se.curity.identityserver.sdk.data.events.SuccessClientAuthenticationEvent;
import se.curity.identityserver.sdk.event.EventListener;

import java.util.Collections;

import static io.curity.identityserver.plugin.events.listeners.util.Constants.CLIENT_AUTH_COUNTER_PURPOSE;
import static io.curity.identityserver.plugin.events.listeners.util.Constants.FAIL_COUNTER_ATTRIBUTE_NAME;

public class ClientAuthSuccessListener implements EventListener<SuccessClientAuthenticationEvent>
{
    private static final Logger _logger = LoggerFactory.getLogger(ClientAuthSuccessListener.class);

    private final EventListenerConfiguration _configuration;

    public ClientAuthSuccessListener(EventListenerConfiguration configuration)
    {
        _configuration = configuration;
    }

    @Override
    public Class<SuccessClientAuthenticationEvent> getEventType()
    {
        return SuccessClientAuthenticationEvent.class;
    }

    @Override
    public void handle(SuccessClientAuthenticationEvent event)
    {
        String clientId = event.getClientId();

        _logger.trace("Resetting authentication attempt counter after successful authentication of client {}", clientId);

        _configuration.getBucket().storeAttributes(clientId, CLIENT_AUTH_COUNTER_PURPOSE,
                Collections.singletonMap(FAIL_COUNTER_ATTRIBUTE_NAME, 0));
    }
}