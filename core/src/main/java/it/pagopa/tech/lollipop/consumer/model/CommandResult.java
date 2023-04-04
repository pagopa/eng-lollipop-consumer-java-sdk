/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommandResult {

    private String resultCode;
    private String resultMessage;
}
