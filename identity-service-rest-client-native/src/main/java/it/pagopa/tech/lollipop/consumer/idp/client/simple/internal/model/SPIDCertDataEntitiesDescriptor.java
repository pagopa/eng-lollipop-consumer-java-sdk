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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;


/**
 * SPIDCertDataEntitiesDescriptor
 */
@JsonPropertyOrder({
  SPIDCertDataEntitiesDescriptor.JSON_PROPERTY_ENTITY_DESCRIPTOR
})
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2023-04-11T16:21:49.277208500+02:00[Europe/Paris]")
public class SPIDCertDataEntitiesDescriptor {
  public static final String JSON_PROPERTY_ENTITY_DESCRIPTOR = "entityDescriptor";
  private List<EntityDescriptor> entityDescriptor;

  public SPIDCertDataEntitiesDescriptor() { 
  }

  public SPIDCertDataEntitiesDescriptor entityDescriptor(List<EntityDescriptor> entityDescriptor) {
    this.entityDescriptor = entityDescriptor;
    return this;
  }

  public SPIDCertDataEntitiesDescriptor addEntityDescriptorItem(EntityDescriptor entityDescriptorItem) {
    if (this.entityDescriptor == null) {
      this.entityDescriptor = new ArrayList<>();
    }
    this.entityDescriptor.add(entityDescriptorItem);
    return this;
  }

   /**
   * Get entityDescriptor
   * @return entityDescriptor
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_ENTITY_DESCRIPTOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<EntityDescriptor> getEntityDescriptor() {
    return entityDescriptor;
  }


  @JsonProperty(JSON_PROPERTY_ENTITY_DESCRIPTOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setEntityDescriptor(List<EntityDescriptor> entityDescriptor) {
    this.entityDescriptor = entityDescriptor;
  }


  /**
   * Return true if this SPIDCertData_entitiesDescriptor object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SPIDCertDataEntitiesDescriptor spIDCertDataEntitiesDescriptor = (SPIDCertDataEntitiesDescriptor) o;
    return Objects.equals(this.entityDescriptor, spIDCertDataEntitiesDescriptor.entityDescriptor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entityDescriptor);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SPIDCertDataEntitiesDescriptor {\n");
    sb.append("    entityDescriptor: ").append(toIndentedString(entityDescriptor)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

  /**
   * Convert the instance into URL query string.
   *
   * @return URL query string
   */
  public String toUrlQueryString() {
    return toUrlQueryString(null);
  }

  /**
   * Convert the instance into URL query string.
   *
   * @param prefix prefix of the query string
   * @return URL query string
   */
  public String toUrlQueryString(String prefix) {
    String suffix = "";
    String containerSuffix = "";
    String containerPrefix = "";
    if (prefix == null) {
      // style=form, explode=true, e.g. /pet?name=cat&type=manx
      prefix = "";
    } else {
      // deepObject style e.g. /pet?id[name]=cat&id[type]=manx
      prefix = prefix + "[";
      suffix = "]";
      containerSuffix = "]";
      containerPrefix = "[";
    }

    StringJoiner joiner = new StringJoiner("&");

    // add `entityDescriptor` to the URL query string
    if (getEntityDescriptor() != null) {
      for (int i = 0; i < getEntityDescriptor().size(); i++) {
        if (getEntityDescriptor().get(i) != null) {
          joiner.add(getEntityDescriptor().get(i).toUrlQueryString(String.format("%sentityDescriptor%s%s", prefix, suffix,
          "".equals(suffix) ? "" : String.format("%s%d%s", containerPrefix, i, containerSuffix))));
        }
      }
    }

    return joiner.toString();
  }
}

