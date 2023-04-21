/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LollipopConsumerRequestConfig {

    @Builder.Default private boolean strictDigestVerify = false;

    // request headers
    @Builder.Default private String signatureHeader = "signature";
    @Builder.Default private String signatureInputHeader = "signature-input";
    @Builder.Default private String contentEncodingHeader = "content-encoding";
    @Builder.Default private String contentDigestHeader = "content-digest";
    @Builder.Default private String originalMethodHeader = "x-pagopa-lollipop-original-method";
    @Builder.Default private String originalURLHeader = "x-pagopa-lollipop-original-url";
    @Builder.Default private String assertionRefHeader = "x-pagopa-lollipop-assertion-ref";
    @Builder.Default private String assertionTypeHeader = "x-pagopa-lollipop-assertion-type";
    @Builder.Default private String userIdHeader = "x-pagopa-lollipop-user-id";
    @Builder.Default private String publicKeyHeader = "x-pagopa-lollipop-public-key";
    @Builder.Default private String authJWTHeader = "x-pagopa-lollipop-auth-jwt";

    @Builder.Default private String expectedFirstLcOriginalMethod = "POST";

    @Builder.Default
    private String expectedFirstLcOriginalUrl = "https://api-app.io.pagopa.it/first-lollipop/sign";

    // assertion validation parameters
    @Builder.Default private int assertionExpireInDays = 30;
    @Builder.Default private String assertionNotBeforeDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    @Builder.Default private String assertionInstantDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    @Builder.Default
    private String samlNamespaceAssertion = "urn:oasis:names:tc:SAML:2.0:assertion";

    @Builder.Default private String assertionNotBeforeTag = "Conditions";
    @Builder.Default private String assertionFiscalCodeTag = "Attribute";
    @Builder.Default private String assertionInResponseToTag = "SubjectConfirmationData";

    @Builder.Default private boolean enableConsumerLogging = true;
    @Builder.Default private boolean enableAssertionLogging = true;
    @Builder.Default private boolean enableIdpCertDataLogging = true;

    @Builder.Default private String assertionEntityIdTag = "Issuer";
    @Builder.Default private String assertionInstantTag = "Assertion";
}
