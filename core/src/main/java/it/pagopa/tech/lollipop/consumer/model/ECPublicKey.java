/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.model;

import lombok.Data;

/** EC public key model */
@Data
public class ECPublicKey {

    private String crv;
    private String kty;
    private String x;
    private String y;
}
