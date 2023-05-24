package at.disys;

import at.disys.db.DatabaseConnector;
import at.disys.model.Customer;
import at.disys.model.Invoice;
import at.disys.model.Station;
import at.disys.queue.QueueService;
import at.disys.service.PdfHelper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.rabbitmq.client.DeliverCallback;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static at.disys.queue.QueueName.*;

/**
 * This class is responsible for generating the PDF report.<br>
 *
 * <i> <br>
 *     Generates the invoice from data<br>
 *     Saves PDF to the file system
 * </i>
 */
public class PDFGenerator {
    public static final String QUERY = "SELECT * FROM customer WHERE id = ?";
    private final QueueService receiverPdfQueue = new QueueService(RECEIVER_PDF_QUEUE.getName());
    private PdfHelper pdfHelper = new PdfHelper();
    private final double pricePerKwh = 0.25;

    /**
     * PDF generator main method
     */
    public static void main(String[] args){
        PDFGenerator pdfGenerator = new PDFGenerator();
        pdfGenerator.handleAllGatheredData();
    }


    public void handleAllGatheredData() {

        try {
            //receive from
            receiverPdfQueue.connect();
            while (true) {
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String receivedMessage = new String(delivery.getBody(), "UTF-8");
                    System.out.println("Received message:" + receivedMessage);

                    pdfHelper.generatePDF(Optional.of(parseInvoiceData(receivedMessage)), pricePerKwh);

                };
                receiverPdfQueue.getChannel().basicConsume(receiverPdfQueue.getQueueName(), true, deliverCallback, consumerTag -> {
                });
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses the complete String to an Invoice object
     * @param receivedMessage String to parse
     * @return Invoice object
     */
    private Invoice parseInvoiceData(String receivedMessage) {
        Invoice invoice = new Invoice();

        String[] invoiceData = receivedMessage.split(",");
        String[] stationData = invoiceData[0].split("\\|");

        for (String single : stationData) {
            String[] stationDataSingle = single.split(";");
            Station station = new Station();
            station.setURLString(stationDataSingle[0]);
            station.setTotalKwh(Long.valueOf(stationDataSingle[1]));
            invoice.getLocations().add(station);
        }

        invoice.setInvoiceNumber(Long.valueOf(invoiceData[1]));
        invoice.setCustomer(getCustomerById(Long.valueOf(invoiceData[2])));
        invoice.setInvoiceDate(LocalDate.parse(invoiceData[3]));
        invoice.setTotalKwh(Long.valueOf(invoiceData[4]));

        return invoice;
    }


    /**
     * Get Customer by id from database
     * @param id Customer id
     * @return Customer object
     */
    public Customer getCustomerById(Long id) {
        DatabaseConnector customerDb = new DatabaseConnector();
        customerDb.connect();

        try (PreparedStatement statement = customerDb.getConnection().prepareStatement(QUERY)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Customer customer = new Customer();
                customer.setId(resultSet.getLong("id"));
                customer.setFirstName(resultSet.getString("first_name"));
                customer.setLastName(resultSet.getString("last_name"));
                return customer;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            customerDb.disconnect();
        }
        return new Customer();
    }

}
