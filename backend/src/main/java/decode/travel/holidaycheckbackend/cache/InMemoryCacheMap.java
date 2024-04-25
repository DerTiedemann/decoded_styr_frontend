package decode.travel.holidaycheckbackend.cache;


import org.springframework.lang.NonNull;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InMemoryCacheMap<T, U> implements Map<T, U> {
    private final Map<T, ValueWrapper<U>> internalMap;
    private final int maxSize;
    private final Duration maxAge;
    private final int cleanupPercentage;

    public InMemoryCacheMap(int maxSize, @NonNull Duration maxAge, int cleanupPercentage) {
        if (maxSize < 1) throw new IllegalArgumentException("Max size must be greater than 0");
        if (cleanupPercentage < 1) throw new IllegalArgumentException("Cleanup percentage must be greater than 0");
        this.maxSize = maxSize;
        this.maxAge = Objects.requireNonNull(maxAge);
        this.cleanupPercentage = cleanupPercentage;
        this.internalMap = new HashMap<>(maxSize);
    }

    @Override
    public int size() {
        return internalMap.size();
    }

    @Override
    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return internalMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) return false;
        return this.internalMap.values().stream().map(ValueWrapper::value).anyMatch(v -> v.equals(value));
    }

    @Override
    public U get(Object key) {
        var wrapper = this.internalMap.get(key);
        return wrapper == null ? null : wrapper.value();
    }

    @Override
    public U put(T key, U value) {
        return Optional.ofNullable(this.internalMap.put(key, new ValueWrapper<>(value)))
                .map(ValueWrapper::value)
                .orElse(null);
    }

    @Override
    public U remove(Object key) {
        return Optional.ofNullable(this.internalMap.remove(key)).map(ValueWrapper::value).orElse(null);
    }

    @Override
    public void putAll(Map<? extends T, ? extends U> m) {
        var map = m.keySet()
                .stream()
                .collect(Collectors.toMap(Function.identity(), k -> new ValueWrapper<U>(m.get(k))));
        this.internalMap.putAll(map);
    }

    @Override
    public void clear() {
        this.internalMap.clear();
    }


    @Override
    public Set<T> keySet() {
        return this.internalMap.keySet();
    }


    @Override
    public Collection<U> values() {
        return this.internalMap.values().stream().map(ValueWrapper::value).toList();
    }


    @Override
    public Set<Entry<T, U>> entrySet() {
        return this.internalMap.entrySet()
                .stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().value()))
                .collect(
                        Collectors.toSet());
    }

    public int cleanup() {
        // remove based on age
        var now = Instant.now();
        var keysToRemove = this.internalMap.entrySet()
                .stream()
                .filter(e -> Duration.between(e.getValue().timestamp(), now).compareTo(maxAge) > 0)
                .map(
                        Entry::getKey)
                .toList();
        keysToRemove.forEach(this.internalMap::remove);
        int numberOfRemovedKeys = keysToRemove.size();

        // if the map is still
        if (size() > maxSize) {
            // delete cleanupPercentage percent
            var limit = (int)(size() - maxSize + maxSize * (cleanupPercentage / 100.));
            keysToRemove = this.internalMap.entrySet()
                    .stream()
                    .sorted(Comparator.comparing(e -> e.getValue().timestamp()))
                    .limit(limit)
                    .map(
                            Entry::getKey)
                    .toList();
            keysToRemove.forEach(this.internalMap::remove);
            numberOfRemovedKeys += keysToRemove.size();
        }
        return numberOfRemovedKeys;
    }


    record ValueWrapper<T>(T value, Instant timestamp) {
        public ValueWrapper(T value) {
            this(value, Instant.now());
        }
    }
}
