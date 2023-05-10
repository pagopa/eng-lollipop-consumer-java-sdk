package it.pagopa.tech.lollipop.comsumer.sample;

import it.pagopa.tech.lollipop.comsumer.sample.mock.AssertionMockServerConfig;
import it.pagopa.tech.lollipop.comsumer.sample.mock.IdpMockServerConfig;
import it.pagopa.tech.lollipop.comsumer.sample.servlet.DemoServlet;
import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.assertion.client.AssertionClientProvider;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.AssertionSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.AssertionSimpleClientProvider;
import it.pagopa.tech.lollipop.consumer.assertion.impl.AssertionServiceFactoryImpl;
import it.pagopa.tech.lollipop.consumer.assertion.storage.AssertionStorageProvider;
import it.pagopa.tech.lollipop.consumer.assertion.storage.SimpleAssertionStorageProvider;
import it.pagopa.tech.lollipop.consumer.assertion.storage.StorageConfig;
import it.pagopa.tech.lollipop.consumer.command.impl.LollipopConsumerCommandBuilderImpl;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.exception.LollipopVerifierException;
import it.pagopa.tech.lollipop.consumer.helper.LollipopConsumerFactoryHelper;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.http_verifier.visma.VismaHttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProviderFactory;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClientProvider;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.storage.SimpleIdpCertStorageProvider;
import it.pagopa.tech.lollipop.consumer.idp.impl.IdpCertProviderFactoryImpl;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.logger.LollipopLoggerServiceFactory;
import it.pagopa.tech.lollipop.consumer.logger.impl.LollipopLogbackLoggerServiceFactory;
import it.pagopa.tech.lollipop.consumer.service.impl.LollipopConsumerRequestValidationServiceImpl;
import it.pagopa.tech.lollipop.consumer.servlet.HttpVerifierServletFilter;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.mockserver.integration.ClientAndServer;

import java.io.File;

public class LollipopConsumerServletSample {

    private static final String LOLLIPOP_CONSUMER_PATH = "/api/v1/lollipop-consumer";
    private static final String SERVLET_NAME = "DemoServlet";

    public static void main(String[] args) throws Exception {

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();
        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);

        FilterDef filterDef = buildFilterDef();
        context.addFilterDef(filterDef);
        context.addFilterMap(buildFilterMap(filterDef));

        Tomcat.addServlet(context, SERVLET_NAME, new DemoServlet());
        context.addServletMappingDecoded(LOLLIPOP_CONSUMER_PATH, SERVLET_NAME);

        tomcat.start();
        ClientAndServer.startClientAndServer(3001, 3000);
        AssertionMockServerConfig.startAssertionMockServer();
        IdpMockServerConfig.startIdpMockServer();
        tomcat.getServer().await();
    }

    private static FilterMap buildFilterMap(FilterDef filterDef) {
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(filterDef.getFilterName());
        filterMap.addURLPattern(LOLLIPOP_CONSUMER_PATH);
        return filterMap;
    }

    private static FilterDef buildFilterDef() throws LollipopVerifierException {
        FilterDef filterDef = new FilterDef();
        filterDef.setFilter(new HttpVerifierServletFilter(new LollipopConsumerCommandBuilderImpl(buildLollipopConsumerFactoryHelper())));
        filterDef.setFilterName("HttpVerifierServletFilter");
        return filterDef;
    }

    private static LollipopConsumerFactoryHelper buildLollipopConsumerFactoryHelper() throws LollipopVerifierException {
        LollipopConsumerRequestConfig lollipopConsumerRequestConfig = LollipopConsumerRequestConfig.builder()
                .assertionNotBeforeDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .assertionInstantDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .build();
        HttpMessageVerifierFactory messageVerifierFactory = new VismaHttpMessageVerifierFactory("UTF-8",
                LollipopConsumerRequestConfig.builder().build());
        AssertionStorageProvider assertionStorageProvider = new SimpleAssertionStorageProvider();
        IdpCertProviderFactory idpCertProviderFactory = new IdpCertProviderFactoryImpl(
                new IdpCertSimpleClientProvider(IdpCertSimpleClientConfig.builder().baseUri("http://localhost:3001").build(),
                        new SimpleIdpCertStorageProvider(), new IdpCertStorageConfig()));
        AssertionClientProvider assertionClientProvider =
                new AssertionSimpleClientProvider(AssertionSimpleClientConfig.builder().build());
        AssertionServiceFactory assertionServiceFactory = new AssertionServiceFactoryImpl(
                assertionStorageProvider, assertionClientProvider, new StorageConfig());
        LollipopLoggerServiceFactory lollipopLoggerServiceFactory = new LollipopLogbackLoggerServiceFactory();
        return new LollipopConsumerFactoryHelper(lollipopLoggerServiceFactory, messageVerifierFactory, idpCertProviderFactory, assertionServiceFactory,
                new LollipopConsumerRequestValidationServiceImpl(lollipopConsumerRequestConfig), lollipopConsumerRequestConfig);
    }
}
