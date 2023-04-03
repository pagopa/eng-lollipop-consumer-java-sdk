package it.pagopa.spid.common.lc.idp.client;

import it.pagopa.spid.common.lc.model.IdpCertData;

public interface IdpCertClient {

    IdpCertData getCertData(String entityId, String instant);
}
