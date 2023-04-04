package it.pagopa.tech.lollipop.consumer.idp.client;

import it.pagopa.tech.lollipop.consumer.model.IdpCertData;

public interface IdpCertClient {

    IdpCertData getCertData(String entityId, String instant);
}
