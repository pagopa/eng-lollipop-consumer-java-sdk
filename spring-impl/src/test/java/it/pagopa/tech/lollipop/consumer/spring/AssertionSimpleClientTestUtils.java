/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.spring;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Header;

public class AssertionSimpleClientTestUtils {

    public static final String RESPONSE_STRING =
            "{\"response_xml\": \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\""
                + " standalone=\\\"no\\\"?> <saml2p:Response"
                + " xmlns:saml2p=\\\"urn:oasis:names:tc:SAML:2.0:protocol\\\""
                + " Destination=\\\"https://app-backend.io.italia.it/assertionConsumerService\\\""
                + " ID=\\\"_de2ce675-f1e5-46fc-96ed-019803471175\\\""
                + " InResponseTo=\\\"sha256-_ZzL8qeuAM5kQ9pbMB4tn7IDSQZCVXAkW9fm4P7ULPI\\\""
                + " IssueInstant=\\\"2023-02-28T16:27:26.400Z\\\" Version=\\\"2.0\\\">"
                + " <saml2:Issuer"
                + " xmlns:saml2=\\\"urn:oasis:names:tc:SAML:2.0:assertion\\\">https://posteid.poste.it</saml2:Issuer>"
                + " <Signature xmlns=\\\"http://www.w3.org/2000/09/xmldsig#\\\"> <SignedInfo>"
                + " <CanonicalizationMethod"
                + " Algorithm=\\\"http://www.w3.org/2001/10/xml-exc-c14n#\\\" /> <SignatureMethod"
                + " Algorithm=\\\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\\\" />"
                + " <Reference URI=\\\"#_de2ce675-f1e5-46fc-96ed-019803471175\\\"> <Transforms>"
                + " <Transform"
                + " Algorithm=\\\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\\\" />"
                + " <Transform Algorithm=\\\"http://www.w3.org/2001/10/xml-exc-c14n#\\\" />"
                + " </Transforms> <DigestMethod"
                + " Algorithm=\\\"http://www.w3.org/2001/04/xmlenc#sha256\\\" />"
                + " <DigestValue>IYCwE8NJNGLAGdL8zA/W/kuTLdlHMMXMeP2hei8LYqU=</DigestValue>"
                + " </Reference> </SignedInfo>"
                + " <SignatureValue>GI9CwzYfcmTBE9Lf7Hvqr2bgCkfbuq6vZPwZaaCmxq5cicDf7+k6TYussUx147iAdngl4vMixAjA"
                + " eABU0cSrZllLW0Gqxm+EPvylwMc4O1tNYlpvjnZzW7PIRns5M22KSzfHBDdGZG7Dq4uDHVbGCENu"
                + " TV5UaGNQJ2sNTD95Qaz6pmQtx0guehDc/m5ldFvChqZTKrOcVu+qTRFkW+OabbnkmKXPSWpTN9WE"
                + " 4RramWgWkNE/sn4z0Rwmnei8oEhcBKSpOMrenbMgpCYjoRto5lDGGJrMkeKsJ1PzD1ZCrE/GkzX9"
                + " HtXHIaYE6cZ7vBXQh4SVpl26JGQ87tu2YLeoZw==</SignatureValue> <KeyInfo> <X509Data>"
                + " <X509Certificate>MIIFgzCCA2ugAwIBAgIIJSppAZKg/XQwDQYJKoZIhvcNAQELBQAwZTELMAkGA1UEBhMCSVQxHjAc"
                + " BgNVBAoMFVBvc3RlIEl0YWxpYW5lIFMucC5BLjEaMBgGA1UEYQwRVkFUSVQtMDExMTQ2MDEwMDYx"
                + " GjAYBgNVBAMMEVBvc3RlIEl0YWxpYW5lIENBMB4XDTIxMDIxODExNDYzMVoXDTI0MDIxOTExNDYz"
                + " MVowQzELMAkGA1UEBhMCSVQxHjAcBgNVBAoMFVBvc3RlIEl0YWxpYW5lIFMucC5BLjEUMBIGA1UE"
                + " AwwLaWRwLXBvc3RlaWQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDZFEtJoEHFAjpC"
                + " aZcj5DVWrRDyaLZyu31XApslbo87CyWz61OJMtw6QQU0MdCtrYbtSJ6vJwx7/6EUjsZ3u4x3EPLd"
                + " lkyiGOqukPwATv4c7TVOUVs5onIqTphM9b+AHRg4ehiMGesm/9d7RIaLuN79iPUvdLn6WP3idAfE"
                + " w+rhJ/wYEQ0h1Xm5osNUgtWcBGavZIjLssWNrDDfJYxXH3QZ0kI6feEvLCJwgjXLGkBuhFehNhM4"
                + " fhbX9iUCWwwkJ3JsP2++Rc/iTA0LZhiUsXNNq7gBcLAJ9UX2V1dWjTzBHevfHspzt4e0VgIIwbDR"
                + " qsRtF8VUPSDYYbLoqwbLt18XAgMBAAGjggFXMIIBUzA/BggrBgEFBQcBAQQzMDEwLwYIKwYBBQUH"
                + " MAGGI2h0dHA6Ly9wb3N0ZWNlcnQucG9zdGUuaXQvcGktb2NzcENBMB0GA1UdDgQWBBRL64pGUJHw"
                + " Y7ok6cRMUgXvMBoLMjAfBgNVHSMEGDAWgBRs0025F7hHd0d+ULyAaELPZ7w/eTA+BgNVHSAENzA1"
                + " MDMGCCtMMAEFAQEEMCcwJQYIKwYBBQUHAgEWGWh0dHA6Ly9wb3N0ZWNlcnQucG9zdGUuaXQwOAYD"
                + " VR0fBDEwLzAtoCugKYYnaHR0cDovL3Bvc3RlY2VydC5wb3N0ZS5pdC9waS1DQS9jcmwuY3JsMA4G"
                + " A1UdDwEB/wQEAwIFoDAdBgNVHSUEFjAUBggrBgEFBQcDAgYIKwYBBQUHAwQwJwYDVR0RBCAwHoEc"
                + " aWRwLXBvc3RlaWRAcG9zdGVpdGFsaWFuZS5pdDANBgkqhkiG9w0BAQsFAAOCAgEAp0EhITlTx+cO"
                + " aoXw//nBl6Q4y82MfSGfPJIw3ROV1z3tHBctaksi/RxAzyMD5beO2s8Q6lXx0sLMCcuUQmzHj3eJ"
                + " bqn+6sIUr000dSlX/iPgVUc2dvPIZZg9xu38J8NvCfrtgAGY5iMVFMd3CZLFw0ybr+Bx/1K/NhQO"
                + " 7jxn0RSGA1J4mM2syVhEDUODs9kz3T4kXYUofwwvPL1a9xB9RBqbp7plYtbBBdftEORUQrWzH1mz"
                + " NO4nlFkX9qgVrgFIIJJT2KadHoop1r65O9ffncK14qpNo3eTsNDq3hRlteb7ylmlJ8CoakUWZeXD"
                + " DP9ZboWxZkyp+9903OrToRvOgeWSc+YrqcRZOv7r6tTALTk4U9OTKDG9/eNWSGQqD7Qd/9rssfF0"
                + " uJEGHnbsk/Hvdxn8apgWN1Zwt6tsT7f/DO0Pdlaso9g7PVy8R+B3VkWAh76uCcICIPFBluC/ljaH"
                + " V8hI+VsCLpMClo83YMCEM6E6nAPD22+fDR/DF9P73P04yUvJVHx4cnHPrpxVrPbaJoKrr9mUOLFy"
                + " VRekX78ZRgiFiKYDNsiq9+148oRy+VehpmBoQ+T2EPeDFQ8JJ4xT8H7qdyr1swSk/9Lu4K0kw/yC"
                + " TSb9K/wCuiHiuoSB54rzJoQxz90gS868r/+JGahYwHY5dUh1RbA4g5N8H3TDThc=</X509Certificate>"
                + " </X509Data> </KeyInfo> </Signature> <saml2p:Status> <saml2p:StatusCode"
                + " Value=\\\"urn:oasis:names:tc:SAML:2.0:status:Success\\\" /> </saml2p:Status>"
                + " <saml2:Assertion xmlns:saml2=\\\"urn:oasis:names:tc:SAML:2.0:assertion\\\""
                + " ID=\\\"_6b9580aa-08b1-4f19-8fb6-8b670d070bad\\\""
                + " IssueInstant=\\\"2023-02-28T16:27:25.400Z\\\" Version=\\\"2.0\\\">"
                + " <saml2:Issuer Format=\\\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\\\">"
                + " https://posteid.poste.it</saml2:Issuer> <Signature"
                + " xmlns=\\\"http://www.w3.org/2000/09/xmldsig#\\\"> <SignedInfo>"
                + " <CanonicalizationMethod"
                + " Algorithm=\\\"http://www.w3.org/2001/10/xml-exc-c14n#\\\" /> <SignatureMethod"
                + " Algorithm=\\\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\\\" />"
                + " <Reference URI=\\\"#_6b9580aa-08b1-4f19-8fb6-8b670d070bad\\\"> <Transforms>"
                + " <Transform"
                + " Algorithm=\\\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\\\" />"
                + " <Transform Algorithm=\\\"http://www.w3.org/2001/10/xml-exc-c14n#\\\" />"
                + " </Transforms> <DigestMethod"
                + " Algorithm=\\\"http://www.w3.org/2001/04/xmlenc#sha256\\\" />"
                + " <DigestValue>ViSjPfKj683dCuO7FdSzbQjw+vECYfoxgTeiVSgxr+I=</DigestValue>"
                + " </Reference> </SignedInfo>"
                + " <SignatureValue>O9lmrtHPudDz2fzzNH3DQxWy2rlXE56G54Siq7OPMYwps/cyo3wKo7+PwMJYNhhz1l57OYJ5e/MF"
                + " ctVtYyl2rWo3QZOidWhg8WINIEqtFXIpk+ht5i2t3P1132/iL/gnY+fgemhnbOV/otEspHA4Wsio"
                + " I8xWjekAFlHBTOTtO9vzzqTtf+yalf+6pZmRLtOYrMMV4W3QZ2oLr7C2vTgcl5eVXJyGf0U8Y2bf"
                + " 7OPRHJNnVs4S8ztWQEwqZLFA1SvyCx1Nx6f+xd9lT7Lo1h81MRMdvRTk3rAaWYaqAmU9mxVnzsw4"
                + " xaLjxR4rE2drY3eb+O8uHZbzFlOhPtaINRPILg==</SignatureValue> <KeyInfo> <X509Data>"
                + " <X509Certificate>MIIFgzCCA2ugAwIBAgIIJSppAZKg/XQwDQYJKoZIhvcNAQELBQAwZTELMAkGA1UEBhMCSVQxHjAc"
                + " BgNVBAoMFVBvc3RlIEl0YWxpYW5lIFMucC5BLjEaMBgGA1UEYQwRVkFUSVQtMDExMTQ2MDEwMDYx"
                + " GjAYBgNVBAMMEVBvc3RlIEl0YWxpYW5lIENBMB4XDTIxMDIxODExNDYzMVoXDTI0MDIxOTExNDYz"
                + " MVowQzELMAkGA1UEBhMCSVQxHjAcBgNVBAoMFVBvc3RlIEl0YWxpYW5lIFMucC5BLjEUMBIGA1UE"
                + " AwwLaWRwLXBvc3RlaWQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDZFEtJoEHFAjpC"
                + " aZcj5DVWrRDyaLZyu31XApslbo87CyWz61OJMtw6QQU0MdCtrYbtSJ6vJwx7/6EUjsZ3u4x3EPLd"
                + " lkyiGOqukPwATv4c7TVOUVs5onIqTphM9b+AHRg4ehiMGesm/9d7RIaLuN79iPUvdLn6WP3idAfE"
                + " w+rhJ/wYEQ0h1Xm5osNUgtWcBGavZIjLssWNrDDfJYxXH3QZ0kI6feEvLCJwgjXLGkBuhFehNhM4"
                + " fhbX9iUCWwwkJ3JsP2++Rc/iTA0LZhiUsXNNq7gBcLAJ9UX2V1dWjTzBHevfHspzt4e0VgIIwbDR"
                + " qsRtF8VUPSDYYbLoqwbLt18XAgMBAAGjggFXMIIBUzA/BggrBgEFBQcBAQQzMDEwLwYIKwYBBQUH"
                + " MAGGI2h0dHA6Ly9wb3N0ZWNlcnQucG9zdGUuaXQvcGktb2NzcENBMB0GA1UdDgQWBBRL64pGUJHw"
                + " Y7ok6cRMUgXvMBoLMjAfBgNVHSMEGDAWgBRs0025F7hHd0d+ULyAaELPZ7w/eTA+BgNVHSAENzA1"
                + " MDMGCCtMMAEFAQEEMCcwJQYIKwYBBQUHAgEWGWh0dHA6Ly9wb3N0ZWNlcnQucG9zdGUuaXQwOAYD"
                + " VR0fBDEwLzAtoCugKYYnaHR0cDovL3Bvc3RlY2VydC5wb3N0ZS5pdC9waS1DQS9jcmwuY3JsMA4G"
                + " A1UdDwEB/wQEAwIFoDAdBgNVHSUEFjAUBggrBgEFBQcDAgYIKwYBBQUHAwQwJwYDVR0RBCAwHoEc"
                + " aWRwLXBvc3RlaWRAcG9zdGVpdGFsaWFuZS5pdDANBgkqhkiG9w0BAQsFAAOCAgEAp0EhITlTx+cO"
                + " aoXw//nBl6Q4y82MfSGfPJIw3ROV1z3tHBctaksi/RxAzyMD5beO2s8Q6lXx0sLMCcuUQmzHj3eJ"
                + " bqn+6sIUr000dSlX/iPgVUc2dvPIZZg9xu38J8NvCfrtgAGY5iMVFMd3CZLFw0ybr+Bx/1K/NhQO"
                + " 7jxn0RSGA1J4mM2syVhEDUODs9kz3T4kXYUofwwvPL1a9xB9RBqbp7plYtbBBdftEORUQrWzH1mz"
                + " NO4nlFkX9qgVrgFIIJJT2KadHoop1r65O9ffncK14qpNo3eTsNDq3hRlteb7ylmlJ8CoakUWZeXD"
                + " DP9ZboWxZkyp+9903OrToRvOgeWSc+YrqcRZOv7r6tTALTk4U9OTKDG9/eNWSGQqD7Qd/9rssfF0"
                + " uJEGHnbsk/Hvdxn8apgWN1Zwt6tsT7f/DO0Pdlaso9g7PVy8R+B3VkWAh76uCcICIPFBluC/ljaH"
                + " V8hI+VsCLpMClo83YMCEM6E6nAPD22+fDR/DF9P73P04yUvJVHx4cnHPrpxVrPbaJoKrr9mUOLFy"
                + " VRekX78ZRgiFiKYDNsiq9+148oRy+VehpmBoQ+T2EPeDFQ8JJ4xT8H7qdyr1swSk/9Lu4K0kw/yC"
                + " TSb9K/wCuiHiuoSB54rzJoQxz90gS868r/+JGahYwHY5dUh1RbA4g5N8H3TDThc=</X509Certificate>"
                + " </X509Data> </KeyInfo> </Signature> <saml2:Subject> <saml2:NameID"
                + " Format=\\\"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\\\""
                + " NameQualifier=\\\"https://posteid.poste.it\\\">SPID-d4de186b-e103-4b39-8209-0bccc7b1acdd</saml2:NameID>"
                + " <saml2:SubjectConfirmation"
                + " Method=\\\"urn:oasis:names:tc:SAML:2.0:cm:bearer\\\">"
                + " <saml2:SubjectConfirmationData"
                + " InResponseTo=\\\"sha256-_ZzL8qeuAM5kQ9pbMB4tn7IDSQZCVXAkW9fm4P7ULPI\\\""
                + " NotOnOrAfter=\\\"2023-02-28T16:28:25.400Z\\\""
                + " Recipient=\\\"https://app-backend.io.italia.it/assertionConsumerService\\\" />"
                + " </saml2:SubjectConfirmation> </saml2:Subject> <saml2:Conditions NotBefore=\\\""
                    + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date())
                    + "\\\" NotOnOrAfter=\\\"2023-02-28T16:28:25.400Z\\\">"
                    + " <saml2:AudienceRestriction>"
                    + " <saml2:Audience>https://app-backend.io.italia.it</saml2:Audience>"
                    + " </saml2:AudienceRestriction> </saml2:Conditions> <saml2:AuthnStatement"
                    + " AuthnInstant=\\\"2023-02-28T16:27:25.400Z\\\"> <saml2:AuthnContext>"
                    + " <saml2:AuthnContextClassRef>https://www.spid.gov.it/SpidL2</saml2:AuthnContextClassRef>"
                    + " </saml2:AuthnContext> </saml2:AuthnStatement> <saml2:AttributeStatement>"
                    + " <saml2:Attribute FriendlyName=\\\"Codice fiscale\\\""
                    + " Name=\\\"fiscalNumber\\\"> <saml2:AttributeValue"
                    + " xmlns:xs=\\\"http://www.w3.org/2001/XMLSchema\\\""
                    + " xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\""
                    + " xsi:type=\\\"xs:string\\\"> TINIT-AAAAAA89S20I111X</saml2:AttributeValue>"
                    + " </saml2:Attribute> </saml2:AttributeStatement> </saml2:Assertion>"
                    + " </saml2p:Response>\"}";
    public static final String ASSERTION_REF = "sha256-_ZzL8qeuAM5kQ9pbMB4tn7IDSQZCVXAkW9fm4P7ULPI";
    public static final String WRONG_ASSERTION_REF =
            "sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfXXXXX";
    public static final String JWT = "aValidJWT";

    public static void createExpectationAssertionFound() {
        new MockServerClient("localhost", 3000)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/assertions/{assertion}")
                                .withPathParameter("assertion", ASSERTION_REF)
                                .withHeaders(
                                        new Header("Accept", "application/json"),
                                        new Header("x-pagopa-lollipop-auth", JWT)))
                .respond(response().withStatusCode(200).withBody(RESPONSE_STRING));
    }

    public static void createExpectationAssertionNotFound() {
        new MockServerClient("localhost", 2000)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/assertions/{assertion}")
                                .withPathParameter("assertion", WRONG_ASSERTION_REF)
                                .withHeaders(
                                        new Header("Accept", "application/json"),
                                        new Header("x-pagopa-lollipop-auth", JWT)))
                .respond(response().withStatusCode(404).withBody("{}"));
    }
}
