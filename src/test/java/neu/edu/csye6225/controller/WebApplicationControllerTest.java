package neu.edu.csye6225.controller;

import neu.edu.csye6225.model.AccountDetails;
import neu.edu.csye6225.repository.WebApplicationRepository;
import neu.edu.csye6225.service.WebApplicationService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebApplicationControllerTest {

    @InjectMocks
    WebApplicationController webApplicationController;

    @Mock
    WebApplicationService webApplicationService;

    @Mock
    WebApplicationRepository webApplicationRepository;

    AccountDetails accountDetails = Make.aAccountDetails();

    JSONObject responseEntity = Make.aSuccessResponseEntity(accountDetails);

    @Before
    public void setup() {
        when(webApplicationRepository.findByUsername(accountDetails.getUsername())).thenReturn(accountDetails);
    }

    @Test
    public void BlankPostRequest() {
        when(webApplicationService.accountRegister(accountDetails)).thenReturn(true);
        when(webApplicationService.getAccountDetails(accountDetails.getUsername(), accountDetails.getPassword())).thenReturn(accountDetails);
        when(webApplicationService.getJSON(accountDetails)).thenReturn(responseEntity);

        ResponseEntity<String> responseEntity = webApplicationController.accountRegister(accountDetails);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);
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
        assertEquals(accountDetails.getUsername(), "xiang.jing@northeastern.edu");
    }

}
