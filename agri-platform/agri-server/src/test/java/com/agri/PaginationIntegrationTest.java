package com.agri;

import com.agri.entity.News;
import com.agri.entity.Product;
import com.agri.entity.SupplyDemand;
import com.agri.service.MarketService;
import com.agri.service.ProductService;
import com.agri.service.SupplyDemandService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PaginationIntegrationTest {

    @Autowired
    private MarketService marketService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplyDemandService supplyDemandService;

    @Test
    void newsPaginationHonorsRequestedPageSize() {
        Page<News> page = marketService.getNews(1, 2, null, "time");

        assertEquals(2, page.getSize());
        assertEquals(2, page.getRecords().size());
        assertTrue(page.getTotal() >= 2);
    }

    @Test
    void productPaginationHonorsRequestedPageSize() {
        Page<Product> page = productService.getProductPage(1, 2, null, null);

        assertEquals(2, page.getSize());
        assertEquals(2, page.getRecords().size());
        assertTrue(page.getTotal() >= 2);
    }

    @Test
    void supplyDemandPaginationHonorsRequestedPageSize() {
        Page<SupplyDemand> page = supplyDemandService.getPage(1, 2, "supply", null);

        assertEquals(2, page.getSize());
        assertEquals(2, page.getRecords().size());
        assertTrue(page.getTotal() >= 2);
    }
}
