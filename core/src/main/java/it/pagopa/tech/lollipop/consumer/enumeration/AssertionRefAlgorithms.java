package it.pagopa.tech.lollipop.consumer.enumeration;

import java.util.regex.Pattern;

/**
 * Supported AssertionRef algorithms
 */
public enum AssertionRefAlgorithms {

    SHA256("sha256", Pattern.compile("^(sha256-[A-Za-z0-9-_=]{1,44})$")),
    SHA384("sha384", Pattern.compile("^(sha384-[A-Za-z0-9-_=]{1,66})$")),
    SHA512("sha512", Pattern.compile("^(sha512-[A-Za-z0-9-_=]{1,88})$"));

    private final String algorithmName;
    private final Pattern pattern;

    AssertionRefAlgorithms(String algorithmName, Pattern pattern) {
        this.algorithmName = algorithmName;
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }
}
