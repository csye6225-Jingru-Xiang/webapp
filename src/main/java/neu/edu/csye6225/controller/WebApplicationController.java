package neu.edu.csye6225.controller;

import neu.edu.csye6225.model.AccountDetails;
import neu.edu.csye6225.service.ForbiddenException;
import neu.edu.csye6225.service.UnauthorizedException;
import neu.edu.csye6225.service.WebApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.json.JSONObject;

@Controller
@RequestMapping(path = "/v1/account")
public class WebApplicationController {

    @Autowired
    private WebApplicationService webApplicationService;

    @GetMapping (path = "/{accountId}", produces = "application/json")
    public ResponseEntity<String> getAccountDetails(@RequestHeader(value = "Authorization") String oauth, @PathVariable String accountId){
        try {
            String authorization = webApplicationService.oauthEncode(oauth);
            String[] headerAuth = authorization.split(":");
            String email = accountId;
            String password = headerAuth[1];
            AccountDetails accountDetails = webApplicationService.getAccountDetails(email, password);
            JSONObject entity = webApplicationService.getJSON(accountDetails);
            ResponseEntity<String> responseEntity = new ResponseEntity<>(entity.toString(), HttpStatus.OK);
            return responseEntity;
        } catch (NullPointerException e) {
            throw new UnauthorizedException();
        } catch (Exception e){
            throw new ForbiddenException();
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
            throw new ForbiddenException();
        }
    }

    @PutMapping(path = "/{accountId}")
    public ResponseEntity<String> accountUpdate(@RequestHeader (value = "Authorization") String oauth, @RequestBody AccountDetails accountDetails, @PathVariable String accountId){
        try{
        String authorization = webApplicationService.oauthEncode(oauth);
        String[] headerAuth = authorization.split(":");
        String email = accountId;
        String password = headerAuth[1];
        return webApplicationService.accountUpdate(email, password, accountDetails);
        }catch (Exception e){
            throw new ForbiddenException();
        }
    }

}
