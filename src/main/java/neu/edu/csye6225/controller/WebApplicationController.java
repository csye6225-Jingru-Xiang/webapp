package neu.edu.csye6225.controller;

import neu.edu.csye6225.WebApplication;
import neu.edu.csye6225.model.AccountDetails;
import neu.edu.csye6225.service.UnauthorizedException;
import neu.edu.csye6225.service.WebApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import org.json.JSONObject;

@Controller
public class WebApplicationController {

    private WebApplicationService webApplicationService;

    @Autowired
    public WebApplicationController(WebApplicationService webApplicationService) {
        this.webApplicationService = webApplicationService;
    }

    @GetMapping(value = "/v1/account/", produces = "application/json")
    public ResponseEntity<String> getAccountDetails(@RequestHeader(value = "Authorization") String oauth){
        String[] details = oauthEncode(oauth).split(":");
        String email = details[0];
        String password = details[1];
        try {
            AccountDetails accountDetails = webApplicationService.getAccountDetails(email, password);
            JSONObject entity = webApplicationService.getJSON(accountDetails);
            ResponseEntity<String> responseEntity = new ResponseEntity<>(entity.toString(), HttpStatus.OK);
            return responseEntity;
        }catch(NullPointerException e){
            throw new UnauthorizedException();
        }
    }

    @PostMapping(value = "/v1/account", produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> accountRegister(@RequestBody AccountDetails accountDetails){
        ResponseEntity<String> responseEntity;
        JSONObject entity = new JSONObject();
        try{
            if(accountDetails != null && webApplicationService.accountRegister(accountDetails)){
                AccountDetails details = webApplicationService.getAccountDetails(accountDetails.getEmail(), accountDetails.getPassword());
                entity = webApplicationService.getJSON(details);
                responseEntity = new ResponseEntity<>(entity.toString(), HttpStatus.CREATED);
            }else{
                entity.put("error", "User Exists or Registration Details Can Not Be Blank");
                responseEntity = new ResponseEntity<>(entity.toString(), HttpStatus.BAD_REQUEST);
            }
            return responseEntity;
        }catch(Exception e){
            entity.put("error", "Post Request Failed");
            responseEntity = new ResponseEntity<>(entity.toString(), HttpStatus.FORBIDDEN);
            return responseEntity;
        }
    }

    @PutMapping(value = "/v1/user/")
    public ResponseEntity<String> accountUpdate(@RequestHeader (value = "Authorization") String oauth, @RequestBody AccountDetails accountDetails){
        ResponseEntity<String> responseEntity;
        String[] details = oauthEncode(oauth).split(":");
        String email = details[0];
        String password = details[1];
        String msg = webApplicationService.accountUpdate(email, password, accountDetails);
        if(msg.equals("Success")){
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        }else if(msg.equals("No Content")){
            responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }else if(msg.equals("Unauthorized")){
            responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else if(msg.equals("Bad Request")){
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }

    // Decode Base64 Token
    public String oauthEncode(String oauth){
        String decodedString = new String(Base64.getDecoder().decode(oauth.substring(6).getBytes()));
        return decodedString;
    }
}
