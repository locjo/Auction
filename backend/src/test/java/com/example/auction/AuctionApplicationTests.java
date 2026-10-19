package com.example.auction;

import com.auction.system.AuctionApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AuctionApplication.class, properties = {
        "spring.datasource.url=jdbc:h2:mem:auction;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AuctionApplicationTests {

	@Test
	void contextLoads() {
	}

}
