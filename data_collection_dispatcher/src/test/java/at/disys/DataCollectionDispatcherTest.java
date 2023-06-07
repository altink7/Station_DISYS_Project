package at.disys;

import at.disys.db.DatabaseConnector;
import at.disys.queue.QueueService;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DataCollectionDispatcherTest {

    private DataCollectionDispatcher dispatcher;
    private QueueService springDispatcherQueue;
    private QueueService dispatcherCollectorQueue;
    private QueueService dispatcherReceiverQueue;
    private DatabaseConnector databaseConnector;

    @BeforeEach
    public void setUp() {
        springDispatcherQueue = Mockito.mock(QueueService.class);
        dispatcherCollectorQueue = Mockito.mock(QueueService.class);
        dispatcherReceiverQueue = Mockito.mock(QueueService.class);
        databaseConnector = Mockito.mock(DatabaseConnector.class);

        dispatcher = new DataCollectionDispatcher(springDispatcherQueue, dispatcherCollectorQueue, dispatcherReceiverQueue, databaseConnector);
    }

    @Test
    public void testDispatchDataCollectionJob() throws IOException, TimeoutException {
        Channel channel = Mockito.mock(Channel.class);
        when(springDispatcherQueue.getChannel()).thenReturn(channel);

        dispatcher.dispatchDataCollectionJob();

        verify(springDispatcherQueue, times(1)).connect();
        verify(channel, times(1)).basicConsume(anyString(), eq(true), (DeliverCallback) any(), (CancelCallback) any());
    }


    @Test
    public void testSendMessageForEachStation() throws IOException, SQLException, TimeoutException {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        when(databaseConnector.executeSQLQuery(DataCollectionDispatcher.QUERY)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        dispatcher.sendMessageForEachStation(dispatcherCollectorQueue, 1L);

        verify(databaseConnector, times(1)).executeSQLQuery(DataCollectionDispatcher.QUERY);
        verify(dispatcherCollectorQueue, times(2)).sendMessage(anyString());
    }
}
