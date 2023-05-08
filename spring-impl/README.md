# Lollipop SDK Spring Implementations

This module contains implementations of general utility of the core SDK, to be used in the context of a Spring
application.

## Configurations

An extension of the configuration class within the core module is provided, in order to enable property loading
through the Spring functionalities. Other configurations are expected to be provided from the application using 
this specific implementation (See the [spring-sample](../samples/spring) as a reference for a complete setup
of the other classes, as well as the application.properties file to be used).

## Interceptor Registry Example

In order to use the provided Http Interceptor it is required to register in the application context, providing
a configuration for the Spring application to use in order to determine which endpoints should be validated using
the library functionalities

```
@Component
public class SampleWebConfigurer implements WebMvcConfigurer {

    @Autowired
    private HttpVerifierHandlerInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/")
                .pathMatcher(new AntPathMatcher());
    }
}
```