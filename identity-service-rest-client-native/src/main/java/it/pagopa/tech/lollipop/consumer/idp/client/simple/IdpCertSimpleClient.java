/* (C)2023-2025 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple;

import it.pagopa.tech.lollipop.consumer.exception.*;
import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.exception.TagListSearchOutOfBoundException;
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
import java.util.logging.Level;
import javax.inject.Inject;
import lombok.extern.java.Log;

@Log
public class IdpCertSimpleClient implements IdpCertClient {

    private final DefaultApi defaultApi;

    private final IdpCertSimpleClientConfig entityConfig;
    private final IdpCertStorage storage;

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
     * @throws CertDataNotFoundException if an error occurred retrieving the certification XML or if
     *     data for the given entityId were not found
     */
    @Override
    public List<IdpCertData> getCertData(String entityId, String instant)
            throws CertDataNotFoundException {
        List<IdpCertData> listCertData = new ArrayList<>();

        if (entityId == null || instant == null || entityId.isBlank() || instant.isBlank()) {
            throw new IllegalArgumentException("EntityID or Assertion Issue Instant missing");
        }

        if (entityConfig.getCieEntityId().contains(entityId)) {
            getCieCerts(entityId, instant, listCertData);
        } else {
            getSpidCerts(entityId, instant, listCertData);
        }
        return listCertData;
    }

    private void getCieCerts(String entityId, String instant, List<IdpCertData> listCertData)
            throws CertDataNotFoundException {
        List<String> tagList;
        try {
            tagList = getCIETagList(instant);
        } catch (ApiException
                | TagListSearchOutOfBoundException
                | InvalidInstantFormatException e) {
            log.log(Level.WARNING, "Error retrieving certificate's tag list: " + e.getMessage(), e);
            throw new CertDataNotFoundException(
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
                log.log(Level.WARNING, "Error retrieving certificate data for tag " + tag + ": " + e.getMessage(), e);
                throw new CertDataNotFoundException(
                        "Error retrieving certificate data for tag " + tag + ": " + e.getMessage(),
                        e);
            }
        }
    }

    private void getSpidCerts(String entityId, String instant, List<IdpCertData> listCertData)
            throws CertDataNotFoundException {
        List<String> tagList;
        try {
            tagList = getSPIDTagList(instant);
        } catch (ApiException
                | TagListSearchOutOfBoundException
                | InvalidInstantFormatException e) {
            log.log(Level.WARNING, "Error retrieving certificate's tag list: " + e.getMessage(), e);
            throw new CertDataNotFoundException(
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
                log.log(
                        Level.WARNING,
                        "Error retrieving certificate data for tag " + tag + ": " + e.getMessage(),
                        e);
                throw new CertDataNotFoundException(
                        "Error retrieving certificate data for tag " + tag + ": " + e.getMessage(),
                        e);
            }
        }
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
                newData.setCertData(entity.getSignatureList());

                return newData;
            }
        }

        throw new EntityIdNotFoundException("Cert for entityID " + entityId + " not found");
    }

    private List<String> getTagsFromInstant(List<String> tagList, String instant)
            throws TagListSearchOutOfBoundException, InvalidInstantFormatException {
        List<String> newTagList = new ArrayList<>();
        String latest = "latest";

        long longInstant = getLongInstant(instant);

        boolean latestRemoved = tagList.remove(latest);

        Collections.sort(tagList);

        ifLatestRemovedAddLatest(tagList, latest, latestRemoved);

        int index = tagList.size() / 2;

        boolean notFound = true;
        while (notFound) {
            try {
                if (isTagListAlreadyFiltered(tagList, latest, longInstant)) {
                    return tagList;
                }

                String upperTag = tagList.get(index);
                String lowerTag = tagList.get(index - 1);
                if (upperTagIsHigherOrLatest(latest, longInstant, upperTag)) {
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
                log.log(Level.WARNING, "Error finding the tags relative to assertion instant "
                                + instant
                                + ": "
                                + e.getMessage(), e);
                throw new TagListSearchOutOfBoundException(
                        "Error finding the tags relative to assertion instant " + instant);
            }
        }

        return newTagList;
    }

    private static boolean isTagListAlreadyFiltered(
            List<String> tagList, String latest, long longInstant) {
        if (tagList.size() <= 2) {
            String firstTimestamp = tagList.get(0);
            return firstTimestamp.equals(latest) || Long.parseLong(firstTimestamp) <= longInstant;
        }
        return false;
    }

    private static long getLongInstant(String instant) throws InvalidInstantFormatException {
        long longInstant;
        try {
            longInstant = Long.parseLong(instant);
        } catch (Exception e) {
            log.log(
                    Level.WARNING,
                    "The given instant " + instant + " is not a valid timestamp: " + e.getMessage(),
                    e);
            throw new InvalidInstantFormatException(
                    "The given instant " + instant + " is not a valid timestamp");
        }
        return longInstant;
    }

    private static boolean upperTagIsHigherOrLatest(
            String latest, long longInstant, String upperTag) {
        return upperTag.equals(latest) || longInstant <= Long.parseLong(upperTag);
    }

    private static void ifLatestRemovedAddLatest(
            List<String> tagList, String latest, boolean latestRemoved) {
        if (latestRemoved) {
            tagList.add(latest);
        }
    }

    private String codifyStorageTag(String tag, String entityId) {
        return tag + entityId;
    }
}
