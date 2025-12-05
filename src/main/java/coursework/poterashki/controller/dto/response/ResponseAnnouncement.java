package coursework.poterashki.controller.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ResponseAnnouncement {
    private String id;
    private String title;
    private String category;
    private String description;
    private String city;
    private String address;
    private LocalDate lostDate;
    private String color;
    private Integer reward;
    private String photoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private String userId;
    private String userName;
    private List<ResponseProfile> respondedUsers;
    private Boolean isFound; // true - нашел, false - потерял
}