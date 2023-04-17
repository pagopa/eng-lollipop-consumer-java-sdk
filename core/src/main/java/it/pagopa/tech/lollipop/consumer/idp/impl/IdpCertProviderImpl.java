/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.impl;

import it.pagopa.tech.lollipop.consumer.exception.CertDataNotFoundException;
import it.pagopa.tech.lollipop.consumer.exception.CertDataTagListNotFoundException;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClient;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorage;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import java.util.List;
import javax.inject.Inject;

public class IdpCertProviderImpl implements IdpCertProvider {

    private IdpCertClient idpCertClient;
    private IdpCertStorage idpCertStorage;

    @Inject
    public IdpCertProviderImpl(IdpCertClient idpCertClient, IdpCertStorage idpCertStorage) {
        this.idpCertClient = idpCertClient;
        this.idpCertStorage = idpCertStorage;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Retrieve the Identity Provider Certification Data, first looking in the storage if enabled
     * ({@link IdpCertStorageConfig}) and then, if not found, through the client {@link
     * IdpCertClient}. If the storage is enabled ({@link IdpCertStorageConfig}) the IdpCertData will
     * be stored, after being retrieved by the client.
     *
     * @param assertionInstant Assertion Issue Instant found in the xml
     * @param entityId Entity ID of the identity provider found in the assertion
     * @return List<IdpCertData> a list of idp cert data
     * @throws CertDataTagListNotFoundException
     * @throws CertDataNotFoundException
     */
    @Override
    public List<IdpCertData> getIdpCertData(String assertionInstant, String entityId)
            throws CertDataTagListNotFoundException, CertDataNotFoundException {
        if (assertionInstant == null
                || assertionInstant.isBlank()
                || entityId == null
                || entityId.isBlank()) {
            String errMsg =
                    String.format(
                            "Cannot retrieve the identity provider cert data, assertion instant"
                                    + " [%s] or entity id [%s] missing",
                            assertionInstant, entityId);
            throw new IllegalArgumentException(errMsg);
        }

        List<IdpCertData> listIdpCertData = idpCertStorage.getIdpCertData(assertionInstant);

        if (listIdpCertData != null && listIdpCertData.size() > 0) {
            return listIdpCertData;
        }

        listIdpCertData = idpCertClient.getCertData(entityId, assertionInstant);

        if (listIdpCertData == null || listIdpCertData.size() > 0) {
            return null;
        }

        idpCertStorage.saveIdpCertData(assertionInstant, listIdpCertData);

        return listIdpCertData;
    }
}
