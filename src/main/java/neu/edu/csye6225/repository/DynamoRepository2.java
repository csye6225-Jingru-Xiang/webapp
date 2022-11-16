package neu.edu.csye6225.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import neu.edu.csye6225.model.AccountTokenItem;
import neu.edu.csye6225.service.ForbiddenException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
public class DynamoRepository2 {

    @Resource
    private DynamoDBMapper dynamoDBMapper;

    public void save(AccountTokenItem accountTokenItem) {
        if (!validation(accountTokenItem)) {
            throw new ForbiddenException();
        }
        dynamoDBMapper.save(accountTokenItem);
    }

    public AccountTokenItem query(String email) {
        AccountTokenItem accountTokenItem = new AccountTokenItem();
        accountTokenItem.setEmail(email);


        List<AccountTokenItem> query = dynamoDBMapper.query(AccountTokenItem.class,
                new DynamoDBQueryExpression<AccountTokenItem>().withHashKeyValues(accountTokenItem));
        if (CollectionUtils.isEmpty(query)) {
            return null;
        }
        return query.get(0);
    }

    public Boolean validation(AccountTokenItem accountTokenItem) {
        if (StringUtils.isEmpty(accountTokenItem.getEmail())) {
            return false;
        }
        if (StringUtils.isEmpty(accountTokenItem.getToken())) {
            return false;
        }
        if (accountTokenItem.getTtl() == null) {
            return false;
        }
        return true;
    }
}
