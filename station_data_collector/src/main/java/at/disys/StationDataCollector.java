package at.disys;

import at.disys.db.DatabaseConnector;
import at.disys.model.Charge;
import at.disys.queue.QueueService;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static at.disys.queue.QueueName.COLLECTOR_RECEIVER_QUEUE;
import static at.disys.queue.QueueName.DISPATCHER_COLLECTOR_QUEUE;

/**
 * This class is responsible for collecting the data from the station. <br>
 *
 * <i> <br>
 *     Gathers data for a specific customer from a specific charging station<br>
 *     Sends data to the Data Collection Receiver
 *</i>
 */
public class StationDataCollector {
    public static final String QUERY = "SELECT * FROM charge WHERE customer_id = ?";

    private final QueueService dispatcherCollectorQueue = new QueueService(DISPATCHER_COLLECTOR_QUEUE.getName());
    private final QueueService collectorReceiverQueue = new QueueService(COLLECTOR_RECEIVER_QUEUE.getName());

    /**
     * station data collector main method
     */

    public static void main(String[] args) {
        StationDataCollector stationDataCollector = new StationDataCollector();
        stationDataCollector.gatherDataForSpecificPersonSpecificCharge();
    }

    public void gatherDataForSpecificPersonSpecificCharge() {
        try {
            // Receive from
            dispatcherCollectorQueue.connect();
            while (true) {
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String receivedMessage = new String(delivery.getBody(), "UTF-8");
                    System.out.println("Received message:" + receivedMessage);
                    try {
                        // Sent to
                        collectorReceiverQueue.connect();
                        //if message is END, then tell the receiver that the job is done
                        if (receivedMessage.equals("END")) {
                            collectorReceiverQueue.sendMessage("END");
                        }else {
                            //split receivedMessage into url and customerId
                            String[] urlAndCustomerId = receivedMessage.split(",");
                            //send charge data to collectorReceiverQueue
                            collectorReceiverQueue.sendMessage(getSpecificDataForSpecificUrl(urlAndCustomerId[1], Long.parseLong(urlAndCustomerId[4])));
                        }

                    } catch (TimeoutException e) {
                        throw new RuntimeException(e);
                    }
                };
                dispatcherCollectorQueue.getChannel().basicConsume(dispatcherCollectorQueue.getQueueName(), true, deliverCallback, consumerTag -> {});
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public String getSpecificDataForSpecificUrl(String url, long customerId) {
        DatabaseConnector chargeDb = new DatabaseConnector();
        chargeDb.connect(DatabaseConnector.getStationUrl(url));
        List<Charge> charges = new ArrayList<>();
        StringBuilder csv = new StringBuilder();
        long totalKwh = 0;

        try (PreparedStatement statement = chargeDb.getConnection().prepareStatement(QUERY)) {
            statement.setLong(1, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Charge charge = new Charge();
                    charge.setCustomerId(resultSet.getLong("customer_id"));
                    charge.setKwh(resultSet.getLong("kwh"));
                    charges.add(charge);
                    totalKwh += charge.getKwh();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            chargeDb.disconnect();
        }
        csv.append(url).append(",").append(totalKwh);

        return csv.toString();
    }

}
