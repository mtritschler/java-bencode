package com.github.mtritschler.bencoding;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ObjectMapperTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void simpleStringShouldBeDecoded() {
        Object result = objectMapper.read("4:spam");
        assertThat(result, is("spam".getBytes(ObjectMapper.DEFAULT_CHARSET)));
    }

    @Test
    public void decodedStringShouldBeEmpty() {
        Object result = objectMapper.read("0:");
        assertThat(result, is(new byte[0]));
    }

    @Test
    public void integerShouldBeParsed() {
        Object result = objectMapper.read("i42e");
        assertThat(result, is(42));
    }

    @Test
    public void negativeIntegerShouldBeParsed() {
        Object result = objectMapper.read("i-42e");
        assertThat(result, is(-42));
    }

    @Test
    public void emptyListShouldBeParsed() {
        Object result = objectMapper.read("le");
        assertThat(result.getClass(), typeCompatibleWith(List.class));
        List<?> list = (List) result;
        assertThat(list, empty());
    }

    @Test
    public void simpleListShouldBeParsed() {
        Object result = objectMapper.read("l4:spame");
        assertThat(result.getClass(), typeCompatibleWith(List.class));
        @SuppressWarnings("unchecked")
        List<Object> list = (List) result;
        assertThat(list, hasSize(1));
    }

    @Test
    public void emptyDictShouldBeParsed() {
        Object result = objectMapper.read("de");
        assertThat(result.getClass(), typeCompatibleWith(Map.class));
        Map<?, ?> map = (Map) result;
        assertThat(map.entrySet(), empty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void complexDictShouldBeParsed() {
        Object result = objectMapper.read("d3:keyl3:one3:twoee");
        assertThat(result.getClass(), typeCompatibleWith(Map.class));
        Map<String, Object> map = (Map) result;
        assertThat(map.size(), is(1));
        assertThat(map, hasKey("key"));
        assertThat(map.get("key").getClass(), typeCompatibleWith(List.class));
        assertThat((List<Object>) map.get("key"), hasItems("one".getBytes(ObjectMapper.DEFAULT_CHARSET), "two".getBytes(ObjectMapper.DEFAULT_CHARSET)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void embeddedDictShouldBeParsed() {
        Object result = objectMapper.read("d3:keyd3:key3:oneee");
        assertThat(result.getClass(), typeCompatibleWith(Map.class));
        Map<String, Object> map = (Map) result;
        assertThat(map.size(), is(1));
        assertThat(map, hasKey("key"));
        assertThat(map.get("key").getClass(), typeCompatibleWith(Map.class));
        assertThat(((Map<String, Object>) map.get("key")), hasEntry("key", "one".getBytes(ObjectMapper.DEFAULT_CHARSET)));
    }

}
