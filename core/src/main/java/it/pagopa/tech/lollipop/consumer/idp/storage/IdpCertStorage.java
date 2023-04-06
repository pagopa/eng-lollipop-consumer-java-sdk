/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.storage;

import it.pagopa.tech.lollipop.consumer.model.IdpCertData;
import java.util.List;

public interface IdpCertStorage {

    List<String> getTagList();

    void saveTagList(List<String> tagList);

    IdpCertData getIdpCertData(String tag);

    void saveIdpCertData(String tag);
}
