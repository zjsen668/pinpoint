/*
 * Copyright 2016 NAVER Corp.
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
 */

package com.navercorp.pinpoint.plugin.druid;

import com.navercorp.pinpoint.bootstrap.config.DefaultProfilerConfig;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * @author Jason Zhang
 */
public class DruidConfigTest {

    @Test
    public void configTest1() throws Exception {
        com.navercorp.pinpoint.plugin.druid.DruidConfig druidConfig = createDruidConfig("false", "false");

        Assert.assertFalse(druidConfig.isPluginEnable());
        Assert.assertFalse(druidConfig.isProfileClose());
    }

    @Test
    public void configTest2() throws Exception {
        com.navercorp.pinpoint.plugin.druid.DruidConfig druidConfig = createDruidConfig("false", "true");

        Assert.assertFalse(druidConfig.isPluginEnable());
        Assert.assertTrue(druidConfig.isProfileClose());
    }

    @Test
    public void configTest3() throws Exception {
        com.navercorp.pinpoint.plugin.druid.DruidConfig druidConfig = createDruidConfig("true", "false");

        Assert.assertTrue(druidConfig.isPluginEnable());
        Assert.assertFalse(druidConfig.isProfileClose());
    }

    @Test
    public void configTest4() throws Exception {
        com.navercorp.pinpoint.plugin.druid.DruidConfig druidConfig = createDruidConfig("true", "true");

        Assert.assertTrue(druidConfig.isPluginEnable());
        Assert.assertTrue(druidConfig.isProfileClose());
    }

    private com.navercorp.pinpoint.plugin.druid.DruidConfig createDruidConfig(String pluginEnable, String profileConnectionCloseEnable) {
        Properties properties = new Properties();
        properties.put(com.navercorp.pinpoint.plugin.druid.DruidConfig.DRUID_PLUGIN_ENABLE, pluginEnable);
        properties.put(com.navercorp.pinpoint.plugin.druid.DruidConfig.DRUID_PROFILE_CONNECTIONCLOSE_ENABLE, profileConnectionCloseEnable);

        ProfilerConfig profilerConfig = new DefaultProfilerConfig(properties);

        return new com.navercorp.pinpoint.plugin.druid.DruidConfig(profilerConfig);
    }

}
