//package coursework.poterashki.controller;
//
//import coursework.poterashki.controller.dto.request.RequestProfile;
//import coursework.poterashki.controller.dto.response.ResponseProfile;
//import org.springframework.web.bind.annotation.*;
//import java.time.LocalDateTime;
//
//@RestController
//public class PersonController {
//
//    @GetMapping("/profile_data")
//    public ResponseProfile profile(@RequestParam String id) {
//        ResponseProfile responseProfile =  new ResponseProfile();
//        responseProfile.setName("Иван Иванов");
//        responseProfile.setPhone("+7 (999) 123-45-67");
//        responseProfile.setCity("Москва");
//        responseProfile.setEmail("ivan@example.com");
//        responseProfile.setDateCreating(LocalDateTime.now().minusDays(30));
//        return responseProfile;
//    }
//
//    @PostMapping("/update_profile")
//    public boolean profile(@RequestBody RequestProfile requestProfile) {
//        return true;
//    }
//}
