package pl.put. poznan.transformer.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import pl.put.poznan.transformer. logic.BuildingClasses;
import java.util.ArrayList;
import java.util.List;

/**
 * Visitor implementation for generating area reports
 * Uses Visitor Pattern to traverse building structure
 */
public class AreaReportVisitor implements BuildingClasses.Visitor {

    /**
     * DTO representing a complete area report for a building.
     */
    @JsonInclude(JsonInclude. Include.NON_NULL)
    public static class AreaReport {
        public String buildingId;
        public String buildingName;
        public double totalArea;
        public List<LevelReport> levels;
    }

    /**
     * DTO representing area information for a specific level.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LevelReport {
        public String levelId;
        public String levelName;
        public double totalArea;
        public List<RoomReport> rooms;
    }

    /**
     * DTO representing area information for an individual room.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RoomReport {
        public String roomId;
        public String roomName;
        public double area;
    }

    private AreaReport report;
    private LevelReport currentLevelReport;
    private final BuildingClasses calculator = new BuildingClasses();

    /**
     * Visits a building to initialize the area report and calculate total building area.
     * @param building The building being visited.
     */
    @Override
    public void visit(BuildingClasses.Building building) {
        report = new AreaReport();
        report.buildingId = building.id;
        report.buildingName = building.name;
        report. totalArea = calculator.calculateArea(building);
        report.levels = new ArrayList<>();
    }

    /**
     * Visits a level to calculate its total area and prepare its report.
     * @param level The level being visited.
     */
    @Override
    public void visit(BuildingClasses. Level level) {
        currentLevelReport = new LevelReport();
        currentLevelReport.levelId = level.id;
        currentLevelReport.levelName = level.name;
        currentLevelReport.totalArea = calculator. calculateArea(level);
        currentLevelReport.rooms = new ArrayList<>();
        if (report != null) {
            report.levels.add(currentLevelReport);
        }
    }

    /**
     * Visits a room to extract its area and add it to the level report.
     * @param room The room being visited.
     */
    @Override
    public void visit(BuildingClasses.Room room) {
        if (currentLevelReport != null) {
            RoomReport roomReport = new RoomReport();
            roomReport.roomId = room.id;
            roomReport.roomName = room. name;
            roomReport.area = room.area;
            currentLevelReport.rooms.add(roomReport);
        }
    }

    /**
     * Returns the generated area report.
     * @return The completed AreaReport object.
     */
    public AreaReport getReport() {
        return report;
    }
}