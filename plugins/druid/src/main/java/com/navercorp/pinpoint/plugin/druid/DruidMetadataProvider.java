package com.navercorp.pinpoint.plugin.druid;

import com.navercorp.pinpoint.common.trace.TraceMetadataProvider;
import com.navercorp.pinpoint.common.trace.TraceMetadataSetupContext;

/**
 * @author Jason Zhang
 */
public class DruidMetadataProvider implements TraceMetadataProvider {
    /**
     * @see TraceMetadataProvider#setup(TraceMetadataSetupContext)
     */
    @Override
    public void setup(TraceMetadataSetupContext context) {
        context.addServiceType(DruidConstants.SERVICE_TYPE);
    }
}
