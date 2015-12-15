/*
 * Copyright 2015 PetalMD
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.petalmd.armor.rest;

import com.petalmd.armor.audit.AuditListener;
import com.petalmd.armor.service.ArmorService;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestFilterChain;
import org.elasticsearch.rest.RestHandler;
import org.elasticsearch.rest.RestRequest;

import java.util.Arrays;
import java.util.List;

public class RestActionFilter extends AbstractACRestFilter {

    protected final List<String> allowedActions;
    protected final List<String> forbiddenActions;

    public RestActionFilter(final ArmorService service, final String filterType, final String filterName,
            final AuditListener auditListener) {
        super(service, filterType, filterName, auditListener);
        allowedActions = Arrays.asList(settings.getAsArray("armor." + filterType + "." + filterName + ".allowed_actions",
                new String[0]));
        forbiddenActions = Arrays.asList(settings.getAsArray("armor." + filterType + "." + filterName + ".forbidden_actions",
                new String[0]));

    }

    @Override
    public void processSecure(final RestRequest request, final RestChannel channel, final RestFilterChain filterChain) throws Exception {
        //populate params and determine request handler
        final RestHandler handler = service.getHandler(request);
        final String[] handlerClassNameSplits = handler.getClass().getName().split("\\.");
        final String simpleClassName = handlerClassNameSplits[handlerClassNameSplits.length - 1];

        request.putInContext("armor." + filterType + "." + filterName + ".allowed_actions", allowedActions);
        request.putInContext("armor." + filterType + "." + filterName + ".forbidden_actions", forbiddenActions);
        request.putInContext("armor." + filterType + "." + filterName + ".class_name", simpleClassName);

        filterChain.continueProcessing(request, channel);

    }
}
