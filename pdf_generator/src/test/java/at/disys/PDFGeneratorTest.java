package at.disys;

import at.disys.db.DatabaseConnector;
import at.disys.model.Customer;
import at.disys.model.Invoice;
import at.disys.queue.QueueService;
import at.disys.service.PdfHelper;
import com.rabbitmq.client.*;
import com.rabbitmq.client.Connection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;
import java.sql.*;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PDFGeneratorTest {
    @InjectMocks
    private PDFGenerator pdfGenerator;

    @Mock
    private QueueService receiverPdfQueue;

    @Mock
    private DatabaseConnector databaseConnector;

    @Mock
    private PdfHelper pdfHelper;

    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    private Connection connection;

    @Mock
    private Channel channel;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Captor
    private ArgumentCaptor<DeliverCallback> deliverCallbackCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleAllGatheredData() throws IOException, TimeoutException {
        when(receiverPdfQueue.getChannel()).thenReturn(channel);
        when(receiverPdfQueue.getQueueName()).thenReturn("queue");

        pdfGenerator.handleAllGatheredData();

        verify(channel, times(1)).basicConsume(eq("queue"), eq(true), any(DeliverCallback.class), any(CancelCallback.class));

    }

    @Test
    public void testGetCustomerById() throws SQLException {
        long id = 1L;
        when(databaseConnector.getConnection()).thenReturn((java.sql.Connection) connection);
        when(((java.sql.Connection) connection).prepareStatement(any())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong(any())).thenReturn(id);
        when(resultSet.getString(any())).thenReturn("Test");

        Customer customer = pdfGenerator.getCustomerById(id);

        verify(databaseConnector, times(1)).connect();
        verify(databaseConnector, times(1)).disconnect();
        assertEquals(id, customer.getId());
        assertEquals("Test", customer.getFirstName());
        assertEquals("Test", customer.getLastName());
    }
}
