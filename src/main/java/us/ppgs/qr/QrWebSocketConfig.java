package us.ppgs.qr;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class QrWebSocketConfig implements WebSocketConfigurer {

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new QrsWebSocket(), "/qrsws").setAllowedOrigins("*");
		registry.addHandler(new QrWebSocket(), "/qrrws").setAllowedOrigins("*");
	}
}