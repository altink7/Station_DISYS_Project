package at.disys.station_javafx_app.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

class InvoiceGeneratorServiceTest {


    @Test
    void getResponseGETRequest_withValidCustomerId() throws IOException {
        // Setup
        String customerId = "1234";
        InvoiceGeneratorService.Result expectedResponse = new InvoiceGeneratorService.Result(null, 404);

        // Execution
        InvoiceGeneratorService.Result actualResponse = InvoiceGeneratorService.getResponseGETRequest(customerId);

        // Assertions
        Assertions.assertEquals(expectedResponse.responseCode(), actualResponse.responseCode());

    }

    @Test
    void getResponseGETRequest_withInvalidPath() throws IOException {
        // Arrange
        String customerId = "1234";
        String invalidPath = "invalid";
        String expectedBaseUrl = "http://localhost:8080/api/invoices/";
        String expectedUrl = expectedBaseUrl + invalidPath + customerId;

        // Act
        URL url = new URL(expectedUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int actualResponseCode = connection.getResponseCode();
        String actualUrl = connection.getURL().toString();

        // Assert
        Assertions.assertEquals(404, actualResponseCode);
        Assertions.assertEquals(expectedUrl, actualUrl);
    }
}


