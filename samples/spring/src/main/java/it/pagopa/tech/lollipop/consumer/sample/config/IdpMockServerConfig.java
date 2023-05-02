package it.pagopa.tech.lollipop.consumer.sample.config;

import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@Component
@ConditionalOnProperty(
        value="lollipop.idp.client.mock.enabled",
        havingValue = "true"
)
public class IdpMockServerConfig {

    private static final String IDP_CLIENT_RESPONSE_STRING =
            "<md:EntityDescriptor xmlns:md=\"urn:oasis:names:tc:SAML:2.0:metadata\""
                    + " entityID=\"https://spid-testenv2:8088\">\n"
                    + "  <md:IDPSSODescriptor"
                    + " protocolSupportEnumeration=\"urn:oasis:names:tc:SAML:2.0:protocol\""
                    + " WantAuthnRequestsSigned=\"true\">\n"
                    + "    <md:KeyDescriptor use=\"signing\">\n"
                    + "      <ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n"
                    + "        <ds:X509Data>\n"
                    + "         "
                    + " <ds:X509Certificate>MIIC7TCCAdWgAwIBAgIJAMbxPOoBth1LMA0GCSqGSIb3DQEBCwUAMA0xCzAJBgNV\n"
                    + "BAYTAklUMB4XDTE4MDkwNDE0MDAxM1oXDTE4MTAwNDE0MDAxM1owDTELMAkGA1UE\n"
                    + "BhMCSVQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDJrW3y8Zd2jESP\n"
                    + "XGMRY04cHC4Qfo3302HEY1C6x1aDfW7aR/tXzNplfdw8ZtZugSSmHZBxVrR8aA08\n"
                    + "dUVbbtUw5qD0uAWKIeREqGfhM+J1STAMSI2/ZxA6t2fLmv8l1eRd1QGeRDm7yF9E\n"
                    + "EKGY9iUZD3LJf2mWdVBAzzYlG23M769k+9JuGZxuviNWMjojgYRiQFgzypUJJQz+\n"
                    + "Ihh3q7LMjjiQiiULVb9vnJg7UdU9Wf3xGRkxk6uiGP9SzWigSObUekYYQ4ZAI/sp\n"
                    + "ILywgDxVMMtv/eVniUFKLABtljn5cE9zltECahPbm7wIuMJpDDu5GYHGdYO0j+K7\n"
                    + "fhjvF2mzAgMBAAGjUDBOMB0GA1UdDgQWBBQEVmzA/L1/fd70ok+6xtDRF8A3HjAf\n"
                    + "BgNVHSMEGDAWgBQEVmzA/L1/fd70ok+6xtDRF8A3HjAMBgNVHRMEBTADAQH/MA0G\n"
                    + "CSqGSIb3DQEBCwUAA4IBAQCRMo4M4PqS0iLTTRWfikMF4hYMapcpmuna6p8aee7C\n"
                    + "wTjS5y7y18RLvKTi9l8OI0dVkgokH8fq8/o13vMw4feGxro1hMeUilRtH52funrW\n"
                    + "C+FgPrqk3o/8cZOnq+CqnFFDfILLiEb/PVJMddvTXgv2f9O6u17f8GmMLzde1yvY\n"
                    + "Da1fG/Pi0fG2F0yw/CmtP8OTLSvxjPtJ+ZckGzZa9GotwHsoVJ+Od21OU2lOeCnO\n"
                    + "jJOAbewHgqwkCB4O4AT5RM4ThAQtoU8QibjD1XDk/ZbEHdKcofnziDyl0V8gglP2\n"
                    + "SxpzDaPX0hm4wgHk9BOtSikb72tfOw+pNfeSrZEr6ItQ\n"
                    + "</ds:X509Certificate>\n"
                    + "        </ds:X509Data>\n"
                    + "      </ds:KeyInfo>\n"
                    + "    </md:KeyDescriptor>\n"
                    + "    <md:SingleLogoutService"
                    + " Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\""
                    + " Location=\"https://spid-testenv2:8088/slo\"/>\n"
                    + "    <md:SingleLogoutService"
                    + " Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect\""
                    + " Location=\"https://spid-testenv2:8088/slo\"/>\n"
                    + "    <md:NameIDFormat>urn:oasis:names:tc:SAML:2.0:nameid-format:transient</md:NameIDFormat>\n"
                    + "    <md:SingleSignOnService"
                    + " Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\""
                    + " Location=\"https://spid-testenv2:8088/sso\"/>\n"
                    + "    <md:SingleSignOnService"
                    + " Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect\""
                    + " Location=\"https://spid-testenv2:8088/sso\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Name=\"spidCode\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Name=\"name\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Name=\"familyName\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " Name=\"placeOfBirth\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " Name=\"countyOfBirth\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " Name=\"dateOfBirth\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Name=\"gender\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " Name=\"companyName\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " Name=\"registeredOffice\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " Name=\"fiscalNumber\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Name=\"ivaCode\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Name=\"idCard\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " Name=\"mobilePhone\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Name=\"email\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Name=\"address\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " Name=\"expirationDate\"/>\n"
                    + "    <saml:Attribute xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                    + " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " Name=\"digitalAddress\"/>\n"
                    + "  </md:IDPSSODescriptor>\n"
                    + "</md:EntityDescriptor>\n";
    private static final String IDP_TAG = "latest";

    public IdpMockServerConfig() {
        ClientAndServer.startClientAndServer(3001);
        new MockServerClient("localhost", 3001)
                .when(request().withMethod("GET").withPath("/idp-keys/spid"))
                .respond(response().withStatusCode(200).withBody("[\"" + IDP_TAG + "\"]"));
        new MockServerClient("localhost", 3001)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/idp-keys/spid/{tag}")
                                .withPathParameter("tag", IDP_TAG))
                .respond(response().withStatusCode(200).withBody(IDP_CLIENT_RESPONSE_STRING));
    }
}
