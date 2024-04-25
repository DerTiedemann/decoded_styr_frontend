package decode.travel.holidaycheckbackend.service;


import decode.travel.holidaycheckbackend.cache.InMemoryCacheMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class DBService {

    private static Duration dur = Duration.ofDays(30);
    private static final InMemoryCacheMap<String, Long> cache = new InMemoryCacheMap<>(1000, dur, 100);

    public void addToCache(String uuid){
        cache.put(uuid, System.currentTimeMillis());
    }

    public boolean checkKey(String key) {
        return cache.containsKey(key);
    }
}
