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

package com.petalmd.armor;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.NoHttpResponseException;
import org.elasticsearch.common.settings.Settings;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.petalmd.armor.util.SecurityUtil;

public class SslTest extends AbstractScenarioTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testHttps() throws Exception {

        enableSSL = true;

        final Settings settings = Settings
                .settingsBuilder()
                .putArray("armor.authentication.authorization.settingsdb.roles.jacksonm", "ceo")
                .put("armor.authentication.settingsdb.user.jacksonm", "secret")
                .put("armor.authentication.authorizer.impl",
                        "com.petalmd.armor.authorization.simple.SettingsBasedAuthorizator")
                        .put("armor.authentication.authorizer.cache.enable", "false")
                        .put("armor.authentication.authentication_backend.impl",
                                "com.petalmd.armor.authentication.backend.simple.SettingsBasedAuthenticationBackend")
                                .put("armor.authentication.authentication_backend.cache.enable", "false")
                                .put("armor.ssl.transport.http.enabled", true)
                .put("armor.ssl.transport.http.enforce_clientauth", true)
                                .put("armor.ssl.transport.http.keystore_filepath", SecurityUtil.getAbsoluteFilePathFromClassPath("ArmorKS.jks"))
                                .put("armor.ssl.transport.http.truststore_filepath",
                        SecurityUtil.getAbsoluteFilePathFromClassPath("ArmorTS.jks")).build();

        username = "jacksonm";
        password = "secret";

        searchOnlyAllowed(settings, false);
    }

    @Test
    public void testHttpsFailSSLv3() throws Exception {
        thrown.expect(SSLHandshakeException.class);

        enableSSL = true;
        enableSSLv3Only = true;

        final Settings settings = Settings
                .settingsBuilder()
                .putArray("armor.authentication.authorization.settingsdb.roles.jacksonm", "ceo")
                .put("armor.authentication.settingsdb.user.jacksonm", "secret")
                .put("armor.authentication.authorizer.impl",
                        "com.petalmd.armor.authorization.simple.SettingsBasedAuthorizator")
                        .put("armor.authentication.authorizer.cache.enable", "false")
                        .put("armor.authentication.authentication_backend.impl",
                                "com.petalmd.armor.authentication.backend.simple.SettingsBasedAuthenticationBackend")
                                .put("armor.authentication.authentication_backend.cache.enable", "false")
                                .put("armor.ssl.transport.http.enabled", true)
                .put("armor.ssl.transport.http.enforce_clientauth", true)
                                .put("armor.ssl.transport.http.keystore_filepath", SecurityUtil.getAbsoluteFilePathFromClassPath("ArmorKS.jks"))
                                .put("armor.ssl.transport.http.truststore_filepath",
                        SecurityUtil.getAbsoluteFilePathFromClassPath("ArmorTS.jks")).build();

        username = "jacksonm";
        password = "secret";

        searchOnlyAllowed(settings, false);
    }

    @Test
    public void testHttpsFail() throws Exception {
        thrown.expect(NoHttpResponseException.class);

        enableSSL = false;

        final Settings settings = Settings
                .settingsBuilder()
                .putArray("armor.authentication.authorization.settingsdb.roles.jacksonm", "ceo")
                .put("armor.authentication.settingsdb.user.jacksonm", "secret")
                .put("armor.authentication.authorizer.impl",
                        "com.petalmd.armor.authorization.simple.SettingsBasedAuthorizator")
                        .put("armor.authentication.authorizer.cache.enable", "false")
                        .put("armor.authentication.authentication_backend.impl",
                                "com.petalmd.armor.authentication.backend.simple.SettingsBasedAuthenticationBackend")
                                .put("armor.authentication.authentication_backend.cache.enable", "false")
                                .put("armor.ssl.transport.http.enabled", true)
                .put("armor.ssl.transport.http.enforce_clientauth", true)
                                .put("armor.ssl.transport.http.keystore_filepath", SecurityUtil.getAbsoluteFilePathFromClassPath("ArmorKS.jks"))
                                .put("armor.ssl.transport.http.truststore_filepath",
                        SecurityUtil.getAbsoluteFilePathFromClassPath("ArmorTS.jks")).build();

        username = "jacksonm";
        password = "secret";

        searchOnlyAllowed(settings, false);
    }

    @Test
    public void testNodeSSL() throws Exception {

        final Settings settings = Settings
                .settingsBuilder()
                .putArray("armor.authentication.authorization.settingsdb.roles.jacksonm", "ceo")
                .put("armor.authentication.settingsdb.user.jacksonm", "secret")
                .put("armor.authentication.authorizer.impl",
                        "com.petalmd.armor.authorization.simple.SettingsBasedAuthorizator")
                        .put("armor.authentication.authorizer.cache.enable", "false")
                        .put("armor.authentication.authentication_backend.impl",
                                "com.petalmd.armor.authentication.backend.simple.SettingsBasedAuthenticationBackend")
                                .put("armor.authentication.authentication_backend.cache.enable", "false")
                                .put("armor.ssl.transport.node.enabled", true)
                .put("armor.ssl.transport.node.enforce_clientauth", true)
                                .put("armor.ssl.transport.node.keystore_filepath", SecurityUtil.getAbsoluteFilePathFromClassPath("ArmorKS.jks"))
                                .put("armor.ssl.transport.node.truststore_filepath",
                        SecurityUtil.getAbsoluteFilePathFromClassPath("ArmorTS.jks"))
                                .put("armor.ssl.transport.node.encforce_hostname_verification", false).build();

        username = "jacksonm";
        password = "secret";

        searchOnlyAllowed(settings, false);
    }

    @Test
    public void mutualSSLAuthentication() throws Exception {

        enableSSL = true;

        final Settings settings = Settings
                .settingsBuilder()
                .put("armor.authentication.http_authenticator.impl",
                    "com.petalmd.armor.authentication.http.clientcert.HTTPSClientCertAuthenticator")
                .putArray("armor.authentication.authorization.settingsdb.roles.localhost", "ceo")
                .put("armor.authentication.authorizer.impl",
                    "com.petalmd.armor.authorization.simple.SettingsBasedAuthorizator")
                .put("armor.authentication.authorizer.cache.enable", "false")
                .put("armor.authentication.authentication_backend.impl",
                    "com.petalmd.armor.authentication.backend.simple.AlwaysSucceedAuthenticationBackend")
                .put("armor.authentication.authentication_backend.cache.enable", "false")
                .put("armor.ssl.transport.http.enabled", true)
                .put("armor.ssl.transport.http.enforce_clientauth", true)
                .put("armor.ssl.transport.http.keystore_filepath",
                    SecurityUtil.getAbsoluteFilePathFromClassPath("ArmorKS.jks"))
                .put("armor.ssl.transport.http.truststore_filepath",
                    SecurityUtil.getAbsoluteFilePathFromClassPath("ArmorTS.jks"))
                .build();

        searchOnlyAllowed(settings, false);
    }
}
