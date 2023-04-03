package it.pagopa.common.lollipop.consumer.idp.storage;

import it.pagopa.common.lollipop.consumer.model.IdpCertData;

import java.util.List;

public interface IdpCertStorage {

    List<String> getTagList();
    void saveTagList(List<String> tagList);
    IdpCertData getIdpCertData(String tag);
    void saveIdpCertData(String tag);
}
