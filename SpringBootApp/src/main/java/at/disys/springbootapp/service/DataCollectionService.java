package at.disys.springbootapp.service;

import at.disys.springbootapp.queue.QueueName;
import at.disys.springbootapp.queue.QueueService;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
}
