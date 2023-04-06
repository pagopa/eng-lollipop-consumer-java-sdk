package it.pagopa.tech.sample;

public class Constants {
    public static final String CONTENT_DIGEST = "content-digest";
    public static final String CONTENT_ENCODING = "content-encoding";
    public static final String SIGNATURE_INPUT = "signature-input";
    public static final String SIGNATURE = "signature";
    public static final String VALID_ENCODING_UTF8 = "UTF-8";
    public static final String INVALID_ENCODING_UTF_326 = "UTF-326";
    public static final String VALID_CONTENT_DIGEST = "sha-256=:cpyRqJ1VhoVC+MSs9fq4/4wXs4c46EyEFriskys43Zw=:";
    public static final String INVALID_CONTENT_DIGEST = "sha-256=:fadsfeagsdage76ad564=:";
    public static final String VALID_MESSAGE_PAYLOAD = "{\"message\":\"a valid message payload\"}";
    public static final String INVALID_MESSAGE_PAYLOAD = "{\"message\":\"an invalid message payload\"}";
    public static final String SIGNATURE_HEADER_VALUE = "sig1=:lTuoRytp53GuUMOB4Rz1z97Y96gfSeEOm/xVpO39d3HR6lLAy4KYiGq+1hZ7nmRFBt2bASWEpen7ov5O4wU3kQ==:";
    public static final String SIGNATURE_INPUT_HEADER_VALUE = "sig1=(\"content-digest\" \"x-pagopa-lollipop-original-method\" "
            + "\"x-pagopa-lollipop-original-url\");created=1678293988;nonce=\"aNonce\";alg=\"ecdsa-p256-sha256\";keyid="
            + "\"sha256-a7qE0Y0DyqeOFFREIQSLKfu5WlbckdxVXKFasfcI-Dg";

    private Constants() {}
}
