package it.pagopa.common.lollipop.consumer.idp;

public interface IdpCertProvider {

    boolean getIdpCertData(String assertionInstant, String entityId);

}
