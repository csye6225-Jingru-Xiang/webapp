package neu.edu.csye6225.config;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SNSConfig {
    @Value("${aws.s3.region}")
    private String region;

    @Bean
    public AmazonSNS getAmazonSNS() {
        return AmazonSNSClientBuilder.standard()
                .withRegion(region)
                .build();
    }
}
