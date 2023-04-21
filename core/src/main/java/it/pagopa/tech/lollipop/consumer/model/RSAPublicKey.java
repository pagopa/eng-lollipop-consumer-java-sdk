/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.model;

import lombok.Data;

/** RSA public key model */
@Data
public class RSAPublicKey {

    private String alg;
    private String e;
    private String kty;
    private String n;
}
