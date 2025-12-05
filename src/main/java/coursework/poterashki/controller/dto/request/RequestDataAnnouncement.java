package coursework.poterashki.controller.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class RequestDataAnnouncement {
    private String title;
    private String category;
    private String description;
    private String city;
    private String address;
    private LocalDate lostDate;
    private String color;
    private Integer reward;
    private String userId;
    private String userName;
    private Boolean isFound;
}