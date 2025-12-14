package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Optional;

/**
 * Data classes for building structure
 * Used with Decorator Pattern for JSON serialization/deserialization
 */
public class BuildingClasses {

    public Building building;

    public static class Building {
        public String id;
        public String name;
        public List<Level> levels;
    }

    public static class Level {
        public String id;
        public String name;
        public List<Room> rooms;
    }

    public static class Room {
        public String id;
        public String name;
        public double area;
        public double cube;
        public double heating;
        public double light;
    }

    // --- CALCULATION AND FINDER METHODS ---

    public double calculateVolume(Building building) {
        if (building == null || building.levels == null) {
            return 0.0;
        }
        return building.levels.stream().mapToDouble(this::calculateVolume).sum();
    }

    public double calculateVolume(Level level) {
        if (level == null || level.rooms == null) {
            return 0.0;
        }
        return level.rooms.stream().mapToDouble(this::calculateVolume).sum();
    }

    public double calculateVolume(Room room) {
        if (room == null) {
            return 0.0;
        }
        return room.cube;
    }

    public Optional<Level> findLevelById(String levelId) {
        if (this.building == null || this.building.levels == null || levelId == null) {
            return Optional.empty();
        }
        return this.building.levels.stream()
                .filter(level -> levelId.equals(level.id))
                .findFirst();
    }

    public Optional<Room> findRoomById(String levelId, String roomId) {
        if (roomId == null) {
            return Optional.empty();
        }
        return findLevelById(levelId)
                .flatMap(level -> {
                    if (level.rooms == null) {
                        return Optional.empty();
                    }
                    return level.rooms.stream()
                            .filter(room -> roomId.equals(room.id))
                            .findFirst();
                });
    }
}
