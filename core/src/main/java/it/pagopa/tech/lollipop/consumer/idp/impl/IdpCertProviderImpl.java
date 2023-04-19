/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.impl;

import it.pagopa.tech.lollipop.consumer.exception.CertDataNotFoundException;
import it.pagopa.tech.lollipop.consumer.idp.IdpCertProvider;
import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClient;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import java.util.List;
import javax.inject.Inject;

public class IdpCertProviderImpl implements IdpCertProvider {

    private IdpCertClient idpCertClient;

    @Inject
    public IdpCertProviderImpl(IdpCertClient idpCertClient) {
        this.idpCertClient = idpCertClient;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Retrieve the certification data of the given entityId issued in the same timeframe as the
     * issue instant of the SAML assertion, first looking in the storage if enabled ({@link
     * IdpCertStorageConfig}) and then, if not found, through the client {@link IdpCertClient}. If
     * the storage is enabled ({@link IdpCertStorageConfig}) the IdpCertData will be stored, after
     * being retrieved by the client.
     *
     * @param entityId Identity Provider ID
     * @param assertionInstant Assertion Issue Instant
     * @return the certifications issued before and after the timestamp instant
     * @throws CertDataNotFoundException if an error occurred retrieving the certification XML or if
     *     data for the given entityId were not found
     */
    @Override
    public List<IdpCertData> getIdpCertData(String assertionInstant, String entityId)
            throws CertDataNotFoundException {
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

        return idpCertClient.getCertData(entityId, assertionInstant);
    }
}
