package pl.put.poznan.transformer.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BuildingClasses calculation methods.
 * Tests volume, area, and luminosity calculations for Building, Level, and Room.
 */
class BuildingClassesTest {

    private BuildingClasses calculator;

    @BeforeEach
    void setUp() {
        calculator = new BuildingClasses();
    }

    // ==================== Volume Tests ====================

    @Test
    void testCalculateVolumeForRoom() {
        BuildingClasses.Room room = new BuildingClasses.Room();
        room.id = "r1";
        room.name = "Room 1";
        room.cube = 150.0;
        room.area = 50.0;

        double volume = calculator.calculateVolume(room);

        assertEquals(150.0, volume, 0.001);
    }

    @Test
    void testCalculateVolumeForNullRoom() {
        double volume = calculator.calculateVolume((BuildingClasses.Room) null);

        assertEquals(0.0, volume, 0.001);
    }

    @Test
    void testCalculateVolumeForLevel() {
        BuildingClasses.Room room1 = new BuildingClasses.Room();
        room1.cube = 100.0;
        room1.area = 40.0;

        BuildingClasses.Room room2 = new BuildingClasses.Room();
        room2.cube = 200.0;
        room2.area = 60.0;

        BuildingClasses.Level level = new BuildingClasses.Level();
        level.id = "l1";
        level.name = "Level 1";
        level.rooms = Arrays.asList(room1, room2);

        double volume = calculator.calculateVolume(level);

        assertEquals(300.0, volume, 0.001);
    }

    @Test
    void testCalculateVolumeForBuilding() {
        BuildingClasses.Room room1 = new BuildingClasses.Room();
        room1.cube = 100.0;

        BuildingClasses.Room room2 = new BuildingClasses.Room();
        room2.cube = 150.0;

        BuildingClasses.Level level1 = new BuildingClasses.Level();
        level1.rooms = Arrays.asList(room1, room2);

        BuildingClasses.Room room3 = new BuildingClasses.Room();
        room3.cube = 250.0;

        BuildingClasses.Level level2 = new BuildingClasses.Level();
        level2.rooms = Collections.singletonList(room3);

        BuildingClasses.Building building = new BuildingClasses.Building();
        building.id = "b1";
        building.name = "Building 1";
        building.levels = Arrays.asList(level1, level2);

        double volume = calculator.calculateVolume(building);

        assertEquals(500.0, volume, 0.001);
    }

    // ==================== Area Tests ====================

    @Test
    void testCalculateAreaForRoom() {
        BuildingClasses.Room room = new BuildingClasses.Room();
        room.id = "r1";
        room.name = "Room 1";
        room.area = 75.5;
        room.cube = 226.5;

        double area = calculator.calculateArea(room);

        assertEquals(75.5, area, 0.001);
    }

    @Test
    void testCalculateAreaForLevel() {
        BuildingClasses.Room room1 = new BuildingClasses.Room();
        room1.area = 50.0;

        BuildingClasses.Room room2 = new BuildingClasses.Room();
        room2.area = 30.0;

        BuildingClasses.Room room3 = new BuildingClasses.Room();
        room3.area = 20.0;

        BuildingClasses.Level level = new BuildingClasses.Level();
        level.id = "l1";
        level.name = "Level 1";
        level.rooms = Arrays.asList(room1, room2, room3);

        double area = calculator.calculateArea(level);

        assertEquals(100.0, area, 0.001);
    }

    @Test
    void testCalculateAreaForBuilding() {
        BuildingClasses.Room room1 = new BuildingClasses.Room();
        room1.area = 40.0;

        BuildingClasses.Room room2 = new BuildingClasses.Room();
        room2.area = 60.0;

        BuildingClasses.Level level1 = new BuildingClasses.Level();
        level1.rooms = Arrays.asList(room1, room2);

        BuildingClasses.Room room3 = new BuildingClasses.Room();
        room3.area = 100.0;

        BuildingClasses.Level level2 = new BuildingClasses.Level();
        level2.rooms = Collections.singletonList(room3);

        BuildingClasses.Building building = new BuildingClasses.Building();
        building.id = "b1";
        building.name = "Building 1";
        building.levels = Arrays.asList(level1, level2);

        double area = calculator.calculateArea(building);

        assertEquals(200.0, area, 0.001);
    }

    // ==================== Luminosity Tests ====================

    @Test
    void testCalculateLuminosityForRoom() {
        BuildingClasses.Room room = new BuildingClasses.Room();
        room.id = "r1";
        room.name = "Room 1";
        room.area = 50.0;
        room.light = 500.0;

        double luminosity = calculator.calculateLuminosity(room);

        assertEquals(10.0, luminosity, 0.001);
    }

    @Test
    void testCalculateLuminosityForBuilding() {
        BuildingClasses.Room room1 = new BuildingClasses.Room();
        room1.area = 20.0;
        room1.light = 200.0; // luminosity = 10

        BuildingClasses.Room room2 = new BuildingClasses.Room();
        room2.area = 40.0;
        room2.light = 800.0; // luminosity = 20

        BuildingClasses.Level level1 = new BuildingClasses.Level();
        level1.rooms = Arrays.asList(room1, room2);

        BuildingClasses.Room room3 = new BuildingClasses.Room();
        room3.area = 25.0;
        room3.light = 375.0; // luminosity = 15

        BuildingClasses.Level level2 = new BuildingClasses.Level();
        level2.rooms = Collections.singletonList(room3);

        BuildingClasses.Building building = new BuildingClasses.Building();
        building.id = "b1";
        building.name = "Building 1";
        building.levels = Arrays.asList(level1, level2);

        double luminosity = calculator.calculateLuminosity(building);

        // Average of (10, 20, 15) = 15
        assertEquals(15.0, luminosity, 0.001);
    }
}
