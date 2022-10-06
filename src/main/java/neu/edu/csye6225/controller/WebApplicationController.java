package neu.edu.csye6225.controller;

import neu.edu.csye6225.model.AccountDetails;
import neu.edu.csye6225.service.UnauthorizedException;
import neu.edu.csye6225.service.WebApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import org.json.JSONObject;

@Controller
@RequestMapping(path = "/v1/account")
public class WebApplicationController {

    @Autowired
    private WebApplicationService webApplicationService;

    @GetMapping (path = "/{accountId}", produces = "application/json")
    public ResponseEntity<String> getAccountDetails(@RequestHeader(value = "Authorization") String oauth, @PathVariable String accountId){
        String authorization = oauthEncode(oauth);
        String[] headerAuth = authorization.split(":");
        String email = accountId;
        String password = headerAuth[1];
        try {
            AccountDetails accountDetails = webApplicationService.getAccountDetails(email, password);
            JSONObject entity = webApplicationService.getJSON(accountDetails);
            ResponseEntity<String> responseEntity = new ResponseEntity<>(entity.toString(), HttpStatus.OK);
            return responseEntity;
        }catch(NullPointerException e){
            throw new UnauthorizedException();
        }
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> accountRegister(@RequestBody AccountDetails accountDetails){
        ResponseEntity<String> responseEntity;
        JSONObject entity = new JSONObject();
        try{
            if(accountDetails != null && webApplicationService.accountRegister(accountDetails)){
                AccountDetails details = webApplicationService.getAccountDetails(accountDetails.getUsername(), accountDetails.getPassword());
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

    @PutMapping(path = "/{accountId}")
    public ResponseEntity<String> accountUpdate(@RequestHeader (value = "Authorization") String oauth, @RequestBody AccountDetails accountDetails, @PathVariable String accountId){
        ResponseEntity<String> responseEntity;
        String authorization = oauthEncode(oauth);
        String[] headerAuth = authorization.split(":");
        String email = accountId;
        String password = headerAuth[1];
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
        assert oauth.substring(0, 6).equals("Basic");
        String basicAuthStr = new String(Base64.getDecoder().decode(oauth.substring(6).getBytes()));
        return basicAuthStr;
    }
}
