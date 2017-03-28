package polling.configuration.security;

import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * Created by Toncho_Petrov on 1/13/2017.
 */
//@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpDestMatchers("/gs-guide-websocket/**").hasAnyAuthority("ROLE_USER", "ROLE_FACILITY_OWNER", "ROLE_ADMIN");
    }

}