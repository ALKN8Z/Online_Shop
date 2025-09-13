package org.example.customerapplication.client.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ClientBadRequestException extends RuntimeException {
    List<String> errors;

    public ClientBadRequestException(Throwable cause, List<String> errors) {
        super(cause);
        this.errors = errors;
    }
}
