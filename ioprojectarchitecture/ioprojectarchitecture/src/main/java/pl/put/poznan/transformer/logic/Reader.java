package pl.put.poznan.transformer.logic;

public interface Reader {
    <T> T read(String input, Class<T> clazz) throws Exception;
}