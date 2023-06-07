package at.disys;


import at.disys.model.Invoice;
import at.disys.model.Station;
import at.disys.queue.QueueService;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static at.disys.queue.QueueName.*;

/**
 * This class is responsible for receiving the data collection from the data
 * collector. <br>
 *
 * <i> <br>
 *     Receives all collected data<br>
 *     Sort the data to the according gathering job<br>
 *     Sends data to the PDF Generator when the data is complete
 * </i>
 */

public class DataCollectionReceiver {
    private final QueueService dispatcherReceiverQueue = new QueueService(DISPATCHER_RECEIVER_QUEUE.getName());
    private final QueueService collectorReceiverQueue = new QueueService(COLLECTOR_RECEIVER_QUEUE.getName());
    private final QueueService receiverPdfQueue = new QueueService(RECEIVER_PDF_QUEUE.getName());
    public Invoice invoice = new Invoice();
    boolean fromDispatcherReceived = false;
    boolean fromCollectorReceived = false;





    /**
     * Ends the data gathering job
     */
    public static void main(String[] args) {
        DataCollectionReceiver dataCollectionReceiver = new DataCollectionReceiver();
        dataCollectionReceiver.receiveDataCollectionJob();
    }

    /**
     * Receives the data collection job from the data collector and sends it to the
     * PDF Generator when the data is complete
     */
    public void receiveDataCollectionJob() {
        try {
            //receive from
            dispatcherReceiverQueue.connect();
            collectorReceiverQueue.connect();
            while (true) {
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String receivedMessage = new String(delivery.getBody(), "UTF-8");
                    System.out.println("Received message:" + receivedMessage);
                    try {
                        //sent to
                        receiverPdfQueue.connect();
                        processDispatcher(receivedMessage, delivery.getEnvelope().getRoutingKey().equals(DISPATCHER_RECEIVER_QUEUE.getName()));
                        processCollector(receivedMessage, delivery.getEnvelope().getRoutingKey().equals(COLLECTOR_RECEIVER_QUEUE.getName()));

                        processJob();
                    } catch (TimeoutException e) {
                        throw new RuntimeException(e);
                    }
                };
                dispatcherReceiverQueue.getChannel().basicConsume(dispatcherReceiverQueue.getQueueName(), true, deliverCallback, consumerTag -> { });
                collectorReceiverQueue.getChannel().basicConsume(collectorReceiverQueue.getQueueName(), true, deliverCallback, consumerTag -> { });
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * if both messages are received, the data gathering process ends and then send gathered data to pdf generator
     */
    protected void processJob() throws IOException {
        if(fromDispatcherReceived && fromCollectorReceived) {
            invoice.setTotalKwh(invoice.getLocations().stream().mapToLong(Station::getTotalKwh).sum());
            receiverPdfQueue.sendMessage(invoice.toCsv().toString());
            //reset variables
            invoice = new Invoice();
            fromDispatcherReceived = false;
            fromCollectorReceived = false;
        }
    }

    /**
     * Processes the message received from the dispatcher
     * @param receivedMessage the message received from the dispatcher
     * @param isDispatcher true if the message is from the dispatcher, false otherwise
     */
    protected void processDispatcher(String receivedMessage, boolean isDispatcher) {
        if (isDispatcher) {
            invoice.setInvoiceNumber(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
            invoice.setCustomerId(Long.parseLong(receivedMessage));
            invoice.setInvoiceDate(LocalDate.now());
            fromDispatcherReceived = true;
        }
    }

    /**
     * Processes the message received from the collector
     * @param receivedMessage the message received from the collector
     * @param isCollector true if the message is from the collector, false otherwise
     */
    protected void processCollector(String receivedMessage, boolean isCollector) {
        if(isCollector) {
            //if message is END, then send to pdf generator
            if (receivedMessage.equals("END")) {
                fromCollectorReceived = true;
            }else {
                String[] messageParts = receivedMessage.split(",");
                Station station = new Station();
                station.setURLString(messageParts[0]);
                station.setTotalKwh(Long.parseLong(messageParts[1]));
                invoice.getLocations().add(station);
            }
        }
    }
}
