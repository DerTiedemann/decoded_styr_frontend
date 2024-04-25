package decode.travel.holidaycheckbackend.service;


import decode.travel.holidaycheckbackend.cache.InMemoryCacheMap;
import decode.travel.holidaycheckbackend.model.Code;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class CodeService {
    private static final InMemoryCacheMap<String, Code> cache = new InMemoryCacheMap<>(1000, Duration.ofMinutes(5), 100);


    private void addToMap(String key, Code code){
        cache.put(key, code);
        //cache.cleanup();
    }
    private Code getFromMap(String key) throws ExecutionException {
        return cache.get(key);
    }
    public Code addCode(String sessionid) throws ExecutionException {
        String num = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
        Code code = getFromMap(sessionid);
        if( code == null ){
            code = new Code(num,sessionid);
        }
        code.setCode(num);
        code.setSession(sessionid);
        addToMap(sessionid, code);
        return code;
    }

    public boolean checkKey(String key) {
        return cache.entrySet().stream().anyMatch(c-> c.getValue().getCode().toUpperCase().equals(key.toUpperCase()));
    }
}
