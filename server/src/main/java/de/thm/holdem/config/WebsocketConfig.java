package de.thm.holdem.config;

import de.thm.holdem.security.JwsAuthenticationToken;
import de.thm.holdem.service.ConnectionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.socket.config.annotation.*;

import java.util.Optional;


@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    private final CorsProperties corsProperties;

    private final ConnectionRegistry registry;



    @Qualifier("websocket")
    private final AuthenticationManager authenticationManager;

    WebsocketConfig(CorsProperties corsProperties, AuthenticationManager authenticationManager,
                    ConnectionRegistry registry) {
        this.corsProperties = corsProperties;
        this.authenticationManager = authenticationManager;
        this.registry = registry;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/topic/", "/queue/");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(corsProperties.getAllowedOrigins().toArray(new String[0]))
                .withSockJS();
    }


    /**
     * Intercepts an incoming connect request and authenticates the user.
     *
     * <p>adds the user to the connection registry
     *
     * @param registration the channel registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    Optional.ofNullable(accessor.getNativeHeader("Authorization")).ifPresent(ah -> {
                        String bearerToken = ah.get(0).replace("Bearer ", "");
                        JwsAuthenticationToken token = (JwsAuthenticationToken) authenticationManager
                                .authenticate(new JwsAuthenticationToken(bearerToken));
                       accessor.setUser(token);

                       // add user to connection registry
                        String userId = token.getName();
                        String sessionId = accessor.getSessionId();
                        registry.connect(userId, sessionId);
                    });
                }
                return message;
            }
        });
    }



}
