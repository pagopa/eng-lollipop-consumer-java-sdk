/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.servlet.utils;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import org.mockserver.client.MockServerClient;
import org.mockserver.model.Header;

public class SimpleClientsTestUtils {

    public static final String VALID_ASSERTION_XML =
            "{\"response_xml\": \"<samlp:Response"
                + " xmlns:samlp=\\\"urn:oasis:names:tc:SAML:2.0:protocol\\\" Version=\\\"2.0\\\""
                + " ID=\\\"id_432ca7e6e3fb172b94de5944e6cc0716b08227e7\\\""
                + " IssueInstant=\\\"2023-04-26T13:23:47Z\\\""
                + " Destination=\\\"https:\\/\\/localhost:8000\\/assertionConsumerService\\\""
                + " InResponseTo=\\\"sha256-chG21HBOK-wJp2hHuYPrx7tAII2UGWVF-IFo0crUOtw\\\">\\n"
                + "  <saml:Issuer xmlns:saml=\\\"urn:oasis:names:tc:SAML:2.0:assertion\\\""
                + " xmlns:xs=\\\"http:\\/\\/www.w3.org\\/2001\\/XMLSchema\\\""
                + " xmlns:xsi=\\\"http:\\/\\/www.w3.org\\/2001\\/XMLSchema-instance\\\""
                + " Format=\\\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\\\""
                + " NameQualifier=\\\"https:\\/\\/spid-testenv2:8088\\\">https:\\/\\/spid-testenv2:8088<\\/saml:Issuer><ds:Signature"
                + " xmlns:ds=\\\"http:\\/\\/www.w3.org\\/2000\\/09\\/xmldsig#\\\"><ds:SignedInfo><ds:CanonicalizationMethod"
                + " Algorithm=\\\"http:\\/\\/www.w3.org\\/2001\\/10\\/xml-exc-c14n#\\\"\\/><ds:SignatureMethod"
                + " Algorithm=\\\"http:\\/\\/www.w3.org\\/2001\\/04\\/xmldsig-more#rsa-sha256\\\"\\/><ds:Reference"
                + " URI=\\\"#id_432ca7e6e3fb172b94de5944e6cc0716b08227e7\\\"><ds:Transforms><ds:Transform"
                + " Algorithm=\\\"http:\\/\\/www.w3.org\\/2000\\/09\\/xmldsig#enveloped-signature\\\"\\/><ds:Transform"
                + " Algorithm=\\\"http:\\/\\/www.w3.org\\/2001\\/10\\/xml-exc-c14n#\\\"\\/><\\/ds:Transforms><ds:DigestMethod"
                + " Algorithm=\\\"http:\\/\\/www.w3.org\\/2001\\/04\\/xmlenc#sha256\\\"\\/><ds:DigestValue>s\\/DqYePHC7eCXX5ncsiFYNLyKbzS6P5C8331H1b8e30=<\\/ds:DigestValue><\\/ds:Reference><\\/ds:SignedInfo><ds:SignatureValue>WcasorooElvhmK0kxFUdBVCqyRYi0SCNGRSZZnC9Q2sZHOYGZbERe4\\/T8OSuRKbSrEivXIHRgNr8WskZTM2CiywfWChHfGvhERsLuPJE8oh9CR3eicX\\/eg0ynJqwx4IoYhTb2NOwqMFc66nnutMhG\\/Smdtjs4SFz0RQYYVeZ5Ho51iTHd94uBV9ZHXjqcvs3EitUsJ0Zg1Pkw352tt8y7niUcGjAd8nydI72S12sF5ePv05AunFp7vZpYbKqi62fQLORCn1ZP7WKFD75hL0bCvZaSRF285GkfSnLfe1S4tLff2SlTQWevOMU\\/wCkHJmQwHT1LMwcWRnMvv4V+vd1XQ==<\\/ds:SignatureValue><ds:KeyInfo><ds:X509Data><ds:X509Certificate>MIIC7TCCAdWgAwIBAgIJAMbxPOoBth1LMA0GCSqGSIb3DQEBCwUAMA0xCzAJBgNV\\n"
                + "BAYTAklUMB4XDTE4MDkwNDE0MDAxM1oXDTE4MTAwNDE0MDAxM1owDTELMAkGA1UE\\n"
                + "BhMCSVQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDJrW3y8Zd2jESP\\n"
                + "XGMRY04cHC4Qfo3302HEY1C6x1aDfW7aR\\/tXzNplfdw8ZtZugSSmHZBxVrR8aA08\\n"
                + "dUVbbtUw5qD0uAWKIeREqGfhM+J1STAMSI2\\/ZxA6t2fLmv8l1eRd1QGeRDm7yF9E\\n"
                + "EKGY9iUZD3LJf2mWdVBAzzYlG23M769k+9JuGZxuviNWMjojgYRiQFgzypUJJQz+\\n"
                + "Ihh3q7LMjjiQiiULVb9vnJg7UdU9Wf3xGRkxk6uiGP9SzWigSObUekYYQ4ZAI\\/sp\\n"
                + "ILywgDxVMMtv\\/eVniUFKLABtljn5cE9zltECahPbm7wIuMJpDDu5GYHGdYO0j+K7\\n"
                + "fhjvF2mzAgMBAAGjUDBOMB0GA1UdDgQWBBQEVmzA\\/L1\\/fd70ok+6xtDRF8A3HjAf\\n"
                + "BgNVHSMEGDAWgBQEVmzA\\/L1\\/fd70ok+6xtDRF8A3HjAMBgNVHRMEBTADAQH\\/MA0G\\n"
                + "CSqGSIb3DQEBCwUAA4IBAQCRMo4M4PqS0iLTTRWfikMF4hYMapcpmuna6p8aee7C\\n"
                + "wTjS5y7y18RLvKTi9l8OI0dVkgokH8fq8\\/o13vMw4feGxro1hMeUilRtH52funrW\\n"
                + "C+FgPrqk3o\\/8cZOnq+CqnFFDfILLiEb\\/PVJMddvTXgv2f9O6u17f8GmMLzde1yvY\\n"
                + "Da1fG\\/Pi0fG2F0yw\\/CmtP8OTLSvxjPtJ+ZckGzZa9GotwHsoVJ+Od21OU2lOeCnO\\n"
                + "jJOAbewHgqwkCB4O4AT5RM4ThAQtoU8QibjD1XDk\\/ZbEHdKcofnziDyl0V8gglP2\\n"
                + "SxpzDaPX0hm4wgHk9BOtSikb72tfOw+pNfeSrZEr6ItQ\\n"
                + "<\\/ds:X509Certificate><\\/ds:X509Data><\\/ds:KeyInfo><\\/ds:Signature>\\n"
                + "  <samlp:Status>\\n"
                + "    <samlp:StatusCode"
                + " Value=\\\"urn:oasis:names:tc:SAML:2.0:status:Success\\\"\\/>\\n"
                + "  <\\/samlp:Status>\\n"
                + "  <saml:Assertion xmlns:saml=\\\"urn:oasis:names:tc:SAML:2.0:assertion\\\""
                + " xmlns:xs=\\\"http:\\/\\/www.w3.org\\/2001\\/XMLSchema\\\""
                + " xmlns:xsi=\\\"http:\\/\\/www.w3.org\\/2001\\/XMLSchema-instance\\\""
                + " Version=\\\"2.0\\\" ID=\\\"id_451563f4726745384be4ab177e82c542baa99430\\\""
                + " IssueInstant=\\\"2023-04-26T13:23:47Z\\\">\\n"
                + "    <saml:Issuer Format=\\\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\\\""
                + " NameQualifier=\\\"https:\\/\\/spid-testenv2:8088\\\">https:\\/\\/spid-testenv2:8088<\\/saml:Issuer><ds:Signature"
                + " xmlns:ds=\\\"http:\\/\\/www.w3.org\\/2000\\/09\\/xmldsig#\\\"><ds:SignedInfo><ds:CanonicalizationMethod"
                + " Algorithm=\\\"http:\\/\\/www.w3.org\\/2001\\/10\\/xml-exc-c14n#\\\"\\/><ds:SignatureMethod"
                + " Algorithm=\\\"http:\\/\\/www.w3.org\\/2001\\/04\\/xmldsig-more#rsa-sha256\\\"\\/><ds:Reference"
                + " URI=\\\"#id_451563f4726745384be4ab177e82c542baa99430\\\"><ds:Transforms><ds:Transform"
                + " Algorithm=\\\"http:\\/\\/www.w3.org\\/2000\\/09\\/xmldsig#enveloped-signature\\\"\\/><ds:Transform"
                + " Algorithm=\\\"http:\\/\\/www.w3.org\\/2001\\/10\\/xml-exc-c14n#\\\"\\/><\\/ds:Transforms><ds:DigestMethod"
                + " Algorithm=\\\"http:\\/\\/www.w3.org\\/2001\\/04\\/xmlenc#sha256\\\"\\/><ds:DigestValue>4cqgG29TSKgNLy2\\/1eFPXhd5WRVPxZBGcd8DgTvd5Fo=<\\/ds:DigestValue><\\/ds:Reference><\\/ds:SignedInfo><ds:SignatureValue>WqxM+y+vtZDcEaIaw2WfcuMuwXUeTOY9ZjaXwzHw+RE8uUr5s8BE1tpaodcKmmqSJK1JQYNr8AUV+W9V79EKfmNtFvfaf0WYeUee7Td7E24QqiyVHjr1YgfDWhSdItFLYJfQUkotj2BepbdwVQGY5yN0Rw6Fq98hgNOgsxty7g6oqxG1OXB4WJ2He20iOoYWQl8ApxlbU\\/\\/hwnefFYe9ghDPy3rDbcNl3JetT07NR\\/+AzhKH4e+JCwKjTkdCBTW30fK4eiV9yBk74Lobip4hMaQhMaByl8egaU3A8AsnsZQuov2B6Wo2sDiQPjIulb8K3DOwFyL8PzEk8BB5YoAfwg==<\\/ds:SignatureValue><ds:KeyInfo><ds:X509Data><ds:X509Certificate>MIIC7TCCAdWgAwIBAgIJAMbxPOoBth1LMA0GCSqGSIb3DQEBCwUAMA0xCzAJBgNV\\n"
                + "BAYTAklUMB4XDTE4MDkwNDE0MDAxM1oXDTE4MTAwNDE0MDAxM1owDTELMAkGA1UE\\n"
                + "BhMCSVQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDJrW3y8Zd2jESP\\n"
                + "XGMRY04cHC4Qfo3302HEY1C6x1aDfW7aR\\/tXzNplfdw8ZtZugSSmHZBxVrR8aA08\\n"
                + "dUVbbtUw5qD0uAWKIeREqGfhM+J1STAMSI2\\/ZxA6t2fLmv8l1eRd1QGeRDm7yF9E\\n"
                + "EKGY9iUZD3LJf2mWdVBAzzYlG23M769k+9JuGZxuviNWMjojgYRiQFgzypUJJQz+\\n"
                + "Ihh3q7LMjjiQiiULVb9vnJg7UdU9Wf3xGRkxk6uiGP9SzWigSObUekYYQ4ZAI\\/sp\\n"
                + "ILywgDxVMMtv\\/eVniUFKLABtljn5cE9zltECahPbm7wIuMJpDDu5GYHGdYO0j+K7\\n"
                + "fhjvF2mzAgMBAAGjUDBOMB0GA1UdDgQWBBQEVmzA\\/L1\\/fd70ok+6xtDRF8A3HjAf\\n"
                + "BgNVHSMEGDAWgBQEVmzA\\/L1\\/fd70ok+6xtDRF8A3HjAMBgNVHRMEBTADAQH\\/MA0G\\n"
                + "CSqGSIb3DQEBCwUAA4IBAQCRMo4M4PqS0iLTTRWfikMF4hYMapcpmuna6p8aee7C\\n"
                + "wTjS5y7y18RLvKTi9l8OI0dVkgokH8fq8\\/o13vMw4feGxro1hMeUilRtH52funrW\\n"
                + "C+FgPrqk3o\\/8cZOnq+CqnFFDfILLiEb\\/PVJMddvTXgv2f9O6u17f8GmMLzde1yvY\\n"
                + "Da1fG\\/Pi0fG2F0yw\\/CmtP8OTLSvxjPtJ+ZckGzZa9GotwHsoVJ+Od21OU2lOeCnO\\n"
                + "jJOAbewHgqwkCB4O4AT5RM4ThAQtoU8QibjD1XDk\\/ZbEHdKcofnziDyl0V8gglP2\\n"
                + "SxpzDaPX0hm4wgHk9BOtSikb72tfOw+pNfeSrZEr6ItQ\\n"
                + "<\\/ds:X509Certificate><\\/ds:X509Data><\\/ds:KeyInfo><\\/ds:Signature>\\n"
                + "    <saml:Subject>\\n"
                + "      <saml:NameID"
                + " Format=\\\"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\\\""
                + " NameQualifier=\\\"https:\\/\\/spid-testenv2:8088\\\">id_48129c2a9d5e9077422591baf495747cfda668c5<\\/saml:NameID>\\n"
                + "      <saml:SubjectConfirmation"
                + " Method=\\\"urn:oasis:names:tc:SAML:2.0:cm:bearer\\\">\\n"
                + "        <saml:SubjectConfirmationData"
                + " Recipient=\\\"https:\\/\\/localhost:8000\\/assertionConsumerService\\\""
                + " NotOnOrAfter=\\\"2023-04-26T13:25:47Z\\\""
                + " InResponseTo=\\\"sha256-chG21HBOK-wJp2hHuYPrx7tAII2UGWVF-IFo0crUOtw\\\"\\/>\\n"
                + "      <\\/saml:SubjectConfirmation>\\n"
                + "    <\\/saml:Subject>\\n"
                + "    <saml:Conditions NotBefore=\\\"2023-04-26T13:21:47Z\\\""
                + " NotOnOrAfter=\\\"2023-04-26T13:25:47Z\\\">\\n"
                + "      <saml:AudienceRestriction>\\n"
                + "        <saml:Audience>https:\\/\\/spid.agid.gov.it\\/cd<\\/saml:Audience>\\n"
                + "      <\\/saml:AudienceRestriction>\\n"
                + "    <\\/saml:Conditions>\\n"
                + "    <saml:AuthnStatement AuthnInstant=\\\"2023-04-26T13:23:47Z\\\""
                + " SessionIndex=\\\"id_2086da5272d65361b188f3bb66b9eacaf9c2e219\\\">\\n"
                + "      <saml:AuthnContext>\\n"
                + "       "
                + " <saml:AuthnContextClassRef>https:\\/\\/www.spid.gov.it\\/SpidL2<\\/saml:AuthnContextClassRef>\\n"
                + "      <\\/saml:AuthnContext>\\n"
                + "    <\\/saml:AuthnStatement>\\n"
                + "    <saml:AttributeStatement>\\n"
                + "      <saml:Attribute Name=\\\"email\\\">\\n"
                + "        <saml:AttributeValue"
                + " xsi:type=\\\"xs:string\\\">info@agid.gov.it<\\/saml:AttributeValue>\\n"
                + "      <\\/saml:Attribute>\\n"
                + "      <saml:Attribute Name=\\\"name\\\">\\n"
                + "        <saml:AttributeValue"
                + " xsi:type=\\\"xs:string\\\">Mario<\\/saml:AttributeValue>\\n"
                + "      <\\/saml:Attribute>\\n"
                + "      <saml:Attribute Name=\\\"familyName\\\">\\n"
                + "        <saml:AttributeValue"
                + " xsi:type=\\\"xs:string\\\">Bianchi<\\/saml:AttributeValue>\\n"
                + "      <\\/saml:Attribute>\\n"
                + "      <saml:Attribute Name=\\\"fiscalNumber\\\">\\n"
                + "        <saml:AttributeValue"
                + " xsi:type=\\\"xs:string\\\">GDNNWA12H81Y874F<\\/saml:AttributeValue>\\n"
                + "      <\\/saml:Attribute>\\n"
                + "      <saml:Attribute Name=\\\"dateOfBirth\\\">\\n"
                + "        <saml:AttributeValue"
                + " xsi:type=\\\"xs:date\\\">1991-12-12<\\/saml:AttributeValue>\\n"
                + "      <\\/saml:Attribute>\\n"
                + "    <\\/saml:AttributeStatement>\\n"
                + "  <\\/saml:Assertion>\\n"
                + "<\\/samlp:Response>\"}";
    public static final String IDP_CLIENT_RESPONSE_STRING =
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
    public static final String ASSERTION_REF = "sha256-chG21HBOK-wJp2hHuYPrx7tAII2UGWVF-IFo0crUOtw";
    public static final String WRONG_ASSERTION_REF =
            "sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfXXXXX";
    public static final String JWT = "aValidJWT";
    public static final String VALID_PUBLIC_KEY =
            "eyJrdHkiOiJFQyIsIngiOiJTaHlZa0ZyN1F3eE9rOE5BRXF6aklkTnc4dEVKODlZOVBlWFF1eVVOWDVjIiwieSI6InlULVJxNWc2VlVadENUd0ZnRExDM2RneGNuM2RsSmNGRjhnWGdxYWgyS0UiLCJjcnYiOiJQLTI1NiJ9";
    private static final String IDP_TAG = "latest";

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
                .respond(response().withStatusCode(200).withBody(VALID_ASSERTION_XML));
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

    public static void createExpectationIdpFound() {
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

    private SimpleClientsTestUtils() {}
}
