package us.ppgs.cam;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class CamWebSocketConfig implements WebSocketConfigurer {

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new CamWebSocket(), "/camws").setAllowedOrigins("*");
		registry.addHandler(new CamWebSocketLive(), "/camlivews").setAllowedOrigins("*");
	}
}