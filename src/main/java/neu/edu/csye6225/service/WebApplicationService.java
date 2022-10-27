package neu.edu.csye6225.service;

import neu.edu.csye6225.repository.WebApplicationRepository;
import neu.edu.csye6225.model.AccountDetails;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class WebApplicationService {

    @Autowired
    private WebApplicationRepository webApplicationRepository;


    public AccountDetails getAccountDetails(String email, String password) {
        if (StringUtils.isEmpty(email) && StringUtils.isEmpty(password)) {
            return null;
        }
        AccountDetails details = webApplicationRepository.findByUsername(email);
        if (details != null && BCrypt.checkpw(password, details.getPassword())) {
            return details;
        } else if(password.equals(details.getPassword())) {
            return details;
        }else{
            return null;
        }
    }

    public JSONObject getJSON(AccountDetails accountDetails) {
        JSONObject entity = new JSONObject();
        entity.put("id", accountDetails.getUuid());
        entity.put("username", accountDetails.getUsername());
        entity.put("first_name", accountDetails.getFirstName());
        entity.put("last_name", accountDetails.getLastName());
        entity.put("account_created", accountDetails.getAccountCreated());
        entity.put("account_updated", accountDetails.getAccountUpdated());
        return entity;
    }

    public boolean accountRegister(AccountDetails accountDetails){
        boolean valid = validation(accountDetails);
        if(valid){
            if(webApplicationRepository.findByUsername(accountDetails.getUsername()) != null){
                return false;
            } else {
                accountDetails.setUuid(UUID.randomUUID().toString());
                accountDetails.setPassword(BCrypt.hashpw(accountDetails.getPassword(), BCrypt.gensalt(12)));
                accountDetails.setFirstName(accountDetails.getFirstName());
                accountDetails.setLastName(accountDetails.getLastName());
                accountDetails.setUsername(accountDetails.getUsername());
                accountDetails.setAccountCreated(new Date());
                accountDetails.setAccountUpdated(new Date());
                webApplicationRepository.save(accountDetails);
            }
        }
        return valid;
    }

    public ResponseEntity<String> accountUpdate(String email, String password, AccountDetails accountDetails){
        boolean valid = validation(accountDetails);
        if(valid){
            if(accountDetails == null){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            if(!accountDetails.getUsername().equals(email)){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            AccountDetails details = webApplicationRepository.findByUsername(accountDetails.getUsername());
            if(details != null && BCrypt.checkpw(password, details.getPassword())){
                details.setPassword(BCrypt.hashpw(accountDetails.getPassword(), BCrypt.gensalt(12)));
                details.setFirstName(accountDetails.getFirstName());
                details.setLastName(accountDetails.getLastName());
                details.setAccountUpdated(new Date());
                webApplicationRepository.save(details);
            }else{
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public Boolean validation(AccountDetails accountDetails){
        if(StringUtils.isEmpty(accountDetails.getUsername())){
            return false;
        }
        if(StringUtils.isEmpty(accountDetails.getPassword())){
            return false;
        }
        if(StringUtils.isEmpty(accountDetails.getFirstName())){
            return false;
        }
        if(StringUtils.isEmpty(accountDetails.getLastName())){
            return false;
        }
        return true;
    }

    public String oauthEncode(String oauth){
        assert oauth.substring(0, 6).equals("Basic");
        String basicAuthStr = new String(Base64.getDecoder().decode(oauth.substring(6).getBytes()));
        return basicAuthStr;
    }

    public String authAndGetUserId(String oauth){
        String authorization = oauthEncode(oauth);
        String[] headerAuth = authorization.split(":");
        String username = headerAuth[0];
        String password = headerAuth[1];
        try {
            AccountDetails accountDetails = getAccountDetails(username, password);
            String accountId = accountDetails.getUuid();
            return accountId;
        }catch (NullPointerException e){
            throw new UnauthorizedException();
        }
    }
}
