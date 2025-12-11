package pl.put.poznan.transformer.logic;



import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;


/**
 * This is just an example to show that the logic should be outside the REST service.
 */

public class TextTransformer {

    private final String[] transforms;

    public TextTransformer(String[] transforms) {
        this.transforms = transforms;
    }

    public void transform(File file) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            BuildingClasses wrapper = mapper.readValue(file, BuildingClasses.class);

            if (wrapper.building != null) {
                BuildingClasses.Building building = wrapper.building;
                System.out.println("Budynek: " + building.name + " (id=" + building.id + ")");

                if (building.levels != null) {
                    for (BuildingClasses.Level level : building.levels) {
                        System.out.println("  Poziom: " + level.name + " (id=" + level.id + ")");
                        if (level.rooms != null) {
                            for (BuildingClasses.Room room : level.rooms) {
                                System.out.println("    Pomieszczenie: " + room.name +
                                        " (id=" + room.id +
                                        ", area=" + room.area +
                                        ", cube=" + room.cube +
                                        ", heating=" + room.heating +
                                        ", light=" + room.light + ")");
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


