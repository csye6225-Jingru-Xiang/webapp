package neu.edu.csye6225.service;

import neu.edu.csye6225.repository.WebApplicationRepository;
import neu.edu.csye6225.model.AccountDetails;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

@Service
public class WebApplicationService {

    private WebApplicationRepository webApplicationRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public WebApplicationService(WebApplicationRepository webApplicationRepository){
        this.webApplicationRepository = webApplicationRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public AccountDetails getAccountDetails(String email, String password) {
        if (StringUtils.isEmpty(email) && StringUtils.isEmpty(password)) {
            AccountDetails details = webApplicationRepository.findAccountDetailsByEmail(email);
            if (details != null && passwordEncoder.matches(password, details.getPassword())) {
                return details;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public JSONObject getJSON(AccountDetails accountDetails) {
        JSONObject entity = new JSONObject();
        entity.put("id", accountDetails.getUuid());
        entity.put("username", accountDetails.getEmail());
        entity.put("first_name", accountDetails.getFirstName());
        entity.put("last_name", accountDetails.getLastName());
        entity.put("account_created", accountDetails.getAccount_created());
        entity.put("account_updated", accountDetails.getAccount_updated());
        return entity;
    }

    public boolean accountRegister(AccountDetails accountDetails){
        boolean valid = validation(accountDetails);
        if(valid){
            if(webApplicationRepository.findAccountDetailsByEmail(accountDetails.getEmail()) != null){
                return false;
            } else {
                accountDetails.setUuid(UUID.randomUUID().toString());
                accountDetails.setPassword(passwordEncoder.encode(accountDetails.getPassword()));
                accountDetails.setFirstName(accountDetails.getFirstName());
                accountDetails.setLastName(accountDetails.getLastName());
                accountDetails.setAccount_created(new Date());
                accountDetails.setAccount_updated(new Date());
                webApplicationRepository.save(accountDetails);
            }
        }
        return valid;
    }

    public String accountUpdate(String email, String password, AccountDetails accountDetails){
        boolean valid = validation(accountDetails);
        if(valid){
            if(!accountDetails.getEmail().equals(email)){
                return "Bad Request";
            }
            if(accountDetails == null){
                return "No Content";
            }
            AccountDetails details = webApplicationRepository.findAccountDetailsByEmail(accountDetails.getEmail());
            if(details != null && passwordEncoder.matches(password, details.getPassword())){
                details.setPassword(passwordEncoder.encode(accountDetails.getPassword()));
                details.setFirstName(accountDetails.getFirstName());
                details.setLastName(accountDetails.getLastName());
                details.setAccount_updated(new Date());
                webApplicationRepository.save(details);
            }else{
                return "Unauthorized";
            }
        }
        return "Success";
    }

    public Boolean validation(AccountDetails accountDetails){
        if(!StringUtils.isEmpty(accountDetails.getEmail())){
            return false;
        }
        if(!StringUtils.isEmpty(accountDetails.getPassword())){
            return false;
        }
        if(!StringUtils.isEmpty(accountDetails.getFirstName())){
            return false;
        }
        if(!StringUtils.isEmpty(accountDetails.getLastName())){
            return false;
        }
        return true;
    }
}
