package at.disys;

import at.disys.model.Invoice;
import at.disys.model.Station;
import at.disys.queue.QueueName;
import at.disys.queue.QueueService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataCollectionReceiverTest {
    private QueueService receiverPDFQueue;
    Invoice invoice;
    DataCollectionReceiver dataCollectionReceiver;

    @BeforeEach
    void setUp() {
        // Arrange
        dataCollectionReceiver = new DataCollectionReceiver();
        receiverPDFQueue = new QueueService(QueueName.RECEIVER_PDF_QUEUE.getName());
    }

    @Test
    void testProcessJob() throws IOException, TimeoutException {
        // Act
        dataCollectionReceiver.processJob();
        // Assert all these variables should be reset after job execution
        assertFalse(dataCollectionReceiver.fromDispatcherReceived);
        assertFalse(dataCollectionReceiver.fromCollectorReceived);

    }

    @Test
    void testProcessDispatcher() {
        // Act
        dataCollectionReceiver.processDispatcher("355", true);
        // Assert
        assertTrue(dataCollectionReceiver.fromDispatcherReceived);
        assertEquals(355L, dataCollectionReceiver.invoice.getCustomerId());
        assertNotNull(dataCollectionReceiver.invoice.getInvoiceNumber());
        assertEquals(LocalDate.now(), dataCollectionReceiver.invoice.getInvoiceDate());

    }


    @Test
    void testProcessCollector() {
        // Act
        dataCollectionReceiver.processCollector("localhost:30011,355", true);
        // Assert - if received collector message, then should be true and the station should be set
        assertEquals(1, dataCollectionReceiver.invoice.getLocations().size());
        assertEquals("localhost:30011", dataCollectionReceiver.invoice.getLocations().stream().findAny().get().getURLString());
        assertEquals(355L, dataCollectionReceiver.invoice.getLocations().stream().findAny().get().getTotalKwh());
    }

    @Test
    void testProcessCollectorEnd() {
        // Act
        dataCollectionReceiver.processCollector("END", true);
        // Assert - if received END message, then should be true and the station should be null
       assertTrue(dataCollectionReceiver.fromCollectorReceived);
       assertEquals(new HashSet<>(), dataCollectionReceiver.invoice.getLocations());
    }
}

