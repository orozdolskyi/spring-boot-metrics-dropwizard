package com.rozdolskyi.springbootmetricsdropwizard.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.logback.InstrumentedAppender;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlet.InstrumentedFilterContextListener;
import com.codahale.metrics.servlets.AdminServlet;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
@EnableMetrics
public class MetricConfiguration {

    @Bean
    public MetricRegistry metricRegistry() {
        MetricRegistry metricRegistry = new MetricRegistry();
        registerMetricsLogger(metricRegistry);
        return metricRegistry;
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        return new ServletRegistrationBean(new AdminServlet(), "/metrics/*");
    }

    @Bean
    public MetricsServlet.ContextListener metricsServletListener() {
        return new MetricsServlet.ContextListener() {
            @Override
            protected MetricRegistry getMetricRegistry() {
                return metricRegistry();
            }
        };
    }

    @Bean
    public FilterRegistrationBean instrumentedFilterRegistration() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        Filter instrumentedFilter = new InstrumentedFilter();
        filterRegistration.setFilter(instrumentedFilter);
        filterRegistration.addUrlPatterns("/hello");
        return filterRegistration;
    }

    @Bean
    public InstrumentedFilterContextListener instrumentedFilterContextListener() {
        return new InstrumentedFilterContextListener() {
            @Override
            protected MetricRegistry getMetricRegistry() {
                return metricRegistry();
            }
        };
    }

    @Bean
    public HealthCheckServlet.ContextListener healthCheckServletListener() {
        return new HealthCheckServlet.ContextListener() {
            @Override
            protected HealthCheckRegistry getHealthCheckRegistry() {
                HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();
                healthCheckRegistry.register("endpoint", new HealthCheck() {
                    @Override
                    protected Result check() throws Exception {
                        return Result.healthy();
                    }
                });
                return healthCheckRegistry;
            }
        };
    }

    private void registerMetricsLogger(MetricRegistry metricRegistry) {
        LoggerContext factory = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger root = factory.getLogger(Logger.ROOT_LOGGER_NAME);

        InstrumentedAppender metricsAppender = new InstrumentedAppender(metricRegistry);
        metricsAppender.setContext(root.getLoggerContext());
        metricsAppender.start();

        root.addAppender(metricsAppender);
    }
}
