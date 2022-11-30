package neu.edu.csye6225.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message implements Serializable {
    private String first_name;
    private String username;
    private String one_time_token;
    private String link;
    private String message_type;
}