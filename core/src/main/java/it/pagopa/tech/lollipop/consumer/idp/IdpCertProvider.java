/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp;

public interface IdpCertProvider {

    boolean getIdpCertData(String assertionInstant, String entityId);
}
