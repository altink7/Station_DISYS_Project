package at.disys;

import at.disys.db.DatabaseConnector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class StationDataCollectorTest {
    private static final String TEST_URL = "localhost:test";
    private static final long TEST_CUSTOMER_ID = 1L;
    private static final String EXPECTED_RESULT = "localhost:test,164";
    @Mock
    private DatabaseConnector databaseConnector;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;
    @InjectMocks
    private StationDataCollector stationDataCollector;

    @BeforeEach
    public void setUp() throws SQLException {
        // Arrange
        MockitoAnnotations.openMocks(this);

        when(databaseConnector.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getLong("customer_id")).thenReturn(TEST_CUSTOMER_ID);
        when(resultSet.getLong("kwh")).thenReturn(164L);
    }

    @Test
    public void testGetSpecificDataForSpecificUrl() {
        // Act
        String result = stationDataCollector.getSpecificDataForSpecificUrl(TEST_URL, TEST_CUSTOMER_ID);
        // Assert - expected String should contain url and customer id
        Assertions.assertEquals(EXPECTED_RESULT, result);
    }
}
