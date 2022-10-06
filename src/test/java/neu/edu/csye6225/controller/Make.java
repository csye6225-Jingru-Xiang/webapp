package neu.edu.csye6225.controller;

import neu.edu.csye6225.model.AccountDetails;
import org.json.JSONException;
import org.json.JSONObject;

public class Make {
    public static AccountDetails aAccountDetails() {
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setUsername("xiang.jing@northeastern.edu");
        accountDetails.setPassword("xjr0928");
        return accountDetails;
    }

    public static JSONObject aSuccessResponseEntity(AccountDetails accountDetails) {
        try {
            JSONObject entity = new JSONObject();
            entity.put("id", accountDetails.getUuid());
            entity.put("username", accountDetails.getUsername());
            entity.put("first_name", accountDetails.getFirstName());
            entity.put("last_name", accountDetails.getLastName());
            entity.put("account_created", accountDetails.getAccountCreated());
            entity.put("account_updated", accountDetails.getAccountUpdated());
            return entity;
        } catch (JSONException e) {
            return null;
        }
    }
}
