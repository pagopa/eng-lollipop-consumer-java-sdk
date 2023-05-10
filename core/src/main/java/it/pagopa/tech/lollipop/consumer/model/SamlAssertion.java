/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SamlAssertion {

    private String assertionRef;
    private String assertionData;
}
