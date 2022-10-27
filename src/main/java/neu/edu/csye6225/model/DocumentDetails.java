package neu.edu.csye6225.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
@ToString
public class DocumentDetails {
    @Id
    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50)")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String docId;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Date dateCreated;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String s3BucketPath;
}
