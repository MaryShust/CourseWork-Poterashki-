package coursework.poterashki.controller;

import coursework.poterashki.controller.dto.request.RequestAuth;
import coursework.poterashki.controller.dto.request.RequestRegistration;
import coursework.poterashki.controller.dto.response.ResponseAuth;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @PostMapping("/auth")
    public ResponseAuth auth(@RequestBody RequestAuth requestAuth) {
        ResponseAuth responseAuth =  new ResponseAuth();
        responseAuth.setName("Debil");
        responseAuth.setId("123");
        return responseAuth;
    }

    @PostMapping("/registration")
    public ResponseAuth auth(@RequestBody RequestRegistration requestRegistration) {
        ResponseAuth responseAuth =  new ResponseAuth();
        responseAuth.setName("Debil");
        responseAuth.setId("123");
        return responseAuth;
    }
}
