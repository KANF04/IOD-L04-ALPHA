package pl.put.poznan.transformer.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.put.poznan.transformer.logic.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.util.Optional;

/**
 * REST Controller for text transformation operations
 * Uses Decorator Pattern for JSON processing
 */
@Controller
public class TextTransformerController {

    private static final Logger logger = LoggerFactory.getLogger(TextTransformerController.class);

    /**
     * Home page redirect
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }

    /**
     * Upload and process JSON file
     * Uses decorated Reader for JSON operations
     */
    @PostMapping("/uploadJson")
    @ResponseBody
    public ResponseEntity<String> uploadJson(@RequestParam("file") MultipartFile file) {
        try {
            logger.info("Received file upload: {}", file.getOriginalFilename());

            // Create temporary file
            File tempFile = File.createTempFile("upload-", ".json");
            file.transferTo(tempFile);

            logger.info("File saved to temporary location: {}", tempFile.getAbsolutePath());

            // Create decorated reader: Base -> Logging
            Reader reader = new LoggingJsonReader(new JsonReader());

            // Create transformer with decorated reader
            TextTransformer transformer = new TextTransformer(new String[]{}, reader);

            // Process the file
            transformer.transform(tempFile);

            // Clean up temporary file
            tempFile.delete();

            logger.info("File processing completed successfully");

            return ResponseEntity.ok("File processed successfully");

        } catch (Exception e) {
            logger.error("Error processing uploaded file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        }
    }

    /**
     * Process JSON string directly
     * Uses decorated Reader for JSON operations
     */
    @PostMapping("/processJson")
    @ResponseBody
    public ResponseEntity<String> processJson(@RequestBody String jsonContent) {
        try {
            logger.info("Received JSON content for processing");

            // Create decorated reader: Base -> Logging
            Reader reader = new LoggingJsonReader(new JsonReader());

            // Create transformer with decorated reader
            TextTransformer transformer = new TextTransformer(new String[]{}, reader);

            // Process the JSON string
            transformer.transformFromString(jsonContent);

            logger.info("JSON processing completed successfully");

            return ResponseEntity.ok("JSON processed successfully");

        } catch (Exception e) {
            logger.error("Error processing JSON content", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing JSON: " + e.getMessage());
        }
    }

    /**
     * Read building data from uploaded file and return as JSON
     */
    @PostMapping("/readBuilding")
    @ResponseBody
    public ResponseEntity<String> readBuilding(@RequestParam("file") MultipartFile file) {
        try {
            logger.info("Reading building data from file: {}", file.getOriginalFilename());

            // Create temporary file
            File tempFile = File.createTempFile("read-", ".json");
            file.transferTo(tempFile);

            // Create decorated reader: Base -> Logging
            Reader reader = new LoggingJsonReader(new JsonReader());

            // Read building data
            BuildingClasses wrapper = reader.readFromFile(tempFile, BuildingClasses.class);

            // Convert back to JSON string
            String jsonResult = reader.write(wrapper);

            // Clean up
            tempFile.delete();

            logger.info("Building data read successfully");

            return ResponseEntity.ok(jsonResult);

        } catch (Exception e) {
            logger.error("Error reading building data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading building: " + e.getMessage());
        }
    }

    /**
     * Save building data to file
     * Demonstrates write operations with decorator pattern
     */
    @PostMapping("/saveBuilding")
    @ResponseBody
    public ResponseEntity<String> saveBuilding(@RequestBody String jsonContent,
                                               @RequestParam("filename") String filename) {
        try {
            logger.info("Saving building data to file: {}", filename);

            // Create decorated reader: Base -> Logging
            Reader reader = new LoggingJsonReader(new JsonReader());

            // Parse JSON content
            BuildingClasses wrapper = reader.read(jsonContent, BuildingClasses.class);

            // Save to file
            File outputFile = new File(filename);
            reader.writeToFile(wrapper, outputFile);

            logger.info("Building data saved successfully to: {}", outputFile.getAbsolutePath());

            return ResponseEntity.ok("Building saved to: " + outputFile.getAbsolutePath());

        } catch (Exception e) {
            logger.error("Error saving building data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving building: " + e.getMessage());
        }
    }

    /**
     * Calculate volume for a building, level, or room based on JSON input.
     * @param jsonContent JSON string representing the building structure.
     * @param levelId Optional ID of the level to calculate volume for.
     * @param roomId Optional ID of the room to calculate volume for.
     * @return ResponseEntity with the calculated volume or an error message.
     */
    @PostMapping(value = "/volume", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> calculateVolume(@RequestBody String jsonContent,
                                             @RequestParam(required = false) String levelId,
                                             @RequestParam(required = false) String roomId) {
        try {
            logger.info("Received volume calculation request. LevelId: {}, RoomId: {}", levelId, roomId);

            ObjectMapper mapper = new ObjectMapper();
            BuildingClasses buildingData = mapper.readValue(jsonContent, BuildingClasses.class);

            if (buildingData.building == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON: 'building' object is missing.");
            }

            // Case 1: Calculate for a specific room
            if (roomId != null) {
                if (levelId == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parameter 'levelId' is required when 'roomId' is provided.");
                }
                Optional<BuildingClasses.Room> roomOptional = buildingData.findRoomById(levelId, roomId);
                if (roomOptional.isPresent()) {
                    return ResponseEntity.ok(buildingData.calculateVolume(roomOptional.get()));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Room with id '" + roomId + "' on level '" + levelId + "' not found.");
                }
            }

            // Case 2: Calculate for a specific level
            if (levelId != null) {
                Optional<BuildingClasses.Level> levelOptional = buildingData.findLevelById(levelId);
                if (levelOptional.isPresent()) {
                    return ResponseEntity.ok(buildingData.calculateVolume(levelOptional.get()));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Level with id '" + levelId + "' not found.");
                }
            }

            // Case 3: Calculate for the whole building
            double totalVolume = buildingData.calculateVolume(buildingData.building);
            return ResponseEntity.ok(totalVolume);

        } catch (Exception e) {
            logger.error("Error processing volume calculation request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }
}
