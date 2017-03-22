package box.web.websocket;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import box.web.websocket.dto.BoxStatsContainer;
import org.springframework.messaging.handler.annotation.SendTo;


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
    @SendTo("/topic/exceptions")
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
