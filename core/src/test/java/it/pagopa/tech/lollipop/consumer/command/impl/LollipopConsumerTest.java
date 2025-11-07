/* (C)2025 */
package it.pagopa.tech.lollipop.consumer.command.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.tech.lollipop.consumer.assertion.AssertionService;
import it.pagopa.tech.lollipop.consumer.assertion.AssertionServiceFactory;
import it.pagopa.tech.lollipop.consumer.assertion.client.AssertionClient;
import it.pagopa.tech.lollipop.consumer.config.LollipopConsumerRequestConfig;
import it.pagopa.tech.lollipop.consumer.http_verifier.HttpMessageVerifier;
import it.pagopa.tech.lollipop.consumer.http_verifier.visma.VismaHttpMessageVerifierFactory;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.IdpCertSimpleClientConfig;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.storage.SimpleIdpCertStorage;
import it.pagopa.tech.lollipop.consumer.idp.impl.IdpCertProviderImpl;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.logger.LollipopLoggerService;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;
import it.pagopa.tech.lollipop.consumer.service.AssertionVerifierService;
import it.pagopa.tech.lollipop.consumer.service.HttpMessageVerifierService;
import it.pagopa.tech.lollipop.consumer.service.LollipopConsumerRequestValidationService;
import it.pagopa.tech.lollipop.consumer.service.impl.AssertionVerifierServiceImpl;
import it.pagopa.tech.lollipop.consumer.service.impl.HttpMessageVerifierServiceImpl;
import it.pagopa.tech.lollipop.consumer.service.impl.LollipopConsumerRequestValidationServiceImpl;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(
        classes = {LollipopConsumerRequestConfig.class, VismaHttpMessageVerifierFactory.class})
class LollipopConsumerTest {

    @Mock LollipopLoggerService logger;
    @Spy AssertionClient assertionClient;
    @Mock AssertionServiceFactory assertionServiceFactory;

    private AssertionService assertionService;

    @BeforeEach
    void setup() {
        reset(assertionServiceFactory, assertionClient);
        assertionService = mock(AssertionService.class);
    }

    @ParameterizedTest
    @MethodSource("getTestParameters")
    void doExecute_happyPath_returnsSuccessWithNames(
            String fileName,
            String expectedFirstLcOriginalMethod,
            String expectedFirstLcOriginalUrl,
            String assertionFileName)
            throws Exception {

        String assertionRef = "sha256-Iz4GEYGtznLdLyHrbtKEkzb6qSJpOkKvsOsCxgXkIhI";
        String jsonAssertionContent = getAssertionFromFile(assertionFileName);

        SamlAssertion samlAssertion = new SamlAssertion();
        samlAssertion.setAssertionRef(assertionRef);
        samlAssertion.setAssertionData(jsonAssertionContent);

        when(assertionService.getAssertion(anyString(), anyString())).thenReturn(samlAssertion);

        LollipopConsumerRequestConfig config =
                buildConfig(expectedFirstLcOriginalMethod, expectedFirstLcOriginalUrl);

        HttpMessageVerifier httpVerifier =
                new VismaHttpMessageVerifierFactory("UTF-8", config).create();

        HttpMessageVerifierService messageVerifierService =
                new HttpMessageVerifierServiceImpl(httpVerifier, config);

        LollipopConsumerRequestValidationService requestValidationService =
                new LollipopConsumerRequestValidationServiceImpl(config);

        IdpCertSimpleClientConfig idpCfg = IdpCertSimpleClientConfig.builder().build();

        Map<String, IdpCertData> idpCertDataMap = new ConcurrentHashMap<>();
        Map<String, ScheduledFuture<?>> scheduledEvictionsMap = new ConcurrentHashMap<>();
        IdpCertStorageConfig storageConfig = new IdpCertStorageConfig();
        SimpleIdpCertStorage idpStorage =
                new SimpleIdpCertStorage(idpCertDataMap, scheduledEvictionsMap, storageConfig);

        ApiClient apiClient = new ApiClient(idpCfg);
        IdpCertSimpleClient idpClient = new IdpCertSimpleClient(apiClient, idpCfg, idpStorage);
        IdpCertProvider idpProvider = new IdpCertProviderImpl(idpClient);

        AssertionVerifierService assertionVerifierService =
                new AssertionVerifierServiceImpl(logger, idpProvider, assertionService, config);

        Map<String, String> headers = getHeaders(fileName);

        Map<String, String[]> params = new HashMap<>();
        String body = "{\"example\": true}";

        LollipopConsumerRequest request =
                LollipopConsumerRequest.builder()
                        .headerParams(headers)
                        .requestParams(params)
                        .requestBody(body)
                        .build();

        LollipopConsumerCommandImpl lollipopConsumerCommand =
                new LollipopConsumerCommandImpl(
                        config,
                        messageVerifierService,
                        assertionVerifierService,
                        requestValidationService,
                        logger,
                        request);

        CommandResult result = lollipopConsumerCommand.doExecute();

        assertThat(result.getResultCode())
                .isEqualTo(LollipopConsumerCommandImpl.VERIFICATION_SUCCESS_CODE); // "SUCCESS"
        assertThat(result.getResultMessage()).isEqualTo("Verification completed successfully");
        assertThat(result.getName()).isEqualTo("Mario");
        assertThat(result.getFamilyName()).isEqualTo("Rossi");
    }

    private static Stream<Arguments> getTestParameters() {
        return Stream.of(
                Arguments.of("postRequest.json", "POST", ".*", "postAssertion.xml"),
                Arguments.of("getRequest.json", "GET", ".*", "getAssertion.xml"));
    }

    private LollipopConsumerRequestConfig buildConfig(
            String expectedFirstLcOriginalMethod, String expectedFirstLcOriginalUrl) {
        return LollipopConsumerRequestConfig.builder()
                .expectedFirstLcOriginalMethod(expectedFirstLcOriginalMethod)
                .expectedFirstLcOriginalUrl(expectedFirstLcOriginalUrl)
                .strictDigestVerify(false)
                .build();
    }

    private String getAssertionFromFile(String fileName) throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/" + fileName)) {
            if (is == null) throw new IllegalStateException("Assertion not found: " + fileName);
            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader =
                    new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }
            return textBuilder.toString();
        }
    }

    private Map<String, String> getHeaders(String fileName) {
        Map<String, String> headers = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream is = getClass().getResourceAsStream("/" + fileName)) {
            assertThat(is).as("postRequest.json not found in src/test/resources").isNotNull();

            JsonNode root = objectMapper.readTree(is);

            // 2) Costruisci headers
            JsonNode headersNode = root.get("headers");
            if (headersNode != null && headersNode.isObject()) {
                headersNode
                        .fields()
                        .forEachRemaining(e -> headers.put(e.getKey(), e.getValue().asText()));
            }

            // 3) Costruisci query params (opzionale)
            Map<String, String[]> params = new HashMap<>();
            JsonNode queryNode = root.get("query");
            if (queryNode != null && queryNode.isObject()) {
                queryNode
                        .fields()
                        .forEachRemaining(
                                e -> params.put(e.getKey(), new String[] {e.getValue().asText()}));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return headers;
    }
}
