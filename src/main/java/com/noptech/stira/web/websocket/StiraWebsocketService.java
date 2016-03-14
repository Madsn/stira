package com.noptech.stira.web.websocket;

import com.noptech.stira.security.AuthoritiesConstants;
import com.noptech.stira.security.SecurityUtils;
import com.noptech.stira.web.websocket.dto.ActivityDTO;
import com.noptech.stira.web.websocket.dto.UpdatePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.inject.Inject;
import java.security.Principal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import static com.noptech.stira.config.WebsocketConfiguration.IP_ADDRESS;

@Controller
@Secured(AuthoritiesConstants.USER)
public class StiraWebsocketService {

    private static final Logger log = LoggerFactory.getLogger(StiraWebsocketService.class);

    @SendTo("/topic/stira")
    public UpdatePayload triggerUpdate() {
        return new UpdatePayload();
    }

}
