package at.disys.springbootapp.service;

import at.disys.springbootapp.queue.QueueService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class DataCollectionServiceTest {
    @Mock
    private QueueService mockMessagePublisher;
    @Mock
    private HttpServletRequest mockHttpServletRequest;
    @Mock
    private HttpServletResponse mockHttpServletResponse;
    @Mock
    private ServletOutputStream mockServletOutputStream;
    private DataCollectionService dataCollectionService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        dataCollectionService = new DataCollectionService();
        dataCollectionService.setMessagePublisher(mockMessagePublisher);
    }
    @Test
    void checkCustomerIdValidIdReturnsTrue() {
        // Arrange
        String validCustomerId = "123";
        // Act
        boolean result = dataCollectionService.checkCustomerId(validCustomerId);
        // Assert
        assertTrue(result, "It should be true if the customer ID is valid.");
    }
    @Test
    void checkCustomerIdNullIdReturnsFalse() {
        // Act
        boolean result = dataCollectionService.checkCustomerId(null);
        // Assert
        assertFalse(result, "It should be false if the customer ID is null.");
    }
    @Test
    void checkCustomerIdEmptyIdReturnsFalse() {
        // Arrange
        String emptyCustomerId = "";
        // Act
        boolean result = dataCollectionService.checkCustomerId(emptyCustomerId);
        // Assert
        assertFalse(result, "It should be false if the customer ID is empty.");
    }
    @Test
    void checkCustomerIdInvalidIdReturnsFalse() {
        // Arrange
        String invalidCustomerId = "abc";
        // Act
        boolean result = dataCollectionService.checkCustomerId(invalidCustomerId);
        // Assert
        assertFalse(result, "It should be false if the customer ID is invalid.");
    }
    @Test
    void publishDataGatheringJobConnectsToMessagePublisherAndSendsMessage() throws IOException, TimeoutException {
        // Arrange
        String customerId = "123";
        // Act
        dataCollectionService.publishDataGatheringJob(customerId);
        // Assert
        verify(mockMessagePublisher).connect();
        verify(mockMessagePublisher).sendMessage(customerId);
    }
    @Test
    void getInvoiceAndDownloadPdfExistsDownloadsPdf() throws IOException {
        // Arrange
        String customerId = "123";
        String fileName = "invoice" + customerId + ".pdf";
        String relativePath = "files" + File.separator + fileName;
        Path absolutePath = Paths.get(System.getProperty("user.dir"), relativePath);
        File invoiceFile = absolutePath.toFile();

        when(mockHttpServletRequest.getContextPath()).thenReturn(System.getProperty("user.dir"));
        when(mockHttpServletResponse.getOutputStream()).thenReturn(mockServletOutputStream);
        when(mockHttpServletRequest.getRequestURI()).thenReturn(relativePath);

        File tempFile = File.createTempFile("invoice123", ".pdf");
        assertTrue(tempFile.exists(), "The temporary file should exist.");
        Files.move(tempFile.toPath(), absolutePath, StandardCopyOption.REPLACE_EXISTING);
        // Act
        boolean result = dataCollectionService.getInvoiceAndDownload(customerId, mockHttpServletResponse);
        // Assert
        assertTrue(result, "It should be true if the PDF exists and is downloaded.");
        verify(mockHttpServletResponse).setContentType("application/pdf");
        verify(mockHttpServletResponse).setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        verify(mockHttpServletResponse).setContentLength((int) invoiceFile.length());
        // Clean up
        tempFile.delete();
        invoiceFile.delete();
    }
    @Test
    void getInvoiceAndDownloadPdfExistsDownloadsPdfError() throws IOException {
        // Arrange
        String customerId = "123";
        when(mockHttpServletResponse.getOutputStream()).thenReturn(mockServletOutputStream);
        doThrow(new IOException("Error")).when(mockServletOutputStream).write(any(byte[].class), anyInt(), anyInt());
        // Act
        boolean result = dataCollectionService.getInvoiceAndDownload(customerId, mockHttpServletResponse);
        // Assert
        assertFalse(result, "It should be false if an error occurs during download.");
    }

    @Test
    void getInvoiceAndDownload_PdfDoesNotExist_ReturnsFalse() {
        // Arrange
        String customerId = "123";
        // Act
        boolean result = dataCollectionService.getInvoiceAndDownload(customerId, mockHttpServletResponse);
        // Assert
        assertFalse(result, "It should be false if the PDF does not exist.");
    }
}
