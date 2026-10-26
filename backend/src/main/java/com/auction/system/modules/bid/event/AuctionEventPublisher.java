package com.auction.system.modules.bid.event;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AuctionEventPublisher {
    private final SimpMessagingTemplate messagingTemplate;

    public AuctionEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishBidPlaced(BidPlacedEvent event) {
        Long productId = event.payload().productId();
        messagingTemplate.convertAndSend(
                "/topic/products/" + productId + "/bids",
                new AuctionSocketMessage("BID_PLACED", event.payload()));
    }
}
