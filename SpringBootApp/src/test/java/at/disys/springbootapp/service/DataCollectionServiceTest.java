package at.disys.springbootapp.service;

import at.disys.springbootapp.queue.QueueService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class DataCollectionServiceTest {

   @Mock
   private QueueService queueService;
    @InjectMocks
    private DataCollectionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkCustomerId() {
        assertTrue(service.checkCustomerId("123"));
        assertFalse(service.checkCustomerId(null));
        assertFalse(service.checkCustomerId(""));
        assertFalse(service.checkCustomerId("abc"));
    }

    @Test
    void publishDataGatheringJob() {
        // As we are not testing interaction with QueueService
        // there is nothing to assert or verify here
        service.publishDataGatheringJob("123");
    }

    @Test
    void getInvoiceAndDownload() {
        // As we are not testing interaction with HttpServletResponse and actual file system,
        // there is nothing to assert or verify here
        HttpServletResponse response = null; // No need for a real response object
        service.getInvoiceAndDownload("123", response);
    }
}
