package pl.put.poznan.transformer.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

/**
 * Decorator that adds logging functionality to Reader operations.
 * This is a concrete decorator in the Decorator pattern.
 * Logs all read and write operations before delegating to wrapped Reader.
 */
public class LoggingJsonReader implements Reader {
    private static final Logger logger = LoggerFactory.getLogger(LoggingJsonReader.class);
    private Reader wrapped;

    /**
     * Constructor - wraps another Reader with logging functionality
     * @param wrapped The Reader instance to decorate
     */
    public LoggingJsonReader(Reader wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Reads and logs JSON string transformation to an object.
     * @param input The JSON string to be parsed.
     * @param clazz The class of the resulting object.
     * @param <T> The type of the object.
     * @return The parsed object.
     * @throws Exception If parsing fails.
     */
    @Override
    public <T> T read(String input, Class<T> clazz) throws Exception {
        logger.info("Reading JSON string for class: {}", clazz.getSimpleName());
        logger.debug("JSON content: {}", input);

        T result = wrapped.read(input, clazz);

        logger.info("Successfully parsed object of type: {}", clazz.getSimpleName());
        logger.debug("Parsed object: {}", result);

        return result;
    }

    /**
     * Reads and logs JSON transformation from a file to an object.
     * @param file The file containing JSON data.
     * @param clazz The class of the resulting object.
     * @param <T> The type of the object.
     * @return The parsed object.
     * @throws Exception If file reading or parsing fails.
     */
    @Override
    public <T> T readFromFile(File file, Class<T> clazz) throws Exception {
        logger.info("Reading JSON from file: {} for class: {}", file.getAbsolutePath(), clazz.getSimpleName());

        T result = wrapped.readFromFile(file, clazz);

        logger.info("Successfully parsed object from file: {}", file.getName());
        logger.debug("Parsed object: {}", result);

        return result;
    }

    /**
     * Serializes an object to a JSON string and logs the process.
     * @param obj The object to be serialized.
     * @return A JSON string representation of the object.
     * @throws Exception If serialization fails.
     */
    @Override
    public String write(Object obj) throws Exception {
        logger.info("Writing object to JSON string: {}", obj.getClass().getSimpleName());
        logger.debug("Object content: {}", obj);

        String result = wrapped.write(obj);

        logger.info("Successfully serialized object to JSON");
        logger.debug("JSON output: {}", result);

        return result;
    }

    /**
     * Serializes an object to a JSON file and logs the process.
     * @param obj The object to be serialized.
     * @param file The target file.
     * @throws Exception If writing to file fails.
     */
    @Override
    public void writeToFile(Object obj, File file) throws Exception {
        logger.info("Writing object to JSON file: {} (type: {})",
                file.getAbsolutePath(), obj.getClass().getSimpleName());
        logger.debug("Object content: {}", obj);

        wrapped.writeToFile(obj, file);

        logger.info("Successfully wrote JSON to file: {}", file.getName());
    }
}