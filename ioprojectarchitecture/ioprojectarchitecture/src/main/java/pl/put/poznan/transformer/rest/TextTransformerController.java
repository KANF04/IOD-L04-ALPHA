package pl.put.poznan.transformer.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.put.poznan.transformer.logic.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.File;

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
    public ResponseEntity<String> uploadJson(@RequestParam("file") MultipartFile file,
                                             @RequestParam(name="option", required=false) String[] options) {
        try {

            if (options != null) {
                for (String opt : options) {
                    System.out.println("Zaznaczona opcja: " + opt);
                }
            }


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
}