package at.disys;

import at.disys.db.DatabaseConnector;
import at.disys.model.Customer;
import at.disys.model.Invoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PDFGeneratorTest {
    @InjectMocks
    private PDFGenerator pdfGenerator;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;
    @Mock
    private DatabaseConnector databaseConnector;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testParseInvoiceData() throws SQLException {
        // Arrange
        when(databaseConnector.executeSQLQuery(PDFGenerator.QUERY)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(databaseConnector.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("first_name")).thenReturn("Test");
        when(resultSet.getString("last_name")).thenReturn("Test");
        String invoiceData = "localhost:30013;164|localhost:30011;69|localhost:30012;179|,728333919433148334,1,2023-06-07,412";
        // Act
        Invoice invoice = pdfGenerator.parseInvoiceData(invoiceData);
        // Assert
        assertEquals(728333919433148334L, invoice.getInvoiceNumber());
        assertEquals(1L, invoice.getCustomer().getId());
        assertEquals("2023-06-07", invoice.getInvoiceDate().toString());
        assertEquals(412L, invoice.getTotalKwh());
    }

    @Test
    public void testGetCustomerById() throws SQLException {
        // Arrange
        when(databaseConnector.executeSQLQuery(PDFGenerator.QUERY)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(databaseConnector.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("first_name")).thenReturn("Test");
        when(resultSet.getString("last_name")).thenReturn("Test");

        // Act
        Customer customer = pdfGenerator.getCustomerById(1L);
        // Assert
        verify(databaseConnector, times(1)).connect();
        verify(databaseConnector, times(1)).disconnect();
        assertEquals(1L, customer.getId());
        assertEquals("Test", customer.getFirstName());
        assertEquals("Test", customer.getLastName());
    }
}
