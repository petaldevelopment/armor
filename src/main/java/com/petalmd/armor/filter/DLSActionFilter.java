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

package com.petalmd.armor.filter;

import com.petalmd.armor.authentication.backend.AuthenticationBackend;
import com.petalmd.armor.authorization.Authorizator;
import com.petalmd.armor.util.ConfigConstants;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.ActionFilterChain;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;

import java.util.*;
import java.util.Map.Entry;

public class DLSActionFilter extends AbstractActionFilter {

    private final String filterType = "dlsfilter";
    private final Map<String, List<String>> filterMap = new HashMap<String, List<String>>();
    private final Client client;
    protected final boolean rewriteGetAsSearch;

    @Inject
    public DLSActionFilter(final Settings settings, final Client client, final AuthenticationBackend backend,
            final Authorizator authorizator, final ClusterService clusterService) {
        super(settings, backend, authorizator, clusterService);
        this.client = client;

        final String[] arFilters = settings.getAsArray(ConfigConstants.ARMOR_DLSFILTER);
        for (int i = 0; i < arFilters.length; i++) {
            final String filterName = arFilters[i];

            final List<String> filters = Arrays.asList(settings.getAsArray("armor." + filterType + "." + filterName, new String[0]));

            filterMap.put(filterName, filters);
        }

        this.rewriteGetAsSearch = settings.getAsBoolean(ConfigConstants.ARMOR_REWRITE_GET_AS_SEARCH, true);
    }

    @Override
    public void applySecure(final String action, final ActionRequest request, final ActionListener listener, final ActionFilterChain chain) {

        if (filterMap.size() == 0) {
            chain.proceed(action, request, listener);
            return;
        }

        if (request instanceof SearchRequest || request instanceof MultiSearchRequest || request instanceof GetRequest
                || request instanceof MultiGetRequest) {

            for (final Iterator<Entry<String, List<String>>> it = filterMap.entrySet().iterator(); it.hasNext();) {

                final Entry<String, List<String>> entry = it.next();

                final String filterName = entry.getKey();
                final List<String> filters = entry.getValue();

                if (request.hasInContext("armor_filter") && filterType != null) {
                    if (!((List<String>) request.getFromContext("armor_filter")).contains(filterType + ":" + filterName)) {
                        ((List<String>) request.getFromContext("armor_filter")).add(filterType + ":" + filterName);
                    }

                } else if (filterType != null) {
                    final List<String> _filters = new ArrayList<String>();
                    _filters.add(filterType + ":" + filterName);
                    request.putInContext("armor_filter", _filters);
                }

                request.putInContext("armor." + filterType + "." + filterName + ".filters", filters);

                log.trace("armor." + filterType + "." + filterName + ".filters {}", filters);

                if (rewriteGetAsSearch && request instanceof GetRequest) {

                    log.debug("Rewrite GetRequest as SearchRequest");

                    this.doGet((GetRequest) request, listener, client);

                    return;

                }

                if (rewriteGetAsSearch && request instanceof MultiGetRequest) {
                    log.debug("Rewrite MultiGetRequest as MultiSearchRequest");

                    this.doGet((MultiGetRequest) request, listener, client);

                    return;

                }
            }
        }

        chain.proceed(action, request, listener);
    }

}
