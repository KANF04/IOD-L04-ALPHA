package pl.put.poznan.transformer.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import pl.put.poznan.transformer.logic.BuildingClasses;
import java.util.ArrayList;
import java.util.List;

/**
 * Visitor implementation for generating hierarchical volume reports from building structures.
 *
 * This class implements the <b>Visitor Pattern</b> to traverse a building's structure
 * (Building → Levels → Rooms) and generate a comprehensive volume report. Volume
 * measurements are calculated by summing the cubic volume of all spaces.
 *
 */
public class VolumeReportVisitor implements BuildingClasses.Visitor {

    /**
     * Data Transfer Object representing a complete volume report for a building.
     *
     * This is the root of the report hierarchy and contains all volume information
     * for the building, including nested levels and rooms.
     *
     * Fields marked with {@code @JsonInclude(NON_NULL)} will be omitted from
     * JSON serialization if they are null.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class VolumeReport {
        /** Unique identifier of the building */
        public String buildingId;

        /** Name of the building */
        public String buildingName;

        /** Total volume of the entire building in cubic meters (sum of all room volumes) */
        public double totalVolume;

        /** List of level reports contained in this building */
        public List<LevelReport> levels;
    }

    /**
     * Data Transfer Object representing volume information for a single level (floor).
     *
     * Contains total volume for the level (sum of room volumes) and detailed reports
     * for each room.
     *
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LevelReport {
        /** Unique identifier of the level */
        public String levelId;

        /** Name of the level */
        public String levelName;

        /** Total volume of this level in cubic meters (sum of all room volumes on this level) */
        public double totalVolume;

        /** List of room reports on this level */
        public List<RoomReport> rooms;
    }

    /**
     * Data Transfer Object representing volume information for a single room.
     *
     * This is the leaf level of the report hierarchy, containing individual room details.
     *
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RoomReport {
        /** Unique identifier of the room */
        public String roomId;

        /** Name of the room */
        public String roomName;

        /** Volume of this room in cubic meters */
        public double volume;
    }

    /** The root report being constructed during traversal */
    private VolumeReport report;

    /**
     * Temporary reference to the level report currently being populated with rooms.
     * This is necessary because room visits need to know which level to add themselves to.
     */
    private LevelReport currentLevelReport;

    /** Calculator instance for performing volume calculations */
    private final BuildingClasses calculator = new BuildingClasses();

    /**
     * Visits a Building node and initializes the root volume report.
     *
     * This is the first method called during traversal. It creates the root report
     * object and calculates the total volume by summing all room volumes in all levels.
     *
     *
     * <p><b>State Changes:</b> Initializes {@link #report} with building-level data.</p>
     *
     * <p><b>Calculation:</b> Total volume is the sum of all room volumes throughout
     * the building.</p>
     *
     * @param building The building to generate a report for
     */
    @Override
    public void visit(BuildingClasses.Building building) {
        report = new VolumeReport();
        report.buildingId = building.id;
        report.buildingName = building.name;
        report.totalVolume = calculator.calculateVolume(building);
        report.levels = new ArrayList<>();
    }

    /**
     * Visits a Level node and creates a level volume report.
     *
     * This method is called for each level in the building. It creates a level report,
     * calculates the total volume by summing all room volumes on the level, and adds
     * the report to the building's level list. The created report becomes the current
     * level for subsequent room visits.
     *
     * <p><b>State Changes:</b> Sets {@link #currentLevelReport} and adds it to the
     * building's levels list.</p>
     *
     * <p><b>Calculation:</b> Total volume is the sum of all room volumes on this level.</p>
     *
     * @param level The level to generate a report for
     */
    @Override
    public void visit(BuildingClasses.Level level) {
        currentLevelReport = new LevelReport();
        currentLevelReport.levelId = level.id;
        currentLevelReport.levelName = level.name;
        currentLevelReport.totalVolume = calculator.calculateVolume(level);
        currentLevelReport.rooms = new ArrayList<>();
        if (report != null) {
            report.levels.add(currentLevelReport);
        }
    }

    /**
     * Visits a Room node and creates a room volume report.
     *
     * This method is called for each room in a level. It creates a room report with
     * the room's volume and adds it to the current level's
     * room list.
     *
     * <p><b>State Changes:</b> Adds a room report to {@link #currentLevelReport}.rooms.</p>
     *
     * <p>Precondition {@link #currentLevelReport} must not be null
     * </p>
     *
     * <p><b>Calculation:</b> Room volume is taken directly from room.cube field.</p>
     *
     * @param room The room to generate a report for
     */
    @Override
    public void visit(BuildingClasses.Room room) {
        if (currentLevelReport != null) {
            RoomReport roomReport = new RoomReport();
            roomReport.roomId = room.id;
            roomReport.roomName = room.name;
            roomReport.volume = calculator.calculateVolume(room);
            currentLevelReport.rooms.add(roomReport);
        }
    }

    /**
     * Returns the complete volume report after traversal.
     *
     * This method should be called after the building's {@code accept(this)} method
     * has completed traversal. The returned report contains the complete hierarchy
     * of volume information.
     *
     *
     * @return The complete VolumeReport, or null if no building has been visited
     */
    public VolumeReport getReport() {
        return report;
    }
}