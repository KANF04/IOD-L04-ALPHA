package pl.put.poznan.transformer.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoggingJsonReader decorator.
 * Uses Mockito to mock the wrapped Reader interface and verify delegation.
 */
@ExtendWith(MockitoExtension.class)
class LoggingJsonReaderTest {

    @Mock
    private Reader wrappedReader;

    private LoggingJsonReader loggingJsonReader;

    @BeforeEach
    void setUp() {
        loggingJsonReader = new LoggingJsonReader(wrappedReader);
    }

    // ==================== Mock-verified tests ====================

    @Test
    void testReadDelegatesToWrappedReader() throws Exception {
        String jsonInput = "{\"building\": null}";
        BuildingClasses expected = new BuildingClasses();
        when(wrappedReader.read(jsonInput, BuildingClasses.class)).thenReturn(expected);

        BuildingClasses result = loggingJsonReader.read(jsonInput, BuildingClasses.class);

        verify(wrappedReader, times(1)).read(jsonInput, BuildingClasses.class);
        assertEquals(expected, result);
    }

    @Test
    void testReadFromFileDelegatesToWrappedReader() throws Exception {
        File testFile = new File("test.json");
        BuildingClasses expected = new BuildingClasses();
        when(wrappedReader.readFromFile(testFile, BuildingClasses.class)).thenReturn(expected);

        BuildingClasses result = loggingJsonReader.readFromFile(testFile, BuildingClasses.class);

        verify(wrappedReader, times(1)).readFromFile(testFile, BuildingClasses.class);
        assertEquals(expected, result);
    }

    @Test
    void testWriteDelegatesToWrappedReader() throws Exception {
        BuildingClasses input = new BuildingClasses();
        String expectedJson = "{\"building\":null}";
        when(wrappedReader.write(input)).thenReturn(expectedJson);

        String result = loggingJsonReader.write(input);

        verify(wrappedReader, times(1)).write(input);
        assertEquals(expectedJson, result);
    }

    @Test
    void testWriteToFileDelegatesToWrappedReader() throws Exception {
        BuildingClasses input = new BuildingClasses();
        File testFile = new File("output.json");

        loggingJsonReader.writeToFile(input, testFile);

        verify(wrappedReader, times(1)).writeToFile(input, testFile);
    }

    @Test
    void testReadReturnsCorrectResult() throws Exception {
        String jsonInput = "{\"building\":{\"id\":\"b1\",\"name\":\"Test\"}}";
        BuildingClasses expected = new BuildingClasses();
        expected.building = new BuildingClasses.Building();
        expected.building.id = "b1";
        expected.building.name = "Test";
        when(wrappedReader.read(jsonInput, BuildingClasses.class)).thenReturn(expected);

        BuildingClasses result = loggingJsonReader.read(jsonInput, BuildingClasses.class);

        verify(wrappedReader).read(jsonInput, BuildingClasses.class);
        assertNotNull(result);
        assertNotNull(result.building);
        assertEquals("b1", result.building.id);
        assertEquals("Test", result.building.name);
    }

    @Test
    void testWriteReturnsCorrectResult() throws Exception {
        BuildingClasses input = new BuildingClasses();
        input.building = new BuildingClasses.Building();
        input.building.id = "b1";
        String expectedJson = "{\"building\":{\"id\":\"b1\"}}";
        when(wrappedReader.write(input)).thenReturn(expectedJson);

        String result = loggingJsonReader.write(input);

        verify(wrappedReader).write(input);
        assertEquals(expectedJson, result);
    }
}
