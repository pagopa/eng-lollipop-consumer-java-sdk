/*
 * identity-services
 * Client used to retrieve the public keys from the identity provider
 *
 * The version of the OpenAPI document: 2022-09-06T20:08:39Z
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package it.pagopa.tech.lollipop.consumer.idp.client.simple.internal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * EntitiesDescriptor
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class EntitiesDescriptor {

  @JsonProperty("EntityDescriptor")
  private List<EntityDescriptor> entityList;

}

