# Lollipop SDK Servlet Implementations

This module contains implementations of general utility of the core SDK with a servlet filter.

## Servlet filter Example

In order to use the provided Servlet filter it is possible to register it in the application context, providing
a configuration for the Spring application to use in order to determine which endpoints should be validated using
the library functionalities

```
@Configuration
public class DemoWebConfigurer implements WebMvcConfigurer {

    @Autowired private LollipopConsumerCommandBuilder commandBuilder;

    @Bean
    public FilterRegistrationBean<HttpVerifierServletFilter> requestFilter() {
        FilterRegistrationBean<HttpVerifierServletFilter> registrationBean =
                new FilterRegistrationBean<>();

        registrationBean.setFilter(new HttpVerifierServletFilter(commandBuilder));
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }
}
```

Otherwise, it is possible to register the filter together with a servlet in a web server like Tomcat as shown in the [Servlet sample](samples/servlet)