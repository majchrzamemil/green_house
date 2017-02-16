package box;

import box.config.DefaultProfileUtil;
import box.repository.GreenHouseManagerRepository;
import box.service.AuditEventService;
import box.service.impl.GreenHouseManagerServiceImpl;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import box.service.GreenHouseManagerService;

/**
 * This is a helper Java class that provides an alternative to creating a web.xml.
 * This will be invoked only when the application is deployed to a servlet container like Tomcat, JBoss etc.
 */
public class ApplicationWebXml extends SpringBootServletInitializer {
    
    @Bean
    public GreenHouseManagerService greenHouseManagerServiceService() {
        return new GreenHouseManagerServiceImpl();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        /**
         * set a default to use when no profile is configured.
         */

        DefaultProfileUtil.addDefaultProfile(application.application());
        return application.sources(GreenHouseApp.class);
    }
}
