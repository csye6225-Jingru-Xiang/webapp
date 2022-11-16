package neu.edu.csye6225.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoConfig {
    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.access_key_id}")
    private String accessKeyId;

    @Value("${aws.secret_access_key}")
    private String secretAccessKey;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper() {

        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKeyId, secretAccessKey));

        return new DynamoDBMapper(AmazonDynamoDBClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .build());
    }
}