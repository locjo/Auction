package com.auction.system.modules.bid.event;

public record AuctionSocketMessage(String type, AuctionUpdatePayload payload) {
}
