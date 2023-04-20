/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IdpCertData {

    private String entityId;
    private String tag;
    private List<String> certData;
}
