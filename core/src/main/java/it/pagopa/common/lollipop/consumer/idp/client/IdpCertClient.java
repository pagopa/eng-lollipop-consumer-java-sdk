package it.pagopa.common.lollipop.consumer.idp.client;

import it.pagopa.common.lollipop.consumer.model.IdpCertData;

public interface IdpCertClient {

    IdpCertData getCertData(String entityId, String instant);
}
