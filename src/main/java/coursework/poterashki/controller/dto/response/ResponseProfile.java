package coursework.poterashki.controller.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ResponseProfile {

    private String id;
    private String name;
    private String phone;
    private String city;
    private String email;
    private LocalDateTime dateCreating;
}