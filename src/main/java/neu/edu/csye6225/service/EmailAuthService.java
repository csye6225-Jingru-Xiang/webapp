package neu.edu.csye6225.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishResult;
import lombok.extern.slf4j.Slf4j;
import neu.edu.csye6225.dto.Message;
import neu.edu.csye6225.model.AccountDetails;
import neu.edu.csye6225.model.AccountTokenItem;
import neu.edu.csye6225.repository.DynamoRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class EmailAuthService {
    @Resource
    private DynamoRepository dynamoRepository;
    @Resource
    private AmazonSNS amazonSNS;

    @Value("${sns.topic.arn}")
    private String snsTopicARN;


    public void trigger(AccountDetails accountDetails) throws UnsupportedEncodingException {
        log.info("start trigger,accountDetails:{}", JSONObject.valueToString(accountDetails));
        String token = getToken(accountDetails);
        try {
            dynamoRepository.save(AccountTokenItem.builder()
                    .email(URLEncoder.encode(accountDetails.getUsername(),"utf-8"))
                    .token(token)
                    .ttl(getAfterFiveMinute())
                    .build());
            this.send(accountDetails, token);
        } catch (Exception e) {
            log.error("trigger error", e);
            throw e;
        }
    }

    public boolean validate(String email, String token) {
        try {
            AccountTokenItem query = dynamoRepository.query(email);
            return query != null && query.getToken().equals(token);
        } catch (Exception e) {
            log.error("validate error", e);
            throw e;
        }
    }


    private void send(AccountDetails accountDetails, String token) {
        try {
            String LINK = "http://prod.rubyxjr.me/v2/verifyUserEmail?email=" + accountDetails.getUsername() + "&token=" + token;
            Map<String, String> msg = new HashMap<>();
            msg.put("first_name",accountDetails.getFirstName());
            msg.put("username",accountDetails.getUsername());
            msg.put("link",LINK);
            msg.put("one_time_token",token);
            msg.put("message_type","String");
            PublishResult publish = amazonSNS.publish(snsTopicARN, JSONObject.valueToString(msg));
            log.info("send succeed msgId:{}", publish.getMessageId());
        } catch (Exception e) {
            log.error("send msg error", e);
            throw e;
        }
    }

    private String getToken(AccountDetails accountDetails) {
        String token = accountDetails.getUsername() +
                accountDetails.getFirstName() +
                accountDetails.getLastName();
        return DigestUtils.md5DigestAsHex(token.getBytes(StandardCharsets.UTF_8));
    }

    private long getAfterFiveMinute() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, 2);
        return instance.getTimeInMillis() / 1000;
    }
}
