package coursework.poterashki.controller.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestAuth {
    private String userEmail;
    private String password;
}