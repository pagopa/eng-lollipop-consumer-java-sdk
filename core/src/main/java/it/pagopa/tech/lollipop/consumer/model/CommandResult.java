/* (C)2023-2025 */
package it.pagopa.tech.lollipop.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommandResult {

    private String resultCode;
    private String resultMessage;
    private String name; // user name
    private String familyName; // user familyName

    public CommandResult(String resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }
    public CommandResult(String resultCode, String resultMessage, String name, String familyName) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.name = name;
        this.familyName = familyName;
    }
}
