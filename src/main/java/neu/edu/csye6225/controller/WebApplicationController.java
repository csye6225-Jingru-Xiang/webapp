package neu.edu.csye6225.controller;

import com.timgroup.statsd.StatsDClient;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequestMapping(path = "/v1/account")
public class WebApplicationController {

    @Autowired
    private StatsDClient statsDClient;

    private WebApplicationService webApplicationService;

    public WebApplicationController(WebApplicationService webApplicationService) {
        this.webApplicationService = webApplicationService;
    }


    @GetMapping (path = "/{accountId}", produces = "application/json")
    public ResponseEntity<String> getAccountDetails(@RequestHeader(value = "Authorization") String oauth, @PathVariable String accountId){
        long startTime = System.currentTimeMillis();
        statsDClient.incrementCounter("endpoint.user.http.get");
        log.info("Hit endpoint.user.http.get successfully");
        try {
            String authorization = webApplicationService.oauthEncode(oauth);
            String[] headerAuth = authorization.split(":");
            String email = accountId;
            String password = headerAuth[1];
            AccountDetails accountDetails = webApplicationService.getAccountDetails(email, password);
            JSONObject entity = webApplicationService.getJSON(accountDetails);
            ResponseEntity<String> responseEntity = new ResponseEntity<>(entity.toString(), HttpStatus.OK);
            statsDClient.recordExecutionTime("endpoint.user.http.post.timer", System.currentTimeMillis() - startTime);
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
        long startTime = System.currentTimeMillis();
        statsDClient.incrementCounter("endpoint.user.http.post");
        log.info("Hit endpoint.user.http.post successfully");
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
            statsDClient.recordExecutionTime("endpoint.user.http.post.timer", System.currentTimeMillis() - startTime);
            return responseEntity;
        }catch(Exception e){
            throw new ForbiddenException();
        }
    }

    @PutMapping(path = "/{accountId}")
    public ResponseEntity<String> accountUpdate(@RequestHeader (value = "Authorization") String oauth, @RequestBody AccountDetails accountDetails, @PathVariable String accountId){
        long startTime = System.currentTimeMillis();
        statsDClient.incrementCounter("endpoint.user.http.put");
        log.info("Hit endpoint.user.http.put successfully");
        try{
        String authorization = webApplicationService.oauthEncode(oauth);
        String[] headerAuth = authorization.split(":");
        String email = accountId;
        String password = headerAuth[1];
        return webApplicationService.accountUpdate(email, password, accountDetails, startTime);
        }catch (Exception e){
            throw new ForbiddenException();
        }
    }

}
