/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.client;

import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import java.util.List;

public interface IdpCertClient {

    List<IdpCertData> getCertData(String entityId, String instant);
}
