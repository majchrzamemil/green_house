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
import box.utils.Dht11Container;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.annotation.Scheduled;

/**
 *
 * @author emil
 */
@Controller
public class GreenHouseServiceWS implements ApplicationListener<SessionDisconnectEvent> {

    private static final Logger log = LoggerFactory.getLogger(GreenHouseServiceWS.class);

    @SendTo("/topic/tempAndHum")
    public Dht11Container sendTempAndHum(Dht11Container container){
        log.debug("Sending hum and temp");
        return container;
    }
    
    @SendTo("/topic/exception")
    public String reportError(String errorMessage){
        log.debug("ERROR: " + errorMessage);
        return errorMessage;
    }

    @Override
    public void onApplicationEvent(SessionDisconnectEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
            
}
