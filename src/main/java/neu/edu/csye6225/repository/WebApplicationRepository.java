package neu.edu.csye6225.repository;

import neu.edu.csye6225.model.AccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WebApplicationRepository extends JpaRepository<AccountDetails, String> {
    AccountDetails findByUsername(String username);

}
