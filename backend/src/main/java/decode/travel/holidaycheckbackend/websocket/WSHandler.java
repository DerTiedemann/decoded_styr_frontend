package decode.travel.holidaycheckbackend.websocket;

import com.google.gson.Gson;
import decode.travel.holidaycheckbackend.service.CodeService;
import decode.travel.holidaycheckbackend.model.Code;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Slf4j
public class WSHandler extends TextWebSocketHandler {

    private final CodeService codeService;
    Gson gson = new Gson();
    private final int repeat;
    public WSHandler(CodeService _codeService, int _repeat) {
        this.codeService = _codeService;
        repeat = _repeat;
    }


    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        session.sendMessage(new TextMessage("WS with ID " + session.getId() + " created"));
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException, InterruptedException, ExecutionException {
        while (true){
            log.info(session.getId());
            Code code = codeService.addCode(session.getId());
            session.sendMessage(new TextMessage(gson.toJson(code)));
            Thread.sleep(repeat*1000);
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Perform actions when a WebSocket connection is closed
    }
}
