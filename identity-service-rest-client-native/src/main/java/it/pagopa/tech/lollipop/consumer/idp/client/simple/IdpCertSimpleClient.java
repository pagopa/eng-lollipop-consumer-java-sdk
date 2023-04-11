/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple;

import it.pagopa.tech.lollipop.consumer.idp.client.IdpCertClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.ApiException;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.api.DefaultApi;
import it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.model.CertData;
import it.pagopa.tech.lollipop.consumer.model.IdpCertData;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class IdpCertSimpleClient implements IdpCertClient {

    private final ApiClient apiClient;
    private final DefaultApi defaultApi;

    @Inject
    public IdpCertSimpleClient(ApiClient client) {
        this.apiClient = client;
        this.defaultApi = new DefaultApi(client);
    }

    /**
     * @param entityId
     * @param instant
     * @return
     */
    @Override
    public List<IdpCertData> getCertData(String entityId, String instant) {
        List<IdpCertData> listCertData = new ArrayList<>();

        if (entityId.equals("https://idserver.servizicie.interno.gov.it/idp/profile/SAML2/POST/SSO")) { //TODO inserire entityID in classe di configurazione
            List<String> tagList = getCIETagList(instant);
            for(String tag: tagList){
                //TODO recupero storage
                IdpCertData certData = getCIECertData(tag);
                listCertData.add(certData);
            }
        } else {
            List<String> tagList = getSPIDTagList(instant);
            for(String tag: tagList){
                //TODO recupero storage
                IdpCertData certData = getSPIDCertData(tag, entityId);
                listCertData.add(certData);
            }
        }
        return listCertData;
    }

    private List<String> getSPIDTagList(String instant) {
        List<String> responseAssertion = new ArrayList<>();

        try {
            responseAssertion = this.defaultApi.idpKeysSpidGet();
        } catch (ApiException e) {
            /*throw new LollipopAssertionNotFoundException(
                    "Error retrieving idp cert list: " + e.getMessage(), e);*/
        }

        //TODO gestire filtro tag (prendere quello subito prima e subito dopo rispetto all' "instant")

        return responseAssertion;
    }

    private IdpCertData getSPIDCertData(String tag, String entityId) {
        CertData responseAssertion;

        try {
            responseAssertion = this.defaultApi.idpKeysSpidTagGet(tag);
        } catch (ApiException e) {
            /*throw new LollipopAssertionNotFoundException(
                    "Error retrieving assertion: " + e.getMessage(), e);*/
        }

        //TODO gestire filtraggio xml per entityId

        return null;
    }

    private List<String> getCIETagList(String instant) {
        List<String> responseAssertion = new ArrayList<>();

        try {
            responseAssertion = this.defaultApi.idpKeysCieGet();
        } catch (ApiException e) {
            /*throw new LollipopAssertionNotFoundException(
                    "Error retrieving idp cert list: " + e.getMessage(), e);*/
        }

        //TODO gestire filtro tag (prendere quello subito prima e subito dopo rispetto all' "instant")

        return responseAssertion;
    }

    private IdpCertData getCIECertData(String tag) {
        CertData responseAssertion;

        try {
            responseAssertion = this.defaultApi.idpKeysCieTagGet(tag);
        } catch (ApiException e) {
            /*throw new LollipopAssertionNotFoundException(
                    "Error retrieving assertion: " + e.getMessage(), e);*/
        }

        return null;
    }
}
