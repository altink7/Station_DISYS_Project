package at.disys.station_javafx_app;

import at.disys.station_javafx_app.model.Invoice;
import at.disys.station_javafx_app.service.InvoiceGeneratorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.xml.transform.Result;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static at.disys.station_javafx_app.service.InvoiceGeneratorService.getResponseGETRequest;

/**
 * Controller for the Invoice Generator JavaFX App.
 */
public class InvoiceGeneratorController {
    private static final String BASE_URL = "http://localhost:8080/api/invoices/";
    private final ObservableList<Invoice> invoices = FXCollections.observableArrayList();

    @FXML
    private TextField customerIdField;

    @FXML
    private  TableView<Invoice> invoiceTable;

    public void initialize() {
        invoiceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Invoice, String> customerIdColumn = new TableColumn<>("Customer ID");
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<Invoice, Button> viewInvoiceColumn = new TableColumn<>("View Invoice");
        viewInvoiceColumn.setCellValueFactory(new PropertyValueFactory<>("viewInvoiceButton"));

        invoiceTable.setItems(invoices);
    }

    /**
     * Generates an invoice for the customer ID entered in the text field.
     */
    @FXML
    protected void generateInvoice() {
        String customerId = customerIdField.getText();
        if (!customerId.isEmpty()) {
            try {
                URL url = new URL(BASE_URL + customerId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.getResponseCode();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
                    //wait 10 seconds for the invoice to be generated
                    Thread.sleep(5000);
                    if(getResponseGETRequest(customerId).responseCode() == HttpURLConnection.HTTP_OK) {
                        System.out.println("Invoice generated for customer ID: " + customerId);
                    }
                    invoiceTable.getItems().add(new Invoice(customerId, createViewInvoiceButton(customerId)));
                    if(viewInvoice(customerId, true, invoiceTable)){
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Information");
                        alert.setHeaderText(null);
                        alert.setContentText("Invoice generated!");
                        alert.showAndWait();
                    }
                } else {
                    System.out.println("Invoice generation failed for customer ID: " + customerId);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Creates a button that opens the invoice for the given customer ID.
     * @param customerId The customer ID for which the invoice should be opened.
     * @return The button that opens the invoice.
     */
    private Button createViewInvoiceButton(String customerId) {
        Button viewInvoiceButton = new Button("View");
        viewInvoiceButton.setOnAction(e -> viewInvoice(customerId, false, invoiceTable));
        return viewInvoiceButton;
    }

    /**
     * Opens the invoice for the given customer ID.
     *
     * @param customerId   The customer ID for which the invoice should be opened.
     * @param invoiceTable invoice Table Data
     */
    private static boolean viewInvoice(String customerId, boolean isCheck, TableView<Invoice> invoiceTable) {
        boolean success = true;
        try {
            InvoiceGeneratorService.Result result = getResponseGETRequest(customerId);
            if (result.responseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Invoice available for customer ID: " + customerId);

                String localFilePath = "files/invoice" + customerId + ".pdf";

                try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = result.connection().getInputStream().read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }

                File pdfFile = new File(localFilePath);
                if (pdfFile.exists() && !isCheck ) {
                    Desktop.getDesktop().open(pdfFile);
                }
            } else if (result.responseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                System.out.println("Invoice not available for customer ID: " + customerId);

                if(isCheck){ //check if exists
                    invoiceTable.getItems().removeIf(invoice -> invoice.getCustomerId().equals(customerId));
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Invoice not found!");
                    alert.showAndWait();
                    success = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }
}
