package neu.edu.csye6225.service;

import neu.edu.csye6225.model.DocumentDetails;
import neu.edu.csye6225.repository.DocumentRepository;
import neu.edu.csye6225.repository.WebApplicationRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class DocumentsService {

    @Autowired
    private DocumentRepository documentRepository;

    public List<DocumentDetails> getDocumentDetailsListByUserId(String userId){
        return documentRepository.findAllByUserId(userId);
    }

    public DocumentDetails getDocumentDetailsByUserIdAndDocId(String userId, String docId){
        return documentRepository.findByUserIdAndDocId(userId, docId);
    }

    public void saveDocuments(DocumentDetails documentDetails){
        documentRepository.save(documentDetails);
    }

    public void deleteDocuments(String key){
        documentRepository.deleteById(key);
    }

    public JSONObject getJSON(DocumentDetails details){
        JSONObject entity = new JSONObject();
        entity.put("doc_id", details.getDocId());
        entity.put("user_id", details.getUserId());
        entity.put("name", details.getName());
        entity.put("date_created", details.getDateCreated());
        entity.put("s3_bucket_path", details.getS3BucketPath());
        return entity;
    }
}
