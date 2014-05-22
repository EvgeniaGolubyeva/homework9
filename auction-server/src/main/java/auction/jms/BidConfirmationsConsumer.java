package auction.jms;

import auction.websockets.WebSocketSessionHolder;
import entity.BidConfirmation;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;

/**
 * @author Evgenia
 */

@MessageDriven(name = "BidMessageConsumer", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/bidConfirmations"),
        @ActivationConfigProperty(propertyName = "destinationType"  , propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode"  , propertyValue = "Auto-acknowledge")
})
public class BidConfirmationsConsumer implements MessageListener {
    private WebSocketSessionHolder webSocketSessionHolder;

    @Override
    public void onMessage(Message message) {
        try {
            String webSocketSessionId = (String) message.getObjectProperty("webSocketSessionId");
            Session session = webSocketSessionHolder.get(webSocketSessionId);

            ObjectMessage objectMessage = (ObjectMessage) message;
            BidConfirmation result = (BidConfirmation) objectMessage.getObject();

            //if amount < min or amount > reserved then notify one client (that placed request) only.
            if (BidConfirmation.RESULT_STATUS_ADDED.equals(result.getStatus())) {
                session.getOpenSessions().forEach(s -> sendText(s, result));
            } else {
                sendText(session, result);
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void sendText(Session session, BidConfirmation result) {
        try {
            if (session.isOpen())
                session.getBasicRemote().sendObject(result);
        } catch (EncodeException | IOException e) {
            e.printStackTrace();
        }
    }

    @Inject
    public void setWebSocketSessionHolder(WebSocketSessionHolder webSocketSessionHolder) {
        this.webSocketSessionHolder = webSocketSessionHolder;
    }
}

