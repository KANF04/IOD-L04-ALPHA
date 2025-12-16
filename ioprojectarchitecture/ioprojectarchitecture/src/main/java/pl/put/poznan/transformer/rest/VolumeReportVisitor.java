package pl.put.poznan.transformer.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import pl.put.poznan.transformer.logic.BuildingClasses;
import java.util.ArrayList;
import java.util.List;

public class VolumeReportVisitor implements BuildingClasses.Visitor {

    // --- DTO classes are now inner classes ---
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class VolumeReport {
        public String buildingId;
        public String buildingName;
        public double totalVolume;
        public List<LevelReport> levels;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LevelReport {
        public String levelId;
        public String levelName;
        public double totalVolume;
        public List<RoomReport> rooms;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RoomReport {
        public String roomId;
        public String roomName;
        public double volume;
    }
    // -----------------------------------------

    private VolumeReport report;
    private LevelReport currentLevelReport;
    private final BuildingClasses calculator = new BuildingClasses();

    @Override
    public void visit(BuildingClasses.Building building) {
        report = new VolumeReport();
        report.buildingId = building.id;
        report.buildingName = building.name;
        report.totalVolume = calculator.calculateVolume(building);
        report.levels = new ArrayList<>();
    }

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

    public VolumeReport getReport() {
        return report;
    }
}
