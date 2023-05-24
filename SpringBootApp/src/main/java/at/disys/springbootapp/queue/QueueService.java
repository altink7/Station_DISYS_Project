package at.disys.springbootapp.queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * This class is responsible for base implementation of a queue service
 */

public class QueueService {
    protected static final String RABBITMQ_HOST = "localhost";
    protected static final int RABBITMQ_PORT = 30003;
    protected final ConnectionFactory factory;
    protected Connection connection;
    protected Channel channel;
    protected String queueName;

    public QueueService(String queueName) {
        this.queueName = queueName;

        factory = new ConnectionFactory();
        factory.setPort(RABBITMQ_PORT);
        factory.setHost(RABBITMQ_HOST);
    }

    /**
     * Connect to RabbitMQ and create a queue with the specified name
     * @throws IOException if there's an issue creating the queue
     * @throws TimeoutException if there's a timeout connecting to RabbitMQ
     */
    public void connect() throws IOException, TimeoutException {
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(queueName, true, false, false, null);
    }

    /**
     * Close the connection to RabbitMQ
     * @throws IOException if there's an issue closing the connection
     * @throws TimeoutException if there's a timeout closing the connection
     */
    public void close() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    /**
     * Send a message to the queue
     * @param message the message to send
     * @throws IOException if there's an issue sending the message
     */
    public void sendMessage(String message) throws IOException {
        channel.basicPublish("", queueName, null, message.getBytes());
        System.out.println("Sent message: " + message);
    }
}