package it.pagopa.tech.lollipop.consumer.assertion.client.simple;

import it.pagopa.tech.lollipop.consumer.assertion.client.AssertionClient;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.ApiClient;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.ApiException;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.api.DefaultApi;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.model.AssertionRef;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.model.LCUserInfo;
import it.pagopa.tech.lollipop.consumer.assertion.client.simple.internal.model.SamlUserInfo;
import it.pagopa.tech.lollipop.consumer.exception.LollipopAssertionNotFoundException;
import it.pagopa.tech.lollipop.consumer.model.SamlAssertion;

import javax.inject.Inject;

public class AssertionSimpleClient implements AssertionClient {

    ApiClient apiClient;
    DefaultApi defaultApi;

    @Inject
    public AssertionSimpleClient(ApiClient client) {
        this.apiClient = client;
        this.defaultApi = new DefaultApi(client);
    }

    @Override
    public SamlAssertion getAssertion(String jwt, String assertionRef) throws LollipopAssertionNotFoundException {
        AssertionRef ref = new AssertionRef(assertionRef);

        if(jwt.isEmpty() || assertionRef.isEmpty()){
            throw new IllegalArgumentException("Jwt or Assertion Ref missing");
        }

        LCUserInfo responseAssertion = new LCUserInfo();

        try{
            responseAssertion = this.defaultApi.getAssertion(ref, jwt);
        }
        catch(ApiException e){
            throw new LollipopAssertionNotFoundException("Error retrieving assertion: "+e.getMessage(), e);
        }

        if(responseAssertion.getActualInstance().getClass().equals(SamlUserInfo.class)){
                SamlAssertion response = new SamlAssertion();
                SamlUserInfo data = (SamlUserInfo)responseAssertion.getActualInstance();
                String assertionData = data.getResponseXml();
                response.setAssertionRef(assertionRef);
               response.setAssertionData(assertionData);
                return response;
        }

        //TODO handle oidc assertion

        return null;
    }
}
