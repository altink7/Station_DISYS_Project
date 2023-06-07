package at.disys;

import at.disys.db.DatabaseConnector;
import at.disys.queue.QueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * test for business logic
 * @see DataCollectionDispatcher
 */
public class DataCollectionDispatcherTest {
    @Mock
    private QueueService dispatcherCollectorQueue;
    @Mock
    private DatabaseConnector databaseConnector;
    @InjectMocks
    private DataCollectionDispatcher dispatcher;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendMessageForEachStation() throws IOException, SQLException, TimeoutException {
        //Arrange
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        when(databaseConnector.executeSQLQuery(DataCollectionDispatcher.QUERY)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        //Act
        dispatcher.sendMessageForEachStation(dispatcherCollectorQueue, 1L);

        //it should be at least 2 times, one Station and one END message
        verify(dispatcherCollectorQueue, atLeast(2)).sendMessage(anyString());

        //for default
        //Assert - we want to make sure we sent for each station a message, and also the END Message ( 3 + 1 ), therefore should be 4
        verify(dispatcherCollectorQueue, times(4)).sendMessage(anyString());
    }
}
