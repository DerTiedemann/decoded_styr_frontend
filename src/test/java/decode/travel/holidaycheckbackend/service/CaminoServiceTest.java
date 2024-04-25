package decode.travel.holidaycheckbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;

@Slf4j
class CaminoServiceTest {
    @Test void testNftCreation() throws Exception {
        CaminoService caminoService = new CaminoService();
        caminoService.createNFT("0x7867aa344c6e011cbc4885c223d6e16bab6d6955");
    }

}