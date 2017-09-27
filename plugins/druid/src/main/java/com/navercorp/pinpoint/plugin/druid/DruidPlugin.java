package com.navercorp.pinpoint.plugin.druid;

import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentException;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentMethod;
import com.navercorp.pinpoint.bootstrap.instrument.Instrumentor;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformCallback;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplate;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplateAware;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext;
import com.navercorp.pinpoint.bootstrap.plugin.util.InstrumentUtils;

import java.security.ProtectionDomain;

/**
 * @author Jason Zhang
 */
public class DruidPlugin implements ProfilerPlugin, TransformTemplateAware {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    private DruidConfig config;

    private TransformTemplate transformTemplate;

    @Override
    public void setup(ProfilerPluginSetupContext context) {
        config = new DruidConfig(context.getConfig());
        if (!config.isPluginEnable()) {
            logger.info("Disable druid option. 'profiler.jdbc.druid=false'");
            return;
        }

        addDruidDataSourceTransformer();
        if (config.isProfileClose()) {
            addPoolConnectionTransformer();
        }
    }

    private void addPoolConnectionTransformer() {
        transformTemplate.transform("com.alibaba.druid.pool.DruidPooledConnection", new TransformCallback() {

            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);

                // closeMethod
                InstrumentMethod closeMethod = InstrumentUtils.findMethod(target, "close");
                closeMethod.addScopedInterceptor(DruidConstants.INTERCEPTOR_CLOSE_CONNECTION, DruidConstants.SCOPE);

                return target.toBytecode();
            }
        });
    }

    private void addDruidDataSourceTransformer() {
        transformTemplate.transform("com.alibaba.druid.pool.DruidDataSource", new TransformCallback() {
            
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);

                if (isAvailableDataSourceMonitor(target)) {
                    logger.info("[DRUID] available");
                    target.addField(DruidConstants.ACCESSOR_DATASOURCE_MONITOR);

                    // default constructor
                    InstrumentMethod defaultConstructor = InstrumentUtils.findConstructor(target);
                    defaultConstructor.addScopedInterceptor(DruidConstants.INTERCEPTOR_CONSTRUCTOR, DruidConstants.SCOPE);
                    InstrumentMethod oneArgContructor = InstrumentUtils.findConstructor(target, new String[]{"boolean"});
                    oneArgContructor.addScopedInterceptor(DruidConstants.INTERCEPTOR_CONSTRUCTOR, DruidConstants.SCOPE);

                    // closeMethod
                    InstrumentMethod closeMethod = InstrumentUtils.findMethod(target, "close");
                    closeMethod.addScopedInterceptor(DruidConstants.INTERCEPTOR_CLOSE, DruidConstants.SCOPE);
                }

                // getConnectionMethod
                InstrumentMethod getConnectionMethod = InstrumentUtils.findMethod(target, "getConnection");
                getConnectionMethod.addScopedInterceptor(DruidConstants.INTERCEPTOR_GET_CONNECTION, DruidConstants.SCOPE);
                getConnectionMethod = InstrumentUtils.findMethod(target, "getConnection", new String[]{"java.lang.String", "java.lang.String"});
                getConnectionMethod.addScopedInterceptor(DruidConstants.INTERCEPTOR_GET_CONNECTION, DruidConstants.SCOPE);
                getConnectionMethod = InstrumentUtils.findMethod(target, "getConnection", new String[]{"long"});
                getConnectionMethod.addScopedInterceptor(DruidConstants.INTERCEPTOR_GET_CONNECTION, DruidConstants.SCOPE);

                return target.toBytecode();
            }
        });
    }

    private boolean isAvailableDataSourceMonitor(InstrumentClass target) {
        boolean hasMethod = target.hasMethod("getUrl");
        if (!hasMethod) {
            return false;
        }

        hasMethod = target.hasMethod("getActiveCount");
        if (!hasMethod) {
            return false;
        }

        hasMethod = target.hasMethod("getMaxActive");
        return hasMethod;
    }

    @Override
    public void setTransformTemplate(TransformTemplate transformTemplate) {
        this.transformTemplate = transformTemplate;
    }
}
