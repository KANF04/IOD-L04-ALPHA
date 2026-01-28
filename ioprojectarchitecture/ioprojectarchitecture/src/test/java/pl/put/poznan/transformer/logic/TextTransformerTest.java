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
 * Unit tests for TextTransformer class.
 * Uses Mockito to mock the Reader interface and verify proper delegation.
 */
@ExtendWith(MockitoExtension.class)
class TextTransformerTest {

    @Mock
    private Reader mockReader;

    private TextTransformer textTransformer;

    @BeforeEach
    void setUp() {
        textTransformer = new TextTransformer(new String[]{"transform1"}, mockReader);
    }

    // ==================== Mock-verified tests ====================

    @Test
    void testTransformFromStringUsesReader() throws Exception {
        String jsonInput = "{\"building\":{\"id\":\"b1\",\"name\":\"Test Building\"}}";
        BuildingClasses buildingClasses = new BuildingClasses();
        buildingClasses.building = new BuildingClasses.Building();
        buildingClasses.building.id = "b1";
        buildingClasses.building.name = "Test Building";

        when(mockReader.read(jsonInput, BuildingClasses.class)).thenReturn(buildingClasses);

        textTransformer.transformFromString(jsonInput);

        verify(mockReader, times(1)).read(jsonInput, BuildingClasses.class);
    }

    @Test
    void testTransformUsesReaderFromFile() throws Exception {
        File testFile = new File("test.json");
        BuildingClasses buildingClasses = new BuildingClasses();
        buildingClasses.building = new BuildingClasses.Building();
        buildingClasses.building.id = "b1";
        buildingClasses.building.name = "Test Building";

        when(mockReader.readFromFile(testFile, BuildingClasses.class)).thenReturn(buildingClasses);

        textTransformer.transform(testFile);

        verify(mockReader, times(1)).readFromFile(testFile, BuildingClasses.class);
    }

    @Test
    void testSaveToFileUsesWriter() throws Exception {
        BuildingClasses buildingClasses = new BuildingClasses();
        buildingClasses.building = new BuildingClasses.Building();
        buildingClasses.building.id = "b1";
        File testFile = new File("output.json");

        textTransformer.saveToFile(buildingClasses, testFile);

        verify(mockReader, times(1)).writeToFile(buildingClasses, testFile);
    }

    @Test
    void testToJsonStringUsesWriter() throws Exception {
        BuildingClasses buildingClasses = new BuildingClasses();
        buildingClasses.building = new BuildingClasses.Building();
        buildingClasses.building.id = "b1";
        String expectedJson = "{\"building\":{\"id\":\"b1\"}}";

        when(mockReader.write(buildingClasses)).thenReturn(expectedJson);

        String result = textTransformer.toJsonString(buildingClasses);

        verify(mockReader, times(1)).write(buildingClasses);
        assertEquals(expectedJson, result);
    }

    @Test
    void testGetReaderReturnsInjectedReader() {
        Reader returnedReader = textTransformer.getReader();

        assertSame(mockReader, returnedReader);
    }
}
