package at.disys.queue;

/**
 * This enum contains the names of the queues used in the application.
 */
public enum QueueName {
    APP_DISPATCHER_QUEUE("app-dispatcher-queue"), //red queue
    DISPATCHER_COLLECTOR_QUEUE("dispatcher-collector-queue"), //green queue
    DISPATCHER_RECEIVER_QUEUE("dispatcher-receiver-queue"), //purple queue
    COLLECTOR_RECEIVER_QUEUE("collector-receiver-queue"), // blue queue
    RECEIVER_PDF_QUEUE("receiver-pdf-queue"); //yellow queue

    private final String name;

    QueueName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
