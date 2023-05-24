package at.disys.springbootapp.service;

import at.disys.springbootapp.queue.QueueName;
import at.disys.springbootapp.queue.QueueService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

/**
 * Business logic for the data collection Spring Boot App.
 */

@Service
public class DataCollectionService {

    /**
     * this method starts the data gathering job
     * @param customerId customer id for the data gathering job
     */
    public void publishDataGatheringJob(String customerId) {
        QueueService messagePublisher = new QueueService(QueueName.APP_DISPATCHER_QUEUE.getName());

        try {
            messagePublisher.connect();
            messagePublisher.sendMessage(customerId);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * gets pdf from pdf generator with name invoiceLuisa_Colon.pdf (example)
     * @param customerId
     * @return
     */
    public boolean getInvoiceAndDownload(String customerId, HttpServletResponse response) {
        String fileName = "invoice" + customerId + ".pdf";
        String relativePath = "files" + File.separator + fileName;
        Path absolutePath = Paths.get(System.getProperty("user.dir"), relativePath);
        File invoiceFile = absolutePath.toFile();

        if (invoiceFile.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(invoiceFile)) {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                response.setContentLength((int) invoiceFile.length());

                OutputStream outputStream = response.getOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
}
