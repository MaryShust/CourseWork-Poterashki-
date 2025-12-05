package coursework.poterashki.controller.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestRegistration {
    private String userName;
    private String userEmail;
    private String password;
}