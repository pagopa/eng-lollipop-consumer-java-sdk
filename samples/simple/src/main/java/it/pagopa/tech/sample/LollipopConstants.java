package it.pagopa.tech.sample;

public class LollipopConstants {
    public static final String CONTENT_DIGEST = "content-digest";
    public static final String CONTENT_ENCODING = "content-encoding";
    public static final String SIGNATURE_INPUT = "signature-input";
    public static final String SIGNATURE = "signature";

    public static final String LOLLIPOP_KEY = "x-pagopa-lollipop-public-key";

    public static final String LOLLIPOP_ORIGIN_METHOD = "x-pagopa-lollipop-original-method";

    public static final String LOLLIPOP_ORIGIN_URL = "x-pagopa-lollipop-original-url";

    public static final String EXPECTED_ORIGIN_METHOD = "POST";
    public static final String EXCPECTED_ORIGIN_URL = "https://api-app.io.pagopa.it/first-lollipop/sign";
    public static final String VALID_ENCODING_UTF8 = "UTF-8";
    public static final String INVALID_ENCODING_UTF_326 = "UTF-326";
    public static final String VALID_CONTENT_DIGEST = "sha-256=:cpyRqJ1VhoVC+MSs9fq4/4wXs4c46EyEFriskys43Zw=:";
    public static final String INVALID_CONTENT_DIGEST = "sha-256=:fadsfeagsdage76ad564=:";
    public static final String VALID_MESSAGE_PAYLOAD = "{\"message\":\"a valid message payload\"}";
    public static final String INVALID_MESSAGE_PAYLOAD = "{\"message\":\"an invalid message payload\"}";
    public static final String SIGNATURE_HEADER_VALUE = "sig123=:lTuoRytp53GuUMOB4Rz1z97Y96gfSeEOm/xVpO39d3HR6lLAy4KYiGq+1hZ7nmRFBt2bASWEpen7ov5O4wU3kQ==:";
    public static final String INVALID_SIGNATURE_HEADER_VALUE = "sig123=:lTTTRytp53GuUMOB4Rz1z97Y96gfSeEOm/xVpO39d3HR6lLAy4KYiGq+1hZ7nmRFBt2bASWEpen7ov5O4wU3kQ==:";
    public static final String SIGNATURE_INPUT_HEADER_VALUE = "sig123=(\"content-digest\" \"x-pagopa-lollipop-original-method\""
            + " \"x-pagopa-lollipop-original-url\");created=1678293988;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\"";

    public static final String ECDSA_LOLLIPOP_JWT_KEY = "eyJrdHkiOiJFQyIsIngiOiJGcUZEd"
            + "XdFZ3U0TVVYRVJQTVZMLTg1cEd2MkQzWW1MNEoxZ2ZNa2RiYzI0IiwieSI6Im"
            + "hkVjBveG1XRlN4TW9KVURwZGlocjc2clM4VlJCRXFNRmViWXlBZks5LWsiLCJjcnYiO"
            + "iJQLTI1NiJ9";

    public static String VALID_MULTI_ECDSA_IGNATURE_INPUT =
            "sig1=(\"x-io-sign-qtspclauses\");created=1678299228;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\","
                    + " sig2=(\"content-digest\" \"x-pagopa-lollipop-original-method\""
                    + " \"x-pagopa-lollipop-original-url\");created=1678299228;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid=\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg\"";
    public static String VALID_MULTI_ECDSA_SIGNATURE =
            "sig1=:dncsEeKERA9wzxBO0vbPIueMK7Izk4zZNX4D0jI+t17XQJ5YrhumR3MGvMiyarb+B8MPqn+rbOJwZt6dV+oXFA==:,"
                    + " sig2=:nbmFduqX8AdhXzqkFX+UIvicn3ZV5yZXqUO+3bceOT8WFPXRTVRcoOcjF+0+W5KLihAZjSW5GXSgCxVVEW8pqQ==:";

    public static String VALID_RSA_PSS_SIGNATURE_INPUT =
            "sig1=(\"content-digest\" \"x-pagopa-lollipop-original-method\""
                    + " \"x-pagopa-lollipop-original-url\");created=1678814391;nonce=\"aNonce\";"
                    + "alg=\"rsa-pss-sha256\";keyid=\"sha256-A3OhKGLYwSvdJ2txHi_SGQ3G-sHLh2Ibu91ErqFx_58\"";

    public static String VALID_RSA_PSS_SIGNATURE =
            "sig1=:q3Og7m8yL18HkrY+zgV92Gj05lrWaFMIEFSPg2PEnO5a46+2Tt/2n7kjqVaGjI1ZXtys+Wyh3cVXCdda" +
                    "dNARizt0BpCRdT9S4r48xsGO79Ucq4IFwZyyHNudKu5WSH4/55j5yX/YmeCtH+Nt6Nun02OZynn3iQwg" +
                    "LJB+CGe3h6X02iSvl4wJjKaMGE64RFHa5osE4MctoPD1j0tRkcOtgwrGmFMr282Kqrkabbx1vUpmO9T1k" +
                    "hjouxIryfUln9zIaZ+wWmukpAZv7TKO3CltNWgfx1XT9m/iwzHiGmtvcHbWVExdAyey8lH23MgLY43AM7y" +
                    "tLQNSlk1s/bPNbGmPwg==:";

    public static String LOLLIPOP_RSA_PUBKEY = "eyJrdHkiOiJSU0EiLCJlIjoiQVFBQiIsImtpZCI6InRlc3Qta2V5LXJzYS1wc3MiLCJuIjoicjR0bW0zc"
            + "jIwV2RfUGJxdlAxczItUUV0dnB1UmFWOFlxNDBnalVSOHkyUmp4YTZkcEcyR1hIYlBmdk0gIHM4Y3Q"
            + "tTGgxR0g0NXgyOFJ3M1J5NTNtbS1vQVhqeVE4Nk9uRGtaNU44bFliZ2dENE8zdzZNNnBBdkxraGs5NU"
            + "FuICBkVHJpZmJJRlBOVThQUE1PN095ckZBSHFnRHN6bmpQRm1UT3RDRWNOMloxRnBXZ2Nod3VZTFBMLV"
            + "dva3FsdGQxMSAgbnFxemktYko5Y3ZTS0FEWWRVQUFONVdVdHpkcGl5NkxiVGdTeFA3b2NpVTRUbjBnNU"
            + "k2YURaSjdBOEx6bzBLU3kgIFpZb0E0ODVtcWNPMEdWQWRWdzlscTRhT1Q5djZkLW5iNGJuTmtRVmtsTFE"
            + "zZlZBdkptLXhkRE9wOUxDTkNONDhWICAycG5ET2tGVjYtVTluVjVveWM2WEkydyJ9";

    public static final String VALID_ASSERTION_REF =
            "sha256-_ZzL8qeuAM5kQ9pbMB4tn7IDSQZCVXAkW9fm4P7ULPI";
    public static final String VALID_FISCAL_CODE = "AAAAAA89S20I111X";
    public static final String VALID_JWT = "aValidJWT";

    public LollipopConstants() {}
}
