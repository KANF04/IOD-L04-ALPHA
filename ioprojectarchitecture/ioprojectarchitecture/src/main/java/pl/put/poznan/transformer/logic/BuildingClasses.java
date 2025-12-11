package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

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
}
