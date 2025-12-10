package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonReader implements Reader {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T read(String input, Class<T> clazz) throws Exception {
        return mapper.readValue(input, clazz);
    }

    public String write(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }
}