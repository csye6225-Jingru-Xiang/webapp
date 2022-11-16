package neu.edu.csye6225.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishResult;
import lombok.extern.slf4j.Slf4j;
import neu.edu.csye6225.dto.Message;
import neu.edu.csye6225.model.AccountDetails;
import neu.edu.csye6225.model.AccountTokenItem;
import neu.edu.csye6225.repository.DynamoRepository2;
import org.apache.tomcat.util.security.MD5Encoder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

@Slf4j
@Service
public class EmailAuthService {
    @Resource
    private DynamoRepository2 dynamoRepository2;
    @Resource
    private AmazonSNS amazonSNS;

    @Value("${sns.topic.arn}")
    private String snsTopicARN;

    private static final String LINK = "https://prod.spicyrice.me/v2/verifyUserEmail?email=user.getUsername()&token=token";

    public void trigger(AccountDetails accountDetails) {
        String token = getToken(accountDetails);
        dynamoRepository2.save(AccountTokenItem.builder()
                .email(accountDetails.getUsername())
                .token(token)
                .ttl(getAfterFiveMinute())
                .build());
        this.send(accountDetails, token);
    }

    public boolean validate(String email, String token) {
        AccountTokenItem query = dynamoRepository2.query(email);
        return query != null && query.getToken().equals(token);
    }


    private void send(AccountDetails accountDetails, String token) {
        Message build = Message.builder()
                .first_name(accountDetails.getFirstName())
                .username(accountDetails.getUsername())
                .link(LINK)
                .one_time_token(token)
                .message_type("String")
                .build();
        PublishResult publish = amazonSNS.publish(snsTopicARN, JSONObject.valueToString(build));
        log.info("send succeed msgId:{}", publish.getMessageId());
    }

    private String getToken(AccountDetails accountDetails) {
        String token = accountDetails.getUsername() +
                accountDetails.getFirstName() +
                accountDetails.getLastName();
        return MD5Encoder.encode(token.getBytes(StandardCharsets.UTF_8));
    }

    private long getAfterFiveMinute() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, 5);
        return instance.getTimeInMillis() / 1000;
    }
}
