package pl.put.poznan.transformer.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.put.poznan.transformer.logic.BuildingClasses;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Visitor implementations: AreaReportVisitor, VolumeReportVisitor, LuminosityReportVisitor.
 * Uses Mockito to mock/spy BuildingClasses calculator and verify method calls.
 */
@ExtendWith(MockitoExtension.class)
class VisitorTest {

    private BuildingClasses.Building building;
    private BuildingClasses.Level level;
    private BuildingClasses.Room room1;
    private BuildingClasses.Room room2;

    @Mock
    private BuildingClasses.Visitor mockVisitor;

    @BeforeEach
    void setUp() {
        room1 = new BuildingClasses.Room();
        room1.id = "r1";
        room1.name = "Room 1";
        room1.area = 50.0;
        room1.cube = 150.0;
        room1.light = 500.0;

        room2 = new BuildingClasses.Room();
        room2.id = "r2";
        room2.name = "Room 2";
        room2.area = 30.0;
        room2.cube = 90.0;
        room2.light = 300.0;

        level = new BuildingClasses.Level();
        level.id = "l1";
        level.name = "Level 1";
        level.rooms = Arrays.asList(room1, room2);

        building = new BuildingClasses.Building();
        building.id = "b1";
        building.name = "Test Building";
        building.levels = Collections.singletonList(level);
    }

    // ==================== AreaReportVisitor Tests ====================

    @Test
    void testAreaReportVisitorBuilding() {
        AreaReportVisitor visitor = new AreaReportVisitor();

        building.accept(visitor);
        AreaReportVisitor.AreaReport report = visitor.getReport();

        assertNotNull(report);
        assertEquals("b1", report.buildingId);
        assertEquals("Test Building", report.buildingName);
        assertEquals(80.0, report.totalArea, 0.001); // 50 + 30
    }

    @Test
    void testAreaReportVisitorLevel() {
        AreaReportVisitor visitor = new AreaReportVisitor();

        building.accept(visitor);
        AreaReportVisitor.AreaReport report = visitor.getReport();

        assertNotNull(report);
        assertNotNull(report.levels);
        assertEquals(1, report.levels.size());

        AreaReportVisitor.LevelReport levelReport = report.levels.get(0);
        assertEquals("l1", levelReport.levelId);
        assertEquals("Level 1", levelReport.levelName);
        assertEquals(80.0, levelReport.totalArea, 0.001);
    }

    @Test
    void testAreaReportVisitorRoom() {
        AreaReportVisitor visitor = new AreaReportVisitor();

        building.accept(visitor);
        AreaReportVisitor.AreaReport report = visitor.getReport();

        assertNotNull(report);
        assertNotNull(report.levels);
        AreaReportVisitor.LevelReport levelReport = report.levels.get(0);
        assertNotNull(levelReport.rooms);
        assertEquals(2, levelReport.rooms.size());

        AreaReportVisitor.RoomReport roomReport1 = levelReport.rooms.get(0);
        assertEquals("r1", roomReport1.roomId);
        assertEquals("Room 1", roomReport1.roomName);
        assertEquals(50.0, roomReport1.area, 0.001);

        AreaReportVisitor.RoomReport roomReport2 = levelReport.rooms.get(1);
        assertEquals("r2", roomReport2.roomId);
        assertEquals(30.0, roomReport2.area, 0.001);
    }

    // ==================== VolumeReportVisitor Tests ====================

    @Test
    void testVolumeReportVisitorBuilding() {
        VolumeReportVisitor visitor = new VolumeReportVisitor();

        building.accept(visitor);
        VolumeReportVisitor.VolumeReport report = visitor.getReport();

        assertNotNull(report);
        assertEquals("b1", report.buildingId);
        assertEquals("Test Building", report.buildingName);
        assertEquals(240.0, report.totalVolume, 0.001); // 150 + 90
    }

    @Test
    void testVolumeReportVisitorLevel() {
        VolumeReportVisitor visitor = new VolumeReportVisitor();

        building.accept(visitor);
        VolumeReportVisitor.VolumeReport report = visitor.getReport();

        assertNotNull(report);
        assertNotNull(report.levels);
        assertEquals(1, report.levels.size());

        VolumeReportVisitor.LevelReport levelReport = report.levels.get(0);
        assertEquals("l1", levelReport.levelId);
        assertEquals("Level 1", levelReport.levelName);
        assertEquals(240.0, levelReport.totalVolume, 0.001);
    }

    // ==================== LuminosityReportVisitor Tests ====================

    @Test
    void testLuminosityReportVisitorBuilding() {
        LuminosityReportVisitor visitor = new LuminosityReportVisitor();

        building.accept(visitor);
        LuminosityReportVisitor.LuminosityReport report = visitor.getReport();

        assertNotNull(report);
        assertEquals("b1", report.buildingId);
        assertEquals("Test Building", report.buildingName);
        // room1: 500/50 = 10, room2: 300/30 = 10, average = 10
        assertEquals(10.0, report.averageLuminosity, 0.001);
    }

    @Test
    void testLuminosityReportVisitorLevel() {
        LuminosityReportVisitor visitor = new LuminosityReportVisitor();

        building.accept(visitor);
        LuminosityReportVisitor.LuminosityReport report = visitor.getReport();

        assertNotNull(report);
        assertNotNull(report.levels);
        assertEquals(1, report.levels.size());

        LuminosityReportVisitor.LevelReport levelReport = report.levels.get(0);
        assertEquals("l1", levelReport.levelId);
        assertEquals("Level 1", levelReport.levelName);
        assertEquals(10.0, levelReport.averageLuminosity, 0.001);
    }

    // ==================== Building.accept() Mock Test ====================

    @Test
    void testBuildingAcceptCallsVisitor() {
        building.accept(mockVisitor);

        verify(mockVisitor, times(1)).visit(building);
        verify(mockVisitor, times(1)).visit(level);
        verify(mockVisitor, times(1)).visit(room1);
        verify(mockVisitor, times(1)).visit(room2);
    }
}
