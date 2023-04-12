/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple;

import it.pagopa.tech.lollipop.consumer.exception.CertDataNotFoundException;
import it.pagopa.tech.lollipop.consumer.exception.CertDataTagListNotFoundException;
import it.pagopa.tech.lollipop.consumer.exception.EntityIdNotFoundException;
import it.pagopa.tech.lollipop.consumer.exception.TagListSearchOutOfBoundException;
import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiException;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.api.DefaultApi;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.model.CertData;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.model.EntitiesDescriptor;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.model.EntityDescriptor;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IdpCertSimpleClient implements IdpCertClient {

    private final ApiClient apiClient;
    private final DefaultApi defaultApi;

    private static final List<String> CIE_ENTITY_IDS = new ArrayList<>(List.of("https://idserver.servizicie.interno.gov.it/idp/profile/SAML2/POST/SSO"));

    @Inject
    public IdpCertSimpleClient(ApiClient client) {
        this.apiClient = client;
        this.defaultApi = new DefaultApi(client);
    }

    /**
     * Retrieve the certification data of the given entityId issued
     * in the same timeframe as the issue instant of the SAML assertion
     *
     * @param entityId Identity Provider ID
     * @param instant  Assertion Issue Instant
     * @return the certifications issued before and after the timestamp instant
     * @throws CertDataTagListNotFoundException if an error occurred retrieving the list of tags or filtering the tags with the instant
     * @throws CertDataNotFoundException        if an error occurred retrieving the certification XML or if data for the given entityId were not found
     */
    @Override
    public List<IdpCertData> getCertData(String entityId, String instant) throws CertDataNotFoundException, CertDataTagListNotFoundException {
        List<IdpCertData> listCertData = new ArrayList<>();
        List<String> tagList;

        if (entityId == null || instant == null || entityId.isBlank() || instant.isBlank()) {
            throw new IllegalArgumentException("EntityID or Assertion Issue Instant missing");
        }

        if (CIE_ENTITY_IDS.contains(entityId)) { //TODO inserire entityID in classe di configurazione

            try {
                tagList = getCIETagList(instant);
            } catch (ApiException | TagListSearchOutOfBoundException e) {
                throw new CertDataTagListNotFoundException("Error retrieving certificate's tag list: " + e.getMessage(), e);
            }

            for (String tag : tagList) {
                try {
                    IdpCertData certData = getCIECertData(tag, entityId);

                    if (certData != null) {
                        listCertData.add(certData);
                    }
                } catch (ApiException | EntityIdNotFoundException e) {
                    throw new CertDataNotFoundException("Error retrieving certificate data for tag " + tag + ": " + e.getMessage(), e);
                }

            }
        } else {
            try {
                tagList = getSPIDTagList(instant);
            } catch (ApiException | TagListSearchOutOfBoundException e) {
                throw new CertDataTagListNotFoundException("Error retrieving certificate's tag list: " + e.getMessage(), e);
            }

            for (String tag : tagList) {
                try {
                    IdpCertData certData = getSPIDCertData(tag, entityId);

                    if (certData != null) {
                        listCertData.add(certData);
                    }
                } catch (ApiException | EntityIdNotFoundException e) {
                    throw new CertDataNotFoundException("Error retrieving certificate data for tag " + tag + ": " + e.getMessage(), e);
                }

            }
        }
        return listCertData;
    }

    private List<String> getSPIDTagList(String instant) throws TagListSearchOutOfBoundException {
        List<String> responseAssertion;

        responseAssertion = this.defaultApi.idpKeysSpidGet();

        return getTagsFromInstant(responseAssertion, instant);
    }

    private IdpCertData getSPIDCertData(String tag, String entityId) throws EntityIdNotFoundException {
        CertData responseAssertion = null;

        responseAssertion = this.defaultApi.idpKeysSpidTagGet(tag);

        return getEntityData(((EntitiesDescriptor) responseAssertion.getActualInstance()), tag, entityId);
    }

    private List<String> getCIETagList(String instant) throws TagListSearchOutOfBoundException {
        List<String> responseAssertion;

        responseAssertion = this.defaultApi.idpKeysCieGet();

        return getTagsFromInstant(responseAssertion, instant);
    }

    private IdpCertData getCIECertData(String tag, String entityId) throws EntityIdNotFoundException {
        CertData responseAssertion;

        responseAssertion = this.defaultApi.idpKeysCieTagGet(tag);

        return getEntityData(((EntitiesDescriptor) responseAssertion.getActualInstance()), tag, entityId);
    }

    private IdpCertData getEntityData(EntitiesDescriptor data, String tag, String entityId) throws EntityIdNotFoundException {
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

    private List<String> getTagsFromInstant(List<String> tagList, String instant) throws TagListSearchOutOfBoundException {
        List<String> newTagList = new ArrayList<>();

        if (tagList.size() <= 2) {
            return tagList;
        }

        int index = tagList.size() / 2;
        boolean latestRemoved = tagList.remove("latest");

        Collections.sort(tagList);

        if (latestRemoved) {
            tagList.add("latest");
        }

        boolean notFound = true;
        while (notFound) {
            try {
                String upperTag = tagList.get(index);
                String lowerTag = tagList.get(index - 1);
                if (upperTag.equals("latest") || Long.valueOf(instant) <= Long.valueOf(upperTag)) {
                    if (Long.valueOf(instant) >= Long.valueOf(lowerTag)) {
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
                throw new TagListSearchOutOfBoundException("Error finding the tags relative to assertion instant " + instant);
            }
        }

        return newTagList;
    }
}
