package com.swiftchat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer; //message broken is an internal system which routes messages to all the subscribed users
import org.springframework.web.socket.config.annotation.StompEndpointRegistry; //implementing STOMP protocols

@Configuration //annotation saying hey spring i will customize the ws
@EnableWebSocketMessageBroker //Hey spring switch on the ws connection
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // enables message broker (for sending messages to clients)
        config.enableSimpleBroker("/topic"); // response back to all the SUBSCRIBED users

        // prefix for messages coming from client
        config.setApplicationDestinationPrefixes("/app"); // /app/sendMessage (client -> server)
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // endpoint for websocket connection
        registry.addEndpoint("/chat")
              .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}

/* SIMPLE FLOW :-
When a user tries to send a message, first the WebSocket connection is established.
Then the user sends the message through that WebSocket connection.
The message hits the WebSocket controller (@MessageMapping), which passes the data to the service.
The service performs the logic (like checking login and storing the message in Redis).
After that, the message is sent to the receiver in real-time using WebSocket. */