/*
 * Copyright 2017 NAVER Corp.
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

package com.navercorp.pinpoint.plugin.druid.interceptor;

import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.DataSourceMonitorRegistry;
import com.navercorp.pinpoint.plugin.druid.DataSourceMonitorAccessor;
import com.navercorp.pinpoint.plugin.druid.DruidDataSourceMonitor;

/**
 * @author Jason Zhang
 */
public class DataSourceCloseInterceptor implements AroundInterceptor {

    private final TraceContext traceContext;
    private final DataSourceMonitorRegistry dataSourceMonitorRegistry;
    private final MethodDescriptor methodDescriptor;

    public DataSourceCloseInterceptor(TraceContext traceContext, DataSourceMonitorRegistry dataSourceMonitorRegistry, MethodDescriptor methodDescriptor) {
        this.traceContext = traceContext;
        this.dataSourceMonitorRegistry = dataSourceMonitorRegistry;
        this.methodDescriptor = methodDescriptor;
    }

    @Override
    public void before(Object target, Object[] args) {
        if ((target instanceof DataSourceMonitorAccessor)) {
            DruidDataSourceMonitor dataSourceMonitor = ((DataSourceMonitorAccessor) target)._$PINPOINT$_getDataSourceMonitor();

            if (dataSourceMonitor != null) {
                ((DataSourceMonitorAccessor) target)._$PINPOINT$_setDataSourceMonitor(null);
                dataSourceMonitor.close();
                dataSourceMonitorRegistry.unregister(dataSourceMonitor);
            }
        }
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {

    }

}
