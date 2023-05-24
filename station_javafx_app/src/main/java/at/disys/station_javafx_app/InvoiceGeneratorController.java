package at.disys.station_javafx_app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class InvoiceGeneratorController {
    private static final String BASE_URL = "http://localhost:8080/api/invoices/";
    private final ObservableList<Invoice> invoices = FXCollections.observableArrayList();

    @FXML
    private TextField customerIdField;

    @FXML
    private TableView<Invoice> invoiceTable;

    public void initialize() {
        invoiceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Invoice, String> customerIdColumn = new TableColumn<>("Customer ID");
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<Invoice, Button> viewInvoiceColumn = new TableColumn<>("View Invoice");
        viewInvoiceColumn.setCellValueFactory(new PropertyValueFactory<>("viewInvoiceButton"));

        invoiceTable.setItems(invoices);
    }

    @FXML
    private void generateInvoice() {
        String customerId = customerIdField.getText();
        if (!customerId.isEmpty()) {
            try {
                URL url = new URL(BASE_URL + customerId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.getResponseCode();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED && getResponseGETRequest(customerId).responseCode() == HttpURLConnection.HTTP_OK) {
                    System.out.println("Invoice generated for customer ID: " + customerId);
                    invoiceTable.getItems().add(new Invoice(customerId, createViewInvoiceButton(customerId)));
                } else {
                    System.out.println("Invoice generation failed for customer ID: " + customerId);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Button createViewInvoiceButton(String customerId) {
        Button viewInvoiceButton = new Button("View");
        viewInvoiceButton.setOnAction(e -> viewInvoice(customerId));
        return viewInvoiceButton;
    }

    private static void viewInvoice(String customerId) {
        try {
            Result result = getResponseGETRequest(customerId);
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
                if (pdfFile.exists()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    System.out.println("Failed to open PDF file");
                }
            } else if (result.responseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                System.out.println("Invoice not available for customer ID: " + customerId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Result getResponseGETRequest(String customerId) throws IOException {
        URL url = new URL(BASE_URL + customerId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        return new Result(connection, responseCode);
    }

    private record Result(HttpURLConnection connection, int responseCode) {
    }


    public static void main(String[] args) {
        viewInvoice("123456");
    }

    public static class Invoice {
        private final String customerId;
        private final Button viewInvoiceButton;
        public Invoice(String customerId, Button viewInvoiceButton) {
            this.customerId = customerId;
            this.viewInvoiceButton = viewInvoiceButton;
        }
        public String getCustomerId() {
            return customerId;
        }
        public Button getViewInvoiceButton() {
            return viewInvoiceButton;
        }
    }
}
