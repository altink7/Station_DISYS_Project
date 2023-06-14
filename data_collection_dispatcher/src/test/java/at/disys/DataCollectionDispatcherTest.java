package at.disys;

import at.disys.db.DatabaseConnector;
import at.disys.queue.QueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DataCollectionDispatcherTest {
    @Mock
    private QueueService dispatcherCollectorQueue;
    @Mock
    private DatabaseConnector databaseConnector;
    @Mock
    ResultSet resultSet;
    @InjectMocks
    private DataCollectionDispatcher dispatcher;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendMessageForEachStation() throws IOException, SQLException, TimeoutException {
        // Arrange
        when(databaseConnector.executeSQLQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        // Act
        dispatcher.sendMessageForEachStation(dispatcherCollectorQueue, 1L);
        // Assert - we want to execute the query once
        verify(databaseConnector, times(1)).executeSQLQuery(anyString());
        //one call for stations and one call for END message
        verify(dispatcherCollectorQueue, times(2)).sendMessage(anyString());
        //one time we should definitively END message
        verify(dispatcherCollectorQueue, times(1)).sendMessage("END");
    }
}
