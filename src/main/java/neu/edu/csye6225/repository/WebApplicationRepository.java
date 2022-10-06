package neu.edu.csye6225.repository;

import neu.edu.csye6225.model.AccountDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface WebApplicationRepository extends CrudRepository<AccountDetails, String> {

    @Query(value = "select account from AccountDetails account where account.email=?1")
    AccountDetails findAccountDetailsByEmail(String email);
}
