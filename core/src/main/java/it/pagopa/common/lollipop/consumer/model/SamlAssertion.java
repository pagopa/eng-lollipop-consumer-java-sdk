package it.pagopa.common.lollipop.consumer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SamlAssertion {

    private String assertionRef;
    private String inResponseTo;
    private Long notBefore;
}
