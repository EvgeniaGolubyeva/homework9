package jms;

import entity.BidConfirmation;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;

/**
 * @author Evgenia
 */

@ApplicationScoped
public class BidConfirmationsProducer {
    private static final String USERNAME = "auctionUser";
    private static final String PASSWORD = "auction2014!";

    private Queue bidConfirmationsQueue;
    private ConnectionFactory connectionFactory;

    public void placeConfirmationToQueue(String webSocketSessionId, BidConfirmation result) {
        try (JMSContext context = connectionFactory.createContext(USERNAME, PASSWORD)) {
            context.createProducer().setProperty("webSocketSessionId", webSocketSessionId).send(bidConfirmationsQueue, result);
        }
    }

    @Resource(lookup = "queue/bidConfirmations")
    public void setBidConfirmationsQueue(Queue bidConfirmationsQueue) {
        this.bidConfirmationsQueue = bidConfirmationsQueue;
    }

    @Resource(lookup ="java:/ConnectionFactory")
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
