package neu.edu.csye6225.controller;

import lombok.extern.slf4j.Slf4j;
import neu.edu.csye6225.WebApplication;
import neu.edu.csye6225.model.AccountDetails;
import neu.edu.csye6225.repository.WebApplicationRepository;
import neu.edu.csye6225.service.WebApplicationService;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebApplicationControllerTest {

    @InjectMocks
    WebApplicationController webApplicationController;

    @Mock
    WebApplicationService webApplicationService;

    @Mock
    WebApplicationRepository webApplicationRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setUsername("xiang.jing@northeastern.edu");
        accountDetails.setPassword("xjr0928");
        when(webApplicationRepository.findByUsername(accountDetails.getUsername())).thenReturn(accountDetails);
    }

    @Test
    public void BlankPostRequest() {
        AccountDetails accountDetails = new AccountDetails();
        ResponseEntity<String> responseEntity = webApplicationController.accountRegister(accountDetails);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void BlankGetRequest() {
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setUsername("xiang.jing@northeastern.edu");
        accountDetails.setPassword("xjr0928");
        webApplicationController.getAccountDetails("", "xiang.jing@northeastern.edu");
    }

    @Test
    public void findUsername() throws Exception {
        AccountDetails accountDetails = webApplicationRepository.findByUsername("xiang.jing@northeastern.edu");
        assertEquals(accountDetails.getUsername(),"xiang.jing@northeastern.edu");
    }

}
