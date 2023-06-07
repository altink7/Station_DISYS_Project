package at.disys;


import at.disys.db.DatabaseConnector;
import at.disys.model.Station;
import at.disys.queue.QueueService;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import static at.disys.queue.QueueName.*;

/**
 * This class is responsible for dispatching the data collection to the correct
 * data collector. <br>
 *
 * <i><br>
 *     Starts the data gathering job<br>
 *     Has knowledge about the available stations<br>
 *     Sends a message for every charging station to the Station Data Collector<br>
 *     Sends a message to the Data Collection Receiver, that a new job started
 * </i>
 */

public class DataCollectionDispatcher {
    public static final String QUERY = "SELECT * FROM station";

    private final QueueService springDispatcherQueue;
    private final QueueService dispatcherCollectorQueue;
    private final QueueService dispatcherReceiverQueue;
    private final DatabaseConnector databaseConnector;

    public DataCollectionDispatcher(QueueService springDispatcherQueue,
                                    QueueService dispatcherCollectorQueue,
                                    QueueService dispatcherReceiverQueue,
                                    DatabaseConnector databaseConnector) {
        this.springDispatcherQueue = springDispatcherQueue;
        this.dispatcherCollectorQueue = dispatcherCollectorQueue;
        this.dispatcherReceiverQueue = dispatcherReceiverQueue;
        this.databaseConnector = databaseConnector;
    }
    /**
     * Starts the data gathering job
     */
    public static void main(String[] args) {
        // Ensure proper instantiation with real objects in the main method
        DataCollectionDispatcher dataCollectionDispatcher = new DataCollectionDispatcher(
                new QueueService(APP_DISPATCHER_QUEUE.getName()),
                new QueueService(DISPATCHER_COLLECTOR_QUEUE.getName()),
                new QueueService(DISPATCHER_RECEIVER_QUEUE.getName()),
                new DatabaseConnector()
        );

        dataCollectionDispatcher.dispatchDataCollectionJob();
    }

    /**
     * Receives the data collection job from the queue and dispatches it to the
     * correct data collector.
     */
    public void dispatchDataCollectionJob() {
        try {
            //receive from
            springDispatcherQueue.connect();
            while (true) {
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String receivedMessage = new String(delivery.getBody(), "UTF-8");
                    System.out.println("Received message:" + receivedMessage);
                    try {
                        //sent to
                        dispatcherReceiverQueue.connect();
                        dispatcherCollectorQueue.connect();

                        sendMessageForEachStation(dispatcherCollectorQueue, Long.parseLong(receivedMessage));
                        dispatcherReceiverQueue.sendMessage(receivedMessage);
                    } catch (TimeoutException e) {
                        throw new RuntimeException(e);
                    }
                };
                springDispatcherQueue.getChannel().basicConsume(springDispatcherQueue.getQueueName(), true, deliverCallback, consumerTag -> { });
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message for every charging station to the Station Data Collector
     * @param queueService send from this queue service
     * @param customerId the customer id
     * @throws IOException problem with sending the message
     * @throws TimeoutException timeout while sending the message
     */
    public void sendMessageForEachStation(QueueService queueService, long customerId) throws IOException, TimeoutException {
        DatabaseConnector stationDb = databaseConnector;
        stationDb.connect();

        try (ResultSet resultSet = stationDb.executeSQLQuery(QUERY)) {
            while (true) {
                Station station = new Station();
                try {
                    if (!resultSet.next()){
                        queueService.sendMessage("END");
                        break;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                station.setId(resultSet.getLong("id"));
                station.setDbUrl(resultSet.getString("db_url"));
                station.setLat(resultSet.getDouble("lat"));
                station.setLng(resultSet.getDouble("lng"));

                queueService.sendMessage(station.toCsv().toString()+","+customerId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        stationDb.disconnect();
    }

}
