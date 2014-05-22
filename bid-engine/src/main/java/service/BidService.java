package service;

import entity.Bid;
import entity.BidConfirmation;
import dao.BidDAO;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * @author Evgenia
 */

@Singleton
public class BidService {
    private BidDAO bidDAO;
    private INotificationService notificationService;

    public BidConfirmation placeBid(Bid bid) {
        PlaceBidResultBuilder placeBidResultBuilder = new PlaceBidResultBuilder();

        BigDecimal min = bid.getProduct().getMinimalPrice();
        BigDecimal res = bid.getProduct().getReservedPrice();

        if (bid.getAmount().compareTo(min) < 0) {
            notificationService.sendSorryNotification(bid);
            return placeBidResultBuilder.rejected(min);
        }

        if (bid.getAmount().compareTo(res) >= 0) {
            notificationService.sendWinNotification(bid);
            return placeBidResultBuilder.won(res);
        }

        bidDAO.create(bid);

        List<Bid> productBids = bidDAO.getBidsByProduct(bid.getProduct().getId());
        notificationService.sendBidWasPlacedNotification(bid, productBids);
        notifyOverbidUsers(bid, productBids);

        Bid topBid = productBids.stream().max(Comparator.comparing(Bid::getAmount)).get();

        return placeBidResultBuilder.added(topBid, productBids.size());
    }

    @Inject
    public void setBidDAO(BidDAO bidDAO) {
        this.bidDAO = bidDAO;
    }

    @Inject
    public void setNotificationService(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    private void notifyOverbidUsers(Bid bid, List<Bid> productBids) {
        productBids.stream()
                .filter(b -> bid.getAmount().compareTo(b.getAmount()) > 0)
                .filter(b -> b.getUser().isGetOverbidNotifications())
                .forEach(b -> notificationService.sendOverbidNotification(b, bid));
    }

    private class PlaceBidResultBuilder {
        public BidConfirmation rejected(BigDecimal min) {
            BidConfirmation result = new BidConfirmation();
            result.setStatus(BidConfirmation.RESULT_STATUS_REJECTED);
            result.setMessage("Sorry! Bid was rejected. Amount you placed is less then product minPrice [" + min + "]");
            return result;
        }

        public BidConfirmation won(BigDecimal res) {
            BidConfirmation result = new BidConfirmation();
            result.setStatus(BidConfirmation.RESULT_STATUS_WON);
            result.setMessage("Congratulations! You win! Amount you placed is more then product resPrice [" + res + "]");
            return result;
        }

        public BidConfirmation added(Bid topBid, int totalBidCount) {
            BidConfirmation result = new BidConfirmation();

            result.setStatus(BidConfirmation.RESULT_STATUS_ADDED);
            result.setMessage("Bid was added");

            result.setTopBidPrice(topBid.getAmount());
            result.setTopBidUser(topBid.getUser().getId());
            result.setTotalCountOfBids(totalBidCount);
            return result;
        }
    }
}
