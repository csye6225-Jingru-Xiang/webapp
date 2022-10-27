package neu.edu.csye6225.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HeathEndpointController {

    @GetMapping(path = "/healthz")
    public ResponseEntity<String> getHealthEndpoint() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        return responseEntity;
    }
}
