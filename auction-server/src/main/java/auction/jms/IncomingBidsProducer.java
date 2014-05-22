package auction.jms;

import entity.Bid;

import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;

/**
 * @author Evgenia
 */

@ApplicationScoped
public class IncomingBidsProducer {
    private static final String USERNAME = "auctionUser";
    private static final String PASSWORD = "auction2014!";

    private ConnectionFactory connectionFactory;
    private Queue incomingBidsQueue;

    public void placeBidToQueue(String webSocketSessionId, Bid bid) {
        try (JMSContext context = connectionFactory.createContext(USERNAME, PASSWORD)) {
            context.createProducer().setProperty("webSocketSessionId", webSocketSessionId).send(incomingBidsQueue, bid);
        }
    }

    @Resource(lookup = "queue/incomingBids")
    public void setIncomingBidsQueue(Queue incomingBidsQueue) {
        this.incomingBidsQueue = incomingBidsQueue;
    }

    @Resource(lookup ="java:/ConnectionFactory")
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
