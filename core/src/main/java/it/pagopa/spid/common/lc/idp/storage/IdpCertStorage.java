package it.pagopa.spid.common.lc.idp.storage;

import it.pagopa.spid.common.lc.model.IdpCertData;

import java.util.List;

public interface IdpCertStorage {

    List<String> getTagList();
    void saveTagList(List<String> tagList);
    IdpCertData getIdpCertData(String tag);
    void saveIdpCertData(String tag);
}
