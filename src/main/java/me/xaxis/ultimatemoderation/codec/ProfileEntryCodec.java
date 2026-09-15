package me.xaxis.ultimatemoderation.codec;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface ProfileEntryCodec<T> {

    Map<String, Object> encode(T value);

    T decode(Map<?, ?> values);

    default List<Map<String, Object>> encodeAll(List<T> values) {
        Objects.requireNonNull(values, "Values cannot be null");

        return values.stream()
                .map(value -> encode(Objects.requireNonNull(value, "Profile entry cannot be null")))
                .toList();
    }

    default List<T> decodeAll(List<Map<?, ?>> values) {
        Objects.requireNonNull(values, "Values cannot be null");

        return values.stream()
                .map(value -> decode(Objects.requireNonNull(value, "Profile entry map cannot be null")))
                .toList();
    }
}
