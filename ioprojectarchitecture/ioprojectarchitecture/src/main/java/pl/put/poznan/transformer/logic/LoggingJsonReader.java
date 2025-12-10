package pl.put.poznan.transformer.logic;

public class LoggingJsonReader implements Reader {
    private Reader wrapped;

    public LoggingJsonReader(Reader wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public <T> T read(String input, Class<T> clazz) throws Exception {
        System.out.println("Reading JSON: " + input);
        T obj = wrapped.read(input, clazz);
        System.out.println("Parsed object: " + obj);
        return obj;
    }
}
