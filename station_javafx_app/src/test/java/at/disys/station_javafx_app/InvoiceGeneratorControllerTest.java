package at.disys.station_javafx_app;



//import at.disys.db.DatabaseConnector;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InvoiceGeneratorControllerTest {
    private static final String TEST_CUSTOMER_ID = "123";

    @Mock
    private TextField customerIdField;

    @Mock
    private TableView<InvoiceGeneratorController.Invoice> invoiceTable;

    @InjectMocks
    private InvoiceGeneratorController invoiceGeneratorController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        invoiceGeneratorController = new InvoiceGeneratorController();
    }


    @Test
    void testInitialize() {

        //invoiceTable sollte schon von setUP wegem BeforeEach gemacht sein?:
        TableView<InvoiceGeneratorController.Invoice> invoiceTable = mock(TableView.class);
        doNothing().when(invoiceTable).setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        invoiceGeneratorController.invoiceTable = invoiceTable;
        invoiceGeneratorController.initialize();
        verify(invoiceTable, times(1)).setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }



    @Test
    void testInvoiceClass() {

        String customerId = "123";
        Button viewInvoiceButton = new Button("View");
        InvoiceGeneratorController.Invoice invoice = new InvoiceGeneratorController.Invoice(customerId, viewInvoiceButton);

        assertEquals(customerId, invoice.getCustomerId());
        assertEquals(viewInvoiceButton, invoice.getViewInvoiceButton());
    }
}

