#Spring Boot Metrics App 

**Metrics**is a Java library which gives you unparalleled insight into what your code does in production.
Metrics provides a powerful toolkit of ways to measure the behavior of critical components in your production environment.

With modules for common libraries like **Jetty, Logback, Log4j, Apache HttpClient, Ehcache, JDBI, Jersey** and reporting 
backends like **Ganglia** and **Graphite**, Metrics provides you with full-stack visibility.[More](http://metrics.dropwizard.io)

The metrics-spring module integrates Dropwizard Metrics library with Spring, and provides XML and Java configuration.
For instance, you can enable Metrics by following java config file:   

```java

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
        filterRegistration.addUrlPatterns("/");
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
}

```

After these you can easily inject **MetricRegistry** to any component where you would like to use it: 

```java

@Autowired
private MetricRegistry metricRegistry;

```