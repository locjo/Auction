package com.auction.system.modules.bid.service;

import com.auction.system.exception.ErrorCode;
import com.auction.system.common.ProductStatus;

import com.auction.system.exception.AppException;

import com.auction.system.modules.bid.dto.request.PlaceBidRequest;
import com.auction.system.modules.bid.dto.response.BidResponse;
import com.auction.system.modules.bid.event.AuctionUpdatePayload;
import com.auction.system.modules.bid.event.BidPlacedEvent;
import com.auction.system.modules.bid.entity.Bid;
import com.auction.system.modules.bid.repository.BidRepository;
import com.auction.system.modules.product.entity.Product;
import com.auction.system.modules.product.repository.ProductRepository;
import com.auction.system.modules.user.entity.User;
import com.auction.system.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class BidService {
    private final BidRepository bids;
    private final ProductRepository products;
    private final UserRepository users;
    private final ApplicationEventPublisher events;

    @Autowired
    public BidService(BidRepository bids, ProductRepository products, UserRepository users,
                      ApplicationEventPublisher events) {
        this.bids = bids;
        this.products = products;
        this.users = users;
        this.events = events;
    }

    public BidService(BidRepository bids, ProductRepository products, UserRepository users) {
        this(bids, products, users, event -> { });
    }

    @Transactional
    public BidResponse placeBid(Long productId, String bidderEmail, PlaceBidRequest request) {
        Product product = products.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy sản phẩm"));
        if (product.getStatus() != ProductStatus.ACTIVE
                || !Instant.now().isBefore(product.getEndTime())
                || Instant.now().isBefore(product.getStartTime())) {
            throw new AppException(ErrorCode.CONFLICT, "Sản phẩm hiện không hoạt động");
        }

        Instant now = Instant.now();
        if (now.isBefore(product.getStartTime()) || now.isAfter(product.getEndTime())) {
            throw new AppException(ErrorCode.CONFLICT, "Sản phẩm chưa bắt đầu hoặc đã kết thúc thời gian đấu giá");
        }

        User bidder = users.findByEmailIgnoreCase(bidderEmail)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy người đặt giá"));
        if (product.getSeller().getId().equals(bidder.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN, "Người bán không được đặt giá cho sản phẩm của mình");
        }
        if (request.bidAmount().compareTo(product.getCurrentPrice()) <= 0) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Giá đặt phải cao hơn giá hiện tại của sản phẩm");
        }

        product.updateCurrentPrice(request.bidAmount());
        Bid bid = bids.save(new Bid(product, bidder, request.bidAmount()));
        BidResponse response = BidResponse.from(bid);
        events.publishEvent(new BidPlacedEvent(new AuctionUpdatePayload(
                product.getId(), product.getCurrentPrice(), bidder.getFullName(),
                request.bidAmount(), product.getEndTime(), response.createdAt())));
        return response;
    }
}
