/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple;

import it.pagopa.tech.lollipop.consumer.exception.*;
import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiException;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.api.DefaultApi;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.model.CertData;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.model.EntitiesDescriptor;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.model.EntityDescriptor;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorage;
import it.pagopa.tech.lollipop.consumer.idp.storage.IdpCertStorageConfig;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class IdpCertSimpleClient implements IdpCertClient {

    private final DefaultApi defaultApi;

    private final IdpCertSimpleClientConfig entityConfig;
    private IdpCertStorage storage;

    @Inject
    public IdpCertSimpleClient(
            ApiClient client, IdpCertSimpleClientConfig entityConfig, IdpCertStorage storage) {
        this.defaultApi = new DefaultApi(client);
        this.entityConfig = entityConfig;
        this.storage = storage;
    }

    /**
     * Retrieve the certification data of the given entityId issued in the same timeframe as the
     * issue instant of the SAML assertion, first looking in the storage if enabled ({@link
     * IdpCertStorageConfig}) and then, if not found, through the client {@link IdpCertClient}. If
     * the storage is enabled ({@link IdpCertStorageConfig}) the IdpCertData will be stored, after
     * being retrieved by the client.
     *
     * @param entityId Identity Provider ID
     * @param instant Assertion Issue Instant
     * @return the certifications issued before and after the timestamp instant
     * @throws CertDataTagListNotFoundException if an error occurred retrieving the list of tags or
     *     filtering the tags with the instant
     * @throws CertDataNotFoundException if an error occurred retrieving the certification XML or if
     *     data for the given entityId were not found
     */
    @Override
    public List<IdpCertData> getCertData(String entityId, String instant)
            throws CertDataNotFoundException, CertDataTagListNotFoundException {
        List<IdpCertData> listCertData = new ArrayList<>();
        List<String> tagList;

        if (entityId == null || instant == null || entityId.isBlank() || instant.isBlank()) {
            throw new IllegalArgumentException("EntityID or Assertion Issue Instant missing");
        }

        if (entityConfig.getCieEntityId().contains(entityId)) {
            try {
                tagList = getCIETagList(instant);
            } catch (ApiException
                    | TagListSearchOutOfBoundException
                    | InvalidInstantFormatException e) {
                throw new CertDataTagListNotFoundException(
                        "Error retrieving certificate's tag list: " + e.getMessage(), e);
            }

            for (String tag : tagList) {
                try {
                    String storageTag = codifyStorageTag(tag, entityId);
                    IdpCertData certData = storage.getIdpCertData(storageTag);

                    if (certData == null) {
                        certData = getCIECertData(tag, entityId);
                    } else {
                        storage.saveIdpCertData(storageTag, certData);
                    }

                    listCertData.add(certData);
                } catch (ApiException | EntityIdNotFoundException e) {
                    throw new CertDataNotFoundException(
                            "Error retrieving certificate data for tag "
                                    + tag
                                    + ": "
                                    + e.getMessage(),
                            e);
                }
            }
        } else {
            try {
                tagList = getSPIDTagList(instant);
            } catch (ApiException
                    | TagListSearchOutOfBoundException
                    | InvalidInstantFormatException e) {
                throw new CertDataTagListNotFoundException(
                        "Error retrieving certificate's tag list: " + e.getMessage(), e);
            }

            for (String tag : tagList) {
                try {
                    String storageTag = codifyStorageTag(tag, entityId);
                    IdpCertData certData = storage.getIdpCertData(codifyStorageTag(tag, entityId));

                    if (certData == null) {
                        certData = getSPIDCertData(tag, entityId);
                    } else {
                        storage.saveIdpCertData(storageTag, certData);
                    }
                    listCertData.add(certData);
                } catch (ApiException | EntityIdNotFoundException e) {
                    throw new CertDataNotFoundException(
                            "Error retrieving certificate data for tag "
                                    + tag
                                    + ": "
                                    + e.getMessage(),
                            e);
                }
            }
        }
        return listCertData;
    }

    private List<String> getSPIDTagList(String instant)
            throws TagListSearchOutOfBoundException, InvalidInstantFormatException {
        List<String> responseAssertion;

        responseAssertion = this.defaultApi.idpKeysSpidGet();

        return getTagsFromInstant(responseAssertion, instant);
    }

    private IdpCertData getSPIDCertData(String tag, String entityId)
            throws EntityIdNotFoundException {
        CertData responseAssertion = null;

        responseAssertion = this.defaultApi.idpKeysSpidTagGet(tag);

        return getEntityData(
                ((EntitiesDescriptor) responseAssertion.getActualInstance()), tag, entityId);
    }

    private List<String> getCIETagList(String instant)
            throws TagListSearchOutOfBoundException, InvalidInstantFormatException {
        List<String> responseAssertion;

        responseAssertion = this.defaultApi.idpKeysCieGet();

        return getTagsFromInstant(responseAssertion, instant);
    }

    private IdpCertData getCIECertData(String tag, String entityId)
            throws EntityIdNotFoundException {
        CertData responseAssertion;

        responseAssertion = this.defaultApi.idpKeysCieTagGet(tag);

        return getEntityData(
                ((EntitiesDescriptor) responseAssertion.getActualInstance()), tag, entityId);
    }

    private IdpCertData getEntityData(EntitiesDescriptor data, String tag, String entityId)
            throws EntityIdNotFoundException {
        IdpCertData newData = new IdpCertData();

        for (EntityDescriptor entity : data.getEntityList()) {
            if (entity.getEntityID().equals(entityId)) {
                newData.setEntityId(entityId);
                newData.setTag(tag);
                newData.setCertData(entity.getSignature());

                return newData;
            }
        }

        throw new EntityIdNotFoundException("Cert for entityID " + entityId + " not found");
    }

    private List<String> getTagsFromInstant(List<String> tagList, String instant)
            throws TagListSearchOutOfBoundException, InvalidInstantFormatException {
        List<String> newTagList = new ArrayList<>();
        String latest = "latest";
        long longInstant;

        try {
            longInstant = Long.parseLong(instant);
        } catch (Exception e) {
            throw new InvalidInstantFormatException(
                    "The given insant " + instant + " is not a valid timestamp");
        }

        boolean latestRemoved = tagList.remove(latest);

        Collections.sort(tagList);

        if (latestRemoved) {
            tagList.add(latest);
        }

        int index = tagList.size() / 2;

        boolean notFound = true;
        while (notFound) {
            try {
                if (tagList.size() <= 2) {
                    String firstTimestamp = tagList.get(0);
                    if (firstTimestamp.equals(latest)
                            || Long.parseLong(firstTimestamp) <= longInstant) {
                        return tagList;
                    }
                }

                String upperTag = tagList.get(index);
                String lowerTag = tagList.get(index - 1);
                if (upperTag.equals(latest) || longInstant <= Long.parseLong(upperTag)) {
                    if (longInstant >= Long.parseLong(lowerTag)) {
                        notFound = false;
                        newTagList.add(upperTag);
                        newTagList.add(lowerTag);
                    } else {
                        index -= 1;
                    }
                } else {
                    index += 1;
                }
            } catch (Exception e) {
                throw new TagListSearchOutOfBoundException(
                        "Error finding the tags relative to assertion instant " + instant);
            }
        }

        return newTagList;
    }

    private String codifyStorageTag(String tag, String entityId) {
        return tag + entityId;
    }
}
