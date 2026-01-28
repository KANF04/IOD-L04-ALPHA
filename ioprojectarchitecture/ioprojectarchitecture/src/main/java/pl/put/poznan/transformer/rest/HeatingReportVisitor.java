package pl.put.poznan.transformer.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import pl.put.poznan.transformer.logic.BuildingClasses;
import java.util.ArrayList;
import java.util.List;

/**
 * Visitor implementation for generating heating energy consumption reports.
 * Uses Visitor Pattern to traverse building structure and calculate heating metrics.
 *
 * This visitor calculates:
 * - Total heating energy consumption at building, level, and room granularity
 * - Average heating per unit volume (heating efficiency metric)
 */
public class HeatingReportVisitor implements BuildingClasses.Visitor {

    /**
     * Data Transfer Object representing a complete heating report for a building.
     *
     * Contains total heating consumption and efficiency metrics (heating per volume)
     * for the entire building hierarchy.
     *
     * Fields marked with {@code @JsonInclude(NON_NULL)} will be omitted from
     * JSON serialization if they are null.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HeatingReport {
        /** Unique identifier of the building */
        public String buildingId;

        /** Name of the building */
        public String buildingName;

        /** Total heating energy consumption for the entire building */
        public double totalHeating;

        /** Average heating per cubic meter (heating efficiency metric) */
        public double averageHeatingPerVolume;

        /** List of level reports contained in this building */
        public List<LevelReport> levels;
    }

    /**
     * Data Transfer Object representing heating information for a single level (floor).
     *
     * Contains total heating consumption and efficiency metrics for the level.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LevelReport {
        /** Unique identifier of the level */
        public String levelId;

        /** Name of the level */
        public String levelName;

        /** Total heating energy consumption for this level */
        public double totalHeating;

        /** Average heating per cubic meter for this level */
        public double averageHeatingPerVolume;

        /** List of room reports on this level */
        public List<RoomReport> rooms;
    }

    /**
     * Data Transfer Object representing heating information for a single room.
     *
     * This is the leaf level of the report hierarchy, containing individual room details.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RoomReport {
        /** Unique identifier of the room */
        public String roomId;

        /** Name of the room */
        public String roomName;

        /** Heating energy consumption for this room */
        public double heating;

        /** Heating per cubic meter for this room */
        public double heatingPerVolume;
    }

    /** The root report being constructed during traversal */
    private HeatingReport report;

    /**
     * Temporary reference to the level report currently being populated with rooms.
     * This is necessary because room visits need to know which level to add themselves to.
     */
    private LevelReport currentLevelReport;

    /** Calculator instance for performing heating calculations */
    private final BuildingClasses calculator = new BuildingClasses();

    /**
     * Visits a Building node and initializes the root heating report.
     *
     * This is the first method called during traversal. It creates the root report
     * object and calculates the total heating and average heating per volume.
     *
     * @param building The building to generate a report for
     */
    @Override
    public void visit(BuildingClasses.Building building) {
        report = new HeatingReport();
        report.buildingId = building.id;
        report.buildingName = building.name;
        report.totalHeating = calculator.calculateHeating(building);
        report.averageHeatingPerVolume = calculator.calculateAverageHeatingPerVolume(building);
        report.levels = new ArrayList<>();
    }

    /**
     * Visits a Level node and creates a level heating report.
     *
     * This method is called for each level in the building. It creates a level report,
     * calculates heating metrics, and adds it to the building's level list.
     *
     * @param level The level to generate a report for
     */
    @Override
    public void visit(BuildingClasses.Level level) {
        currentLevelReport = new LevelReport();
        currentLevelReport.levelId = level.id;
        currentLevelReport.levelName = level.name;
        currentLevelReport.totalHeating = calculator.calculateHeating(level);
        currentLevelReport.averageHeatingPerVolume = calculator.calculateAverageHeatingPerVolume(level);
        currentLevelReport.rooms = new ArrayList<>();
        if (report != null) {
            report.levels.add(currentLevelReport);
        }
    }

    /**
     * Visits a Room node and creates a room heating report.
     *
     * This method is called for each room in a level. It creates a room report with
     * the room's heating consumption and efficiency metrics, and adds it to the current
     * level's room list.
     *
     * @param room The room to generate a report for
     */
    @Override
    public void visit(BuildingClasses.Room room) {
        if (currentLevelReport != null) {
            RoomReport roomReport = new RoomReport();
            roomReport.roomId = room.id;
            roomReport.roomName = room.name;
            roomReport.heating = room.heating;
            roomReport.heatingPerVolume = calculator.calculateHeatingPerVolume(room);
            currentLevelReport.rooms.add(roomReport);
        }
    }

    /**
     * Returns the completed heating report after traversal.
     *
     * @return The complete heating report, or null if traversal hasn't started
     */
    public HeatingReport getReport() {
        return report;
    }
}