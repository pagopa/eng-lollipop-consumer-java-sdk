/* (C)2022-2023 */
package it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** EntitiesDescriptor */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class EntitiesDescriptor {

    @JsonProperty("EntityDescriptor")
    private List<EntityDescriptor> entityList;
}
