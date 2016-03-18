package com.noptech.stira.web.websocket;

import com.noptech.stira.security.AuthoritiesConstants;
import com.noptech.stira.web.websocket.dto.UpdatePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

@Controller
@Secured(AuthoritiesConstants.USER)
public class StiraWebsocketService {

    private static final Logger log = LoggerFactory.getLogger(StiraWebsocketService.class);

    @SendTo("/topic/stira")
    public UpdatePayload triggerUpdate() {
        return new UpdatePayload();
    }

}
