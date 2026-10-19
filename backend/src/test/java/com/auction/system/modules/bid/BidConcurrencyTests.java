package com.auction.system.modules.bid;

import com.auction.system.AuctionApplication;
import com.auction.system.modules.bid.dto.request.PlaceBidRequest;
import com.auction.system.modules.bid.repository.BidRepository;
import com.auction.system.modules.bid.service.BidService;
import com.auction.system.modules.category.entity.Category;
import com.auction.system.modules.category.repository.CategoryRepository;
import com.auction.system.modules.product.entity.Product;
import com.auction.system.modules.product.repository.ProductRepository;
import com.auction.system.modules.user.entity.User;
import com.auction.system.modules.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = AuctionApplication.class, properties = {
        "spring.datasource.url=jdbc:h2:mem:bid-concurrency;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class BidConcurrencyTests {
    @Autowired private BidRepository bids;
    @Autowired private ProductRepository products;
    @Autowired private CategoryRepository categories;
    @Autowired private UserRepository users;
    @Autowired private PlatformTransactionManager transactionManager;

    private Product product;
    private User bidderOne;
    private User bidderTwo;

    @BeforeEach
    void setUp() {
        bids.deleteAll();
        products.deleteAll();
        categories.deleteAll();
        users.deleteAll();

        User seller = users.saveAndFlush(new User("seller@example.test", "hash", "Người bán", null));
        bidderOne = users.saveAndFlush(new User("bidder1@example.test", "hash", "Người mua 1", null));
        bidderTwo = users.saveAndFlush(new User("bidder2@example.test", "hash", "Người mua 2", null));
        Category category = categories.saveAndFlush(new Category(null, "Danh mục kiểm thử"));
        product = new Product(seller, category, "Sản phẩm kiểm thử", null,
                new BigDecimal("100.00"), Instant.now().minusSeconds(60), Instant.now().plusSeconds(3600));
        product.activate();
        product = products.saveAndFlush(product);
    }

    @AfterEach
    void cleanUp() {
        bids.deleteAll();
        products.deleteAll();
        categories.deleteAll();
        users.deleteAll();
    }

    @Test
    void concurrentBidsOnSameProductAllowOnlyOneTransactionToCommit() throws Exception {
        CountDownLatch bothTransactionsReadProduct = new CountDownLatch(2);
        CountDownLatch releaseTransactions = new CountDownLatch(1);
        ProductRepository synchronizedProductRepository = repositoryWithReadBarrier(
                bothTransactionsReadProduct, releaseTransactions);
        BidService bidService = new BidService(bids, synchronizedProductRepository, users);
        TransactionTemplate transactions = new TransactionTemplate(transactionManager);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            List<Future<Throwable>> results = new ArrayList<>();
            results.add(submitBid(executor, transactions, bidService, bidderOne));
            results.add(submitBid(executor, transactions, bidService, bidderTwo));

            assertTrue(bothTransactionsReadProduct.await(10, TimeUnit.SECONDS),
                    "Hai transaction không cùng đọc được sản phẩm trong thời hạn kiểm thử");
            releaseTransactions.countDown();

            List<Throwable> failures = java.util.Arrays.asList(results.get(0).get(), results.get(1).get());
            long successCount = failures.stream().filter(failure -> failure == null).count();
            long optimisticConflictCount = failures.stream().filter(this::isOptimisticLockFailure).count();

            assertEquals(1, successCount, "Chỉ một lượt đặt giá được commit");
            assertEquals(1, optimisticConflictCount,
                    "Lượt đặt giá còn lại phải gặp optimistic locking. Lỗi nhận được: " + failures);
            assertEquals(1, bids.count(), "Transaction xung đột phải rollback cả bản ghi bid");
            assertEquals(new BigDecimal("200.00"), products.findById(product.getId()).orElseThrow().getCurrentPrice());
        } finally {
            releaseTransactions.countDown();
            executor.shutdownNow();
        }
    }

    private Future<Throwable> submitBid(ExecutorService executor, TransactionTemplate transactions,
                                        BidService bidService, User bidder) {
        return executor.submit(() -> {
            try {
                transactions.execute(status -> bidService.placeBid(product.getId(), bidder.getEmail(),
                        new PlaceBidRequest(new BigDecimal("200.00"))));
                return null;
            } catch (Throwable failure) {
                return failure;
            }
        });
    }

    private ProductRepository repositoryWithReadBarrier(CountDownLatch bothRead, CountDownLatch release) {
        return (ProductRepository) Proxy.newProxyInstance(ProductRepository.class.getClassLoader(),
                new Class<?>[]{ProductRepository.class}, (proxy, method, args) -> {
                    try {
                        Object result = method.invoke(products, args);
                        if (method.getName().equals("findById") && args != null
                                && args.length == 1 && product.getId().equals(args[0])) {
                            bothRead.countDown();
                            if (!release.await(10, TimeUnit.SECONDS)) {
                                throw new IllegalStateException("Hết thời gian chờ đồng bộ transaction");
                            }
                        }
                        return result;
                    } catch (InvocationTargetException exception) {
                        throw exception.getCause();
                    }
                });
    }

    private boolean isOptimisticLockFailure(Throwable failure) {
        if (failure == null) return false;
        Throwable cause = failure;
        while (cause != null) {
            if (cause instanceof ObjectOptimisticLockingFailureException
                    || cause instanceof OptimisticLockingFailureException) return true;
            cause = cause.getCause();
        }
        return false;
    }
}
