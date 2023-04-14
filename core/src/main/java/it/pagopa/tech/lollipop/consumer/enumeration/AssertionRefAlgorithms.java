package it.pagopa.tech.lollipop.consumer.enumeration;

import java.util.regex.Pattern;

/**
 * Supported AssertionRef algorithms
 */
public enum AssertionRefAlgorithms {

    SHA256("sha256", AlgorithmName.SHA_256, Pattern.compile("^(sha256-[A-Za-z0-9-_=]{1,44})$")),
    SHA384("sha384", AlgorithmName.SHA_384, Pattern.compile("^(sha384-[A-Za-z0-9-_=]{1,66})$")),
    SHA512("sha512", AlgorithmName.SHA_512, Pattern.compile("^(sha512-[A-Za-z0-9-_=]{1,88})$"));

    private final String algorithmName;
    private final String hashAlgorithm;
    private final Pattern pattern;

    AssertionRefAlgorithms(String algorithmName, String hashAlgorithm, Pattern pattern) {
        this.algorithmName = algorithmName;
        this.hashAlgorithm = hashAlgorithm;
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public static AssertionRefAlgorithms getAlgorithmFromHash(String algorithmName) {
        switch (algorithmName) {
            case AlgorithmName.SHA_256: return SHA256;
            case AlgorithmName.SHA_384: return SHA384;
            case AlgorithmName.SHA_512: return SHA512;
            default: throw new UnsupportedOperationException("Unsupported algorithm: " + algorithmName);
        }
    }

    private static class AlgorithmName {
        public static final String SHA_256 = "SHA-256";
        public static final String SHA_384 = "SHA-384";
        public static final String SHA_512 = "SHA-512";
    }
}
