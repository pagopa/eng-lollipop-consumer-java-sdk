package it.pagopa.lollipop.consumer.http.verifier.visma;

import it.pagopa.common.lollipop.consumer.exception.LollipopDigestException;
import net.visma.autopay.http.digest.DigestException;

public class ErrorCodeConverter {

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
