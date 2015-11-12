package com.github.mtritschler.bencoding;

import java.nio.charset.Charset;
import java.util.*;

public class ObjectMapper {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public Object read(byte[] input) {
        return read(input, 0).getData();
    }

    public Object read(String input) {
        return read(input.getBytes(DEFAULT_CHARSET));
    }

    private Result<?> read(byte[] bytes, int start) {
        switch (bytes[start]) {
            case 'd':
                return decodeDictionary(bytes, start + 1);
            case 'l':
                return decodeList(bytes, start + 1);
            case 'i':
                return decodeInteger(bytes, start + 1);
            default:
                return decodeString(bytes, start);
        }
    }

    private Result<Map<String, ?>> decodeDictionary(byte[] bytes, int start) {
        Map<String, Object> result = new HashMap<>();
        int pos = start;
        while (bytes[pos] != 'e') {
            Result<?> k = read(bytes, pos);
            Result<?> v = read(bytes, k.getLast() + 1);
            result.put(new String((byte[]) k.getData()), v.getData());
            pos = v.getLast() + 1;
        }
        return new Result<>(result, pos);
    }

    private Result<List<?>> decodeList(byte[] data, int start) {
        List<Object> result = new ArrayList<>();
        int pos = start;
        while (data[pos] != 'e') {
            Result next = read(data, pos);
            result.add(next.data);
            pos = next.getLast() + 1;
        }
        return new Result<>(result, pos);
    }

    private Result<Integer> decodeInteger(byte[] bytes, int pos) {
        int i = find('e', bytes, pos);
        int integer = Integer.parseInt(new String(Arrays.copyOfRange(bytes, pos, i)), 10);
        return new Result<>(integer, i);
    }

    private Result<byte[]> decodeString(byte[] bytes, int pos) {
        int separator = find(':', bytes, pos);
        int length = Integer.parseInt(new String(Arrays.copyOfRange(bytes, pos, separator)), 10);
        int dataStart = separator + 1;
        int dataEnd = separator + length;
        byte[] byteString = Arrays.copyOfRange(bytes, dataStart, dataEnd + 1);
        return new Result<>(byteString, dataEnd);
    }

    private int find(char c, byte[] data, int start) {
        int i = start;
        while (data[i] != c) {
            i++;
        }
        return i;
    }

    private static class Result<T> {
        private final T data;
        private final int last;

        private Result(T data, int last) {
            this.data = data;
            this.last = last;
        }

        public T getData() {
            return data;
        }

        public int getLast() {
            return last;
        }
    }

}
