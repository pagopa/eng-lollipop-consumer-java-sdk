/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.servlet.config;

import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommandBuilder;
import it.pagopa.tech.lollipop.consumer.servlet.HttpVerifierServletFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class DemoWebConfigurer implements WebMvcConfigurer {

    @Autowired private LollipopConsumerCommandBuilder commandBuilder;

    @Bean
    public CommonsRequestLoggingFilter loggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludeClientInfo(true);
        filter.setIncludeHeaders(true);
        filter.setIncludePayload(true);
        return filter;
    }

    @Bean
    public FilterRegistrationBean<HttpVerifierServletFilter> requestFilter() {
        FilterRegistrationBean<HttpVerifierServletFilter> registrationBean =
                new FilterRegistrationBean<>();

        registrationBean.setFilter(new HttpVerifierServletFilter(commandBuilder));
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }
}
