package box.web.websocket;

import box.security.SecurityUtils;
import box.web.websocket.dto.ActivityDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.security.Principal;
import java.util.Calendar;

import static box.config.WebsocketConfiguration.IP_ADDRESS;

/**
 *
 * @author emil
 */
public class GreenHouseServiceWS {

    private final SimpMessageSendingOperations messagingTemplate;

    public GreenHouseServiceWS(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    @SubscribeMapping("/topic/temp")
    @SendTo("/topic/tempAndHum")
    public 
}
