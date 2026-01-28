package pl.put.poznan.transformer.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import pl.put.poznan.transformer.logic.BuildingClasses;
import java.util.ArrayList;
import java.util.List;

/**
 * Visitor implementation for generating luminosity reports.
 * Uses Visitor Pattern to traverse building structure and collect luminosity data.
 */
public class LuminosityReportVisitor implements BuildingClasses.Visitor {

    /**
     * DTO for the complete building luminosity report.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LuminosityReport {
        public String buildingId;
        public String buildingName;
        public double averageLuminosity;
        public List<LevelReport> levels;
    }

    /**
     * DTO for luminosity information for a specific level.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LevelReport {
        public String levelId;
        public String levelName;
        public double averageLuminosity;
        public List<RoomReport> rooms;
    }

    /**
     * DTO for luminosity information for an individual room.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RoomReport {
        public String roomId;
        public String roomName;
        public double luminosity;
    }

    private LuminosityReport report;
    private LevelReport currentLevelReport;
    private final BuildingClasses calculator = new BuildingClasses();

    /**
     * Visits a building to initialize the report and calculate its average luminosity.
     * @param building The building being visited.
     */
    @Override
    public void visit(BuildingClasses.Building building) {
        report = new LuminosityReport();
        report.buildingId = building.id;
        report.buildingName = building.name;
        report.averageLuminosity = calculator.calculateLuminosity(building);
        report.levels = new ArrayList<>();
    }

    /**
     * Visits a level to calculate its average luminosity and prepare the level report.
     * @param level The level being visited.
     */
    @Override
    public void visit(BuildingClasses.Level level) {
        currentLevelReport = new LevelReport();
        currentLevelReport.levelId = level.id;
        currentLevelReport.levelName = level.name;
        currentLevelReport.averageLuminosity = calculator.calculateLuminosity(level);
        currentLevelReport.rooms = new ArrayList<>();
        if (report != null) {
            report.levels.add(currentLevelReport);
        }
    }

    /**
     * Visits a room to calculate its specific luminosity and add it to the level report.
     * @param room The room being visited.
     */
    @Override
    public void visit(BuildingClasses.Room room) {
        if (currentLevelReport != null) {
            RoomReport roomReport = new RoomReport();
            roomReport.roomId = room.id;
            roomReport.roomName = room.name;
            roomReport.luminosity = calculator.calculateLuminosity(room);
            currentLevelReport.rooms.add(roomReport);
        }
    }

    /**
     * Returns the generated luminosity report.
     * @return The completed LuminosityReport object.
     */
    public LuminosityReport getReport() {
        return report;
    }
}
