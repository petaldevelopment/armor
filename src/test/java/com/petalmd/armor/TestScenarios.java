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

import org.elasticsearch.common.settings.Settings;
import org.junit.Test;

import com.petalmd.armor.util.ConfigConstants;
import com.petalmd.armor.util.SecurityUtil;

public class TestScenarios extends AbstractScenarioTest {

    @Test
    public void testSearchOnlyAllowedMoreFilters() throws Exception {

        final boolean wrongPassword = false;
        username = "jacksonm";
        password = "secret";

        searchOnlyAllowedMoreFilters(getAuthSettings(wrongPassword, "ceo"), wrongPassword);
    }

    @Test
    public void testSimpleDlsScenario() throws Exception {

        final boolean wrongPassword = false;
        username = "jacksonm";
        password = "secret";

        simpleDlsScenario(getAuthSettings(wrongPassword, "ceo"));
    }

    @Test
    public void testSimpleFlsScenarioInclude() throws Exception {

        final boolean wrongPassword = false;
        username = "jacksonm";
        password = "secret";

        simpleFlsScenarioInclude(getAuthSettings(wrongPassword, "ceo"));
    }

    @Test
    public void testSimpleFlsScenarioExclude() throws Exception {

        final boolean wrongPassword = false;
        username = "jacksonm";
        password = "secret";

        simpleFlsScenarioExclude(getAuthSettings(wrongPassword, "ceo"));
    }

    @Test
    public void testSimpleFlsScenarioFields() throws Exception {

        final boolean wrongPassword = false;
        username = "jacksonm";
        password = "secret";

        simpleFlsScenarioFields(getAuthSettings(wrongPassword, "ceo"));
    }

    @Test
    public void testSearchOnlyAllowedActionSessionsEnabled() throws Exception {

        final boolean wrongPassword = false;
        username = "jacksonm";
        password = "secret";

        final Settings settings = Settings.builder().put(getAuthSettings(wrongPassword, "ceo"))
                .put(ConfigConstants.ARMOR_HTTP_ENABLE_SESSIONS, true).build();

        searchOnlyAllowedAction(settings, wrongPassword);
    }

    @Test
    public void testSearchOnlyAllowedAction() throws Exception {

        final boolean wrongPassword = false;
        username = "jacksonm";
        password = "secret";

        searchOnlyAllowedAction(getAuthSettings(wrongPassword, "ceo"), wrongPassword);
    }

    @Test
    public void testSearchOnlyAllowedActionFail() throws Exception {

        final boolean wrongPassword = true;
        username = "jacksonm";
        password = "secret";

        searchOnlyAllowedAction(getAuthSettings(wrongPassword, "ceo"), wrongPassword);
    }

    @Test
    public void testDlsLdapUserAttribute() throws Exception {
        startLDAPServer();
        ldapServer.applyLdif(SecurityUtil.getAbsoluteFilePathFromClassPath("ldif1.ldif"));

        final Settings settings = Settings
                .settingsBuilder()
                .put("armor.authentication.authorizer.impl", "com.petalmd.armor.authorization.ldap.LDAPAuthorizator")
                .put("armor.authentication.authorizer.cache.enable", "true")
                .put("armor.authentication.authentication_backend.impl",
                        "com.petalmd.armor.authentication.backend.ldap.LDAPAuthenticationBackend")
                .put("armor.authentication.authentication_backend.cache.enable", "true")
                .putArray("armor.authentication.ldap.host", "localhost:" + ldapServerPort)
                .put("armor.authentication.ldap.usersearch", "(uid={0})")
                .put("armor.authentication.ldap.username_attribute", "uid")
                .put("armor.authentication.authorization.ldap.rolesearch", "(uniqueMember={0})")
                .put("armor.authentication.authorization.ldap.rolename", "cn")
                .build();

        dlsLdapUserAttribute(settings);
    }
}
