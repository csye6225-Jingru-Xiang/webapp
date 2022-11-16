package neu.edu.csye6225.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Entity
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
@ToString
public class AccountDetails {

    @Id
    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50)")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String uuid;

    @Email(message = "Email should be valid!")
    @NotNull(message = "Can't be empty!")
    private String username;

    @NotNull(message = "Can't be empty!")
    private String firstName;

    @NotNull(message = "Can't be empty!")
    private String lastName;

    @NotNull(message = "Can't be empty!")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private boolean authenticated;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date accountCreated;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date accountUpdated;
}
