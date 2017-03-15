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
import box.utils.BoxStatsContainer;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Service responsible for WebSocket communication.
 *
 * @author emil
 */
@Controller
public class GreenHouseServiceWS implements ApplicationListener<SessionDisconnectEvent> {

    private static final Logger log = LoggerFactory.getLogger(GreenHouseServiceWS.class);

    /**
     * Sends container with box statistics to all subscribers to
     * /topic/humAndTemp
     *
     * @param container
     * @return container with Box statistics.
     */
    @SendTo("/topic/tempAndHum")
    public BoxStatsContainer sendTempAndHum(BoxStatsContainer container) {
        log.debug("Sending hum and temp");
        return container;
    }

    /**
     * Sends error message to all subscribers to /topic/exception
     *
     * @param errorMessage
     * @return error message form exception.
     */
    @SendTo("/topic/exception")
    public String reportError(String errorMessage) {
        log.debug("ERROR: " + errorMessage);
        return errorMessage;
    }

    /**
     * Method invoked when session ends.
     *
     * @param e
     */
    @Override
    public void onApplicationEvent(SessionDisconnectEvent e) {
        log.error("Session closed: " + e.getSessionId());
    }

}
