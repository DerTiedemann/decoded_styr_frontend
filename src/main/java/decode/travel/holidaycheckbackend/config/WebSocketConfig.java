package decode.travel.holidaycheckbackend.config;

import decode.travel.holidaycheckbackend.service.CodeService;
import decode.travel.holidaycheckbackend.websocket.WSHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Value("${code.repeat:600}")
    private int repeat;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WSHandler(new CodeService(), repeat), "/ws").setAllowedOrigins("*");
    }
}