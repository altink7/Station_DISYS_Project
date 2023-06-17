package at.disys.station_javafx_app.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * business logic for the invoice generator
 * it contains the logic for generating an invoice
 */
public class InvoiceGeneratorService {
    private static InvoiceGeneratorService instance;

    private InvoiceGeneratorService() {
    }

    /**
     * Returns the singleton instance of the InvoiceGeneratorService.
     * @return The singleton instance of the InvoiceGeneratorService.
     */
    public static InvoiceGeneratorService getInstance() {
        if (instance == null) {
            instance = new InvoiceGeneratorService();
        }
        return instance;
    }

    /** Server API */
    protected static String BASE_URL = "http://localhost:8080/api/invoices/";

    /**
     * Sends a GET request to the server to check if an invoice is available for the given customer ID.
     * @param customerId The customer ID for which the invoice should be checked.
     * @return The response of the GET request.
     * @throws IOException If an I/O error occurs.
     */
    public Result getResponseGETRequest(String customerId) throws IOException {
        URL url = new URL(BASE_URL + customerId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        return new InvoiceGeneratorService.Result(connection, responseCode);
    }

    public record Result(HttpURLConnection connection, int responseCode) {
    }
}
