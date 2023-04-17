/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.storage;

import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import java.util.List;

/**
 * Interface of the storage used for storing the identity provider certification data retrieved for
 * validation
 */
public interface IdpCertStorage {

    /**
     * Retrieve the idp cert data associated with the provided tag
     *
     * @param tag
     * @return the list of idpCertData found
     */
    List<IdpCertData> getIdpCertData(String tag);

    /**
     * Store the provided idpCertData
     *
     * @param tag the idpCertData issue instance
     * @param idpCertData
     */
    void saveIdpCertData(String tag, List<IdpCertData> idpCertData);
}
