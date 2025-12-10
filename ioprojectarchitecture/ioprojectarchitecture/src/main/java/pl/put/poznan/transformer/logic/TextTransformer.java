package pl.put.poznan.transformer.logic;

/**
 * This is just an example to show that the logic should be outside the REST service.
 */

public class TextTransformer {

    private final String[] transforms;

    public TextTransformer(String[] transforms){
        this.transforms = transforms;
    }

    public String transform(String text){
        Reader reader = new JsonReader();
        Reader loggingReader = new LoggingJsonReader(reader);

        try {
            Person person = loggingReader.read(text, Person.class);
            System.out.println(person);
            return person.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    // excample class
    public static class Person {
        public String name;
        public int age;

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }
}
