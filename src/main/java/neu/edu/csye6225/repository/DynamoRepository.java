package neu.edu.csye6225.repository;

import lombok.extern.slf4j.Slf4j;
import neu.edu.csye6225.model.AccountTokenItem;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DynamoRepository {

    @Value("${aws.dynamodb.tableName}")
    private String tableName;

    @Resource
    private DynamoDbClient dynamoDbClient;

    public AccountTokenItem query(String email) {

        Map<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("Email", AttributeValue.builder()
                .s(email).build());

        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(this.tableName)
                .build();

        try {
            log.info("dynamoDbClient.getItem request,:{}",JSONObject.valueToString(request));
            GetItemResponse item = dynamoDbClient.getItem(request);
            log.info("dynamoDbClient.getItem response,:{}",JSONObject.valueToString(item));
            Map<String, AttributeValue> returnedItem = item
                    .item();
            if (returnedItem == null) {
                return null;
            }
            log.info("returnedItem：{}", JSONObject.valueToString(returnedItem));
            return AccountTokenItem.builder()
                    .email(returnedItem.get("Email").s())
                    .token(returnedItem.get("Token").s())
                    .build();
        } catch (DynamoDbException e) {
            log.error("query error", e);
            throw new RuntimeException("query error", e);
        }
    }


    public void save(AccountTokenItem accountTokenItem) {

        Map<String, AttributeValue> itemValues = new HashMap<>();

        itemValues.put("Email", AttributeValue.builder().s(accountTokenItem.getEmail()).build());
        itemValues.put("Token", AttributeValue.builder().s(accountTokenItem.getToken()).build());
        itemValues.put("Ttl", AttributeValue.builder().s(String.valueOf(accountTokenItem.getTtl())).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

        try {
            PutItemResponse response = dynamoDbClient.putItem(request);
            log.info(tableName + " was successfully updated", response);
        } catch (Exception e) {
            log.error("save error", e);
            throw new RuntimeException("save error", e);
        }
    }
}
