package neu.edu.csye6225.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.timgroup.statsd.StatsDClient;

@Controller
@Slf4j
public class HeathEndpointController {

    @Autowired
    private StatsDClient statsDClient;

    @GetMapping(path = "/healthz")
    public ResponseEntity<String> getHealthEndpoint() {
        long startTime = System.currentTimeMillis();
        statsDClient.incrementCounter("endpoint.health.http.get");
        log.info("Hit endpoint.health.http.get successfully");
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        statsDClient.recordExecutionTime("endpoint.health.http.get.timer", System.currentTimeMillis() - startTime);
        return responseEntity;
    }
}
