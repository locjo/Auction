package com.auction.system.modules.bid.repository;

import com.auction.system.modules.bid.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {}
