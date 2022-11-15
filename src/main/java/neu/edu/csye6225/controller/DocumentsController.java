package neu.edu.csye6225.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.timgroup.statsd.StatsDClient;
import lombok.extern.slf4j.Slf4j;
import neu.edu.csye6225.model.DocumentDetails;
import neu.edu.csye6225.service.DocumentsService;
import neu.edu.csye6225.service.ForbiddenException;
import neu.edu.csye6225.service.WebApplicationService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping(path = "/v1/documents")
public class DocumentsController {
    @Autowired
    private StatsDClient statsDClient;

    private WebApplicationService webApplicationService;
    private DocumentsService documentsService;

    Regions clientRegion = Regions.US_EAST_1;

    String bucketName = System.getenv("S3_BUCKET_NAME");

    public DocumentsController(WebApplicationService webApplicationService, DocumentsService documentsService) {
        this.webApplicationService = webApplicationService;
        this.documentsService = documentsService;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<String>> getUploadedDocs(@RequestHeader(value = "Authorization") String oauth) {
        long startTime = System.currentTimeMillis();
        statsDClient.incrementCounter("endpoint.doc.http.get");
        log.info("Hit endpoint.doc.http.get successfully");
        try {
            String accountId = webApplicationService.authAndGetUserId(oauth);
            List<DocumentDetails> doc = documentsService.getDocumentDetailsListByUserId(accountId);
            List<String> entities = new ArrayList<>();
            for (DocumentDetails details : doc) {
                JSONObject entity = documentsService.getJSON(details);
                entities.add(entity.toString());
            }
            statsDClient.recordExecutionTime("endpoint.doc.http.get.timer", System.currentTimeMillis() - startTime);
            return new ResponseEntity<>(entities, HttpStatus.OK);
        } catch (Exception e) {
            throw new ForbiddenException();
        }
    }

    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<String> uploadDoc(@RequestHeader(value = "Authorization") String oauth, @RequestParam MultipartFile file) throws IOException {
        long startTime = System.currentTimeMillis();
        statsDClient.incrementCounter("endpoint.doc.http.post");
        log.info("Hit endpoint.doc.http.post successfully");
        try {

            String localFileName = "/home/ubuntu/" + file.getOriginalFilename();
            File tempFile = this.getFile(file, localFileName);
            String fileObjKeyName = this.uploadS3(tempFile, file.getContentType());

            String accountId = webApplicationService.authAndGetUserId(oauth);
            DocumentDetails doc;
            if (documentsService.getDocumentDetailsByUserIdAndDocId(accountId, fileObjKeyName) != null) {
                doc = documentsService.getDocumentDetailsByUserIdAndDocId(accountId, fileObjKeyName);
            } else {
                doc = new DocumentDetails();
            }
            doc.setUserId(accountId);
            doc.setS3BucketPath(bucketName);
            doc.setDateCreated(new Date());
            doc.setName(localFileName);
            doc.setDocId(fileObjKeyName);
            documentsService.saveDocuments(doc);
            JSONObject entity = documentsService.getJSON(doc);
            statsDClient.recordExecutionTime("endpoint.doc.http.post.timer", System.currentTimeMillis() - startTime);
            return new ResponseEntity<>(entity.toString(), HttpStatus.CREATED);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Amazon Service Exception", HttpStatus.BAD_REQUEST);
        } catch (SdkClientException e) {
            e.printStackTrace();
            return new ResponseEntity<>("SDK Client Exception", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private String uploadS3(File file, String contentType) {

        String fileObjKeyName = UUID.randomUUID().toString();
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(clientRegion)
                .build();

        PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, file);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.addUserMetadata("fileName", fileObjKeyName);
        request.setMetadata(metadata);

        s3Client.putObject(request);

        return fileObjKeyName;
    }

    private File getFile(MultipartFile multipartFile, String fileName) throws IOException {
        File file = new File(fileName);
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] ss = multipartFile.getBytes();
            for (int i = 0; i < ss.length; i++) {
                out.write(ss[i]);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    @GetMapping(path = "/{docId}")
    public ResponseEntity<String> getDocDetails(@RequestHeader(value = "Authorization") String oauth, @PathVariable String docId) {
        long startTime = System.currentTimeMillis();
        statsDClient.incrementCounter("endpoint.doc.http.get");
        log.info("Hit endpoint.doc.http.get with docID successfully");
        try {
            String accountId = webApplicationService.authAndGetUserId(oauth);
            DocumentDetails documentDetails = documentsService.getDocumentDetailsByUserIdAndDocId(accountId, docId);
            JSONObject entity = documentsService.getJSON(documentDetails);
            statsDClient.recordExecutionTime("endpoint.doc.http.get.timer", System.currentTimeMillis() - startTime);
            return new ResponseEntity<>(entity.toString(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ForbiddenException();
        }
    }

    @DeleteMapping(path = "/{docId}")
    public ResponseEntity<String> deleteDoc(@RequestHeader(value = "Authorization") String oauth, @PathVariable String docId) {
        long startTime = System.currentTimeMillis();
        statsDClient.incrementCounter("endpoint.doc.http.delete");
        log.info("Hit endpoint.doc.http.get with docID successfully");
        try {
            String accountId = webApplicationService.authAndGetUserId(oauth);
            if (documentsService.getDocumentDetailsByUserIdAndDocId(accountId, docId) != null) {
                String keyName = docId;
                AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                        .withRegion(clientRegion)
                        .build();
                s3Client.deleteObject(new DeleteObjectRequest(bucketName, keyName));
                documentsService.deleteDocuments(keyName);
                statsDClient.recordExecutionTime("endpoint.doc.http.delete.timer", System.currentTimeMillis() - startTime);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                statsDClient.recordExecutionTime("endpoint.doc.http.delete.timer", System.currentTimeMillis() - startTime);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Amazon Service Exception", HttpStatus.BAD_REQUEST);
        } catch (SdkClientException e) {
            e.printStackTrace();
            return new ResponseEntity<>("SDK Client Exception", HttpStatus.BAD_REQUEST);
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
