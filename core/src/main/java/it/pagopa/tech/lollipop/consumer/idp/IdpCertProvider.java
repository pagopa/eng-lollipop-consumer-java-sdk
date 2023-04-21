/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp;

import it.pagopa.tech.lollipop.consumer.exception.CertDataNotFoundException;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import java.util.List;

public interface IdpCertProvider {

    List<IdpCertData> getIdpCertData(String assertionInstant, String entityId)
            throws CertDataNotFoundException;
}
