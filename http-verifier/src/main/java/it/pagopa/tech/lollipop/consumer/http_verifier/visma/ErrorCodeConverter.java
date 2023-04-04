package it.pagopa.tech.lollipop.consumer.http_verifier.visma;

import it.pagopa.tech.lollipop.consumer.exception.LollipopDigestException;
import net.visma.autopay.http.digest.DigestException;

/**
 * Manages the conversion between {@link DigestException.ErrorCode} and {@link LollipopDigestException.ErrorCode}
  */
public class ErrorCodeConverter {

    /**
    * @param errorCode {@link DigestException.ErrorCode} to convert
     * @return converted {@link LollipopDigestException.ErrorCode}
     */
    public static LollipopDigestException.ErrorCode convertErrorCode(DigestException.ErrorCode errorCode) {

        if (errorCode != null) {
            switch (errorCode) {
                case INVALID_HEADER:
                    return LollipopDigestException.ErrorCode.INVALID_HEADER;
                case INCORRECT_DIGEST:
                    return LollipopDigestException.ErrorCode.INCORRECT_DIGEST;
                case UNSUPPORTED_ALGORITHM:
                    return LollipopDigestException.ErrorCode.UNSUPPORTED_ALGORITHM;
            }
        }

        return null;
    }

}
