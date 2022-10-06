package neu.edu.csye6225.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "account_details")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccountDetails {

    @Id
    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50)")
    private String uuid;

    @Email(message = "Email should be valid!")
    private String username;

    @NotNull(message = "Can't be empty!")
    private String firstName;

    @NotNull(message = "Can't be empty!")
    private String lastName;

    @NotNull(message = "Can't be empty!")
    private String password;

    private Date accountCreated;
    private Date accountUpdated;
}
