package it.pagopa.spid.common.lc.idp;

public interface IdpCertProvider {

    boolean getIdpCertData(String assertionInstant, String entityId);

}
