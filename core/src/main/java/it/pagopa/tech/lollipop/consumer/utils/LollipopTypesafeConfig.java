/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.utils;

import com.typesafe.config.Config;
import lombok.Getter;

@Getter
public class LollipopTypesafeConfig {

    boolean ASSERTION_CLIENT_MOCK_ENABLED;
    boolean IDP_CLIENT_MOCK_ENABLED;

    // Sample controller endpoint
    String SAMPLE_LOLLIPOP_CONSUMER_ENDPOINT;

    // General Lollipop Configs Sample
    int LOLLIPOP_ASSERTION_EXPIRE_IN_DAYS;
    String LOLLIPOP_EXPECTED_LC_ORIGINAL_URL;
    String LOLLIPOP_EXPECTED_LC_ORIGINAL_METHOD;
    String LOLLIPOP_ASSERTION_NOT_BEFORE_DATE_FORMAT;
    String LOLLIPOP_ASSERTION_INSTANT_DATE_FORMAT;

    // Idp Client Configs
    String IDP_CLIENT_CIEID;
    String IDP_CLIENT_BASE_URI;
    String IDP_CLIENT_CIE_ENDPOINT;
    String IDP_CLIENT_SPID_ENDPOINT;

    // Idp Storage Configs
    boolean IDP_STORAGE_ENABLED;
    int IDP_STORAGE_EVICTION_DELAY;

    // Assertion Client Configs
    String ASSERTION_REST_URI;
    String ASSERTION_REST_ENDPOINT;

    // Assertion Storage Configs
    boolean ASSERTION_STORAGE_ENABLED;
    int ASSERTION_STORAGE_EVICTION_DELAY;

    public LollipopTypesafeConfig(Config config) {
        this.ASSERTION_CLIENT_MOCK_ENABLED =
                getConfigBoolean(config, "lollipop.assertion.client.mock.enabled", false);
        this.IDP_CLIENT_MOCK_ENABLED =
                getConfigBoolean(config, "lollipop.assertion.client.mock.enabled", false);

        this.SAMPLE_LOLLIPOP_CONSUMER_ENDPOINT =
                getConfigString(
                        config,
                        "sample.lollipop.consumer.config.endpoint",
                        "/api/v1/lollipop-consumer");

        this.LOLLIPOP_ASSERTION_EXPIRE_IN_DAYS =
                getConfigInt(config, "lollipop.core.config.assertionExpireInDays", 180);
        this.LOLLIPOP_EXPECTED_LC_ORIGINAL_URL =
                getConfigString(
                        config,
                        "lollipop.-core.config.expectedFirstLcOriginalUrl",
                        "https://api-app.io.pagopa.it/first-lollipop/sign");
        this.LOLLIPOP_EXPECTED_LC_ORIGINAL_METHOD =
                getConfigString(
                        config, "lollipop.core.config.expectedFirstLcOriginalMethod", "POST");
        this.LOLLIPOP_ASSERTION_NOT_BEFORE_DATE_FORMAT =
                getConfigString(
                        config,
                        "lollipop.core.config.assertionNotBeforeDateFormat",
                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        this.LOLLIPOP_ASSERTION_INSTANT_DATE_FORMAT =
                getConfigString(
                        config,
                        "lollipop.core.config.assertionInstantDateFormat",
                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        this.IDP_CLIENT_CIEID =
                getConfigString(
                        config,
                        "lollipop.idp.rest.config.cieEntityId",
                        "https://idserver.servizicie.interno.gov.it/idp/profile/SAML2/POST/SSO");
        this.IDP_CLIENT_BASE_URI =
                getConfigString(
                        config, "lollipop.idp.rest.config.baseUri", "https://api.is.eng.pagopa.it");
        this.IDP_CLIENT_CIE_ENDPOINT =
                getConfigString(
                        config, "lollipop.idp.rest.config.idpKeysCieEndpoint", "/idp-keys/cie");
        this.IDP_CLIENT_SPID_ENDPOINT =
                getConfigString(
                        config, "lollipop.idp.rest.config.idpKeysSpidEndpoint", "/idp-keys/spid");

        this.IDP_STORAGE_ENABLED =
                getConfigBoolean(
                        config, "lollipop.idp.storage.config.idpCertDataStorageEnabled", true);
        this.IDP_STORAGE_EVICTION_DELAY =
                getConfigInt(config, "lollipop.idp.storage.config.storageEvictionDelay", 1);

        this.ASSERTION_REST_URI =
                getConfigString(
                        config, "lollipop.assertion.rest.config.baseUri", "http://localhost:3000");
        this.ASSERTION_REST_ENDPOINT =
                getConfigString(
                        config,
                        "lollipop.assertion.rest.config.assertionRequestEndpoint",
                        "/assertions");

        this.ASSERTION_STORAGE_ENABLED =
                getConfigBoolean(
                        config, "lollipop.assertion.rest.config.assertionStorageEnabled", true);
        this.ASSERTION_STORAGE_EVICTION_DELAY =
                getConfigInt(config, "lollipop.assertion.rest.config.storageEvictionDelay", 1);
    }

    private String getConfigString(Config config, String path, String defaultString) {
        if (config.hasPath(path)) {
            return config.getString(path);
        } else {
            return defaultString;
        }
    }

    private boolean getConfigBoolean(Config config, String path, boolean defaultBoolean) {
        if (config.hasPath(path)) {
            return config.getBoolean(path);
        } else {
            return defaultBoolean;
        }
    }

    private int getConfigInt(Config config, String path, int defaultInt) {
        if (config.hasPath(path)) {
            return config.getInt(path);
        } else {
            return defaultInt;
        }
    }
}
