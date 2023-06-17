package at.disys.station_javafx_app.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InvoiceTest {

    Invoice invoice = new Invoice("1", null);

    @Test
    void getCustomerId() {
        Assertions.assertEquals("1", invoice.getCustomerId());
    }

    @Test
    void getViewInvoiceButton() {
        Assertions.assertNull(invoice.getViewInvoiceButton());
    }
}