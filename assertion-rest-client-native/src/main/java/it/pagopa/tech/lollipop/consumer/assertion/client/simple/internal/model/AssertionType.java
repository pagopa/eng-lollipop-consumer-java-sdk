/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Gets or Sets AssertionType */
public enum AssertionType {
    SAML("SAML"),

    OIDC("OIDC");

    private String value;

    AssertionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static AssertionType fromValue(String value) {
        for (AssertionType b : AssertionType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    /**
     * Convert the instance into URL query string.
     *
     * @param prefix prefix of the query string
     * @return URL query string
     */
    public String toUrlQueryString(String prefix) {
        if (prefix == null) {
            prefix = "";
        }

        return String.format("%s=%s", prefix, this.toString());
    }
}
