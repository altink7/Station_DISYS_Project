package at.disys.service;

import at.disys.model.Customer;
import at.disys.model.Invoice;
import at.disys.model.Station;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PdfHelperTest {

    private PdfHelper pdfHelper;
    private Invoice invoice;

    @BeforeEach
    public void setup() {
        pdfHelper = new PdfHelper();

        invoice = new Invoice();
        Customer customer = new Customer();
        customer.setId(1L);
        invoice.setCustomer(customer);
        invoice.setInvoiceNumber(123L);
        invoice.setTotalKwh(200L);
        Station station = new Station();
        station.setURLString("https://test.com");
        station.setTotalKwh(100L);
        invoice.setLocations(Collections.singleton(station));
    }

    @Test
    void testCreateTableReturnTotal() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        PdfPTable chargesTable = new PdfPTable(4);
        double pricePerKwh = 0.25;

        // reflect private method
        Method method = PdfHelper.class.getDeclaredMethod("CreateTableReturnTotal", Invoice.class, double.class, FontFactory.getFont(FontFactory.HELVETICA).getClass(), PdfPTable.class);
        method.setAccessible(true);

        // invoke method and get result
        double totalAmount = (double) method.invoke(pdfHelper, invoice, pricePerKwh, FontFactory.getFont(FontFactory.HELVETICA), chargesTable);

        assertEquals(invoice.getTotalKwh() * pricePerKwh, totalAmount*2);
    }
}
