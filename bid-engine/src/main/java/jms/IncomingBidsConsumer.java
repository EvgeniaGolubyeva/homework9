package jms;

import entity.Bid;
import entity.BidConfirmation;
import service.BidService;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * @author Evgenia
 */

@MessageDriven(name = "BidMessageConsumer", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/incomingBids"),
        @ActivationConfigProperty(propertyName = "destinationType"  , propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode"  , propertyValue = "Auto-acknowledge")
})

public class IncomingBidsConsumer implements MessageListener {
    private BidService bidService;
    private BidConfirmationsProducer bidConfirmationsProducer;

    @Resource
    private MessageDrivenContext ctx;


    @Override
    public void onMessage(Message message) {
        try {
            String webSocketSessionId = (String) message.getObjectProperty("webSocketSessionId");

            ObjectMessage objectMessage = (ObjectMessage) message;
            Bid bid = (Bid) objectMessage.getObject();

            BidConfirmation result = bidService.placeBid(bid);

            bidConfirmationsProducer.placeConfirmationToQueue(webSocketSessionId, result);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Inject
    public void setBidService(BidService bidService) {
        this.bidService = bidService;
    }

    @Inject
    public void setBidConfirmationsProducer(BidConfirmationsProducer bidConfirmationsProducer) {
        this.bidConfirmationsProducer = bidConfirmationsProducer;
    }
}
