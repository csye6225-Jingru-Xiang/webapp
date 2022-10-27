package neu.edu.csye6225.repository;

import neu.edu.csye6225.model.DocumentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentDetails, String> {
    List<DocumentDetails> findAllByUserId(String userId);

    DocumentDetails findByUserIdAndDocId(String userId, String docId);
}
