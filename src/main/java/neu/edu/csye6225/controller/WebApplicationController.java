package neu.edu.csye6225.controller;

import com.timgroup.statsd.StatsDClient;
import lombok.extern.slf4j.Slf4j;
import neu.edu.csye6225.model.AccountDetails;
import neu.edu.csye6225.service.EmailAuthService;
import neu.edu.csye6225.service.ForbiddenException;
import neu.edu.csye6225.service.UnauthorizedException;
import neu.edu.csye6225.service.WebApplicationService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@Slf4j
@RequestMapping(path = "/v1/account")
public class WebApplicationController {

    @Autowired
    private StatsDClient statsDClient;

    private WebApplicationService webApplicationService;

    @Resource
    private EmailAuthService emailAuthService;

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
                emailAuthService.trigger(details);
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

    @GetMapping("/v2/verifyUserEmail")
    @ResponseBody
    public ResponseEntity<?> verifyUser(@RequestParam String email, @RequestParam String token) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(token)) {
            throw new ForbiddenException();
        }
        if (!emailAuthService.validate(email, token)) {
            throw new UnauthorizedException();
        }
        AccountDetails accountDetails = webApplicationService.getAccountDetails(email);
        accountDetails.setAuthenticated(true);
        return webApplicationService.accountUpdate(accountDetails.getUsername(), accountDetails.getPassword(), accountDetails, System.currentTimeMillis());
    }
}
