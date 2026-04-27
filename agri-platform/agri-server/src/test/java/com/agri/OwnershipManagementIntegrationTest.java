package com.agri;

import com.agri.entity.Product;
import com.agri.entity.SupplyDemand;
import com.agri.service.ProductService;
import com.agri.service.SupplyDemandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class OwnershipManagementIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplyDemandService supplyDemandService;

    @Test
    void ownerCanUpdateAndDeleteOwnProduct() {
        Product product = new Product();
        product.setUserId(2L);
        product.setName("测试产品");
        product.setCategory("蔬菜");
        product.setPrice(java.math.BigDecimal.valueOf(1.23));
        product.setUnit("斤");
        product.setStock(10);
        product.setDescription("测试");
        product.setContactName("测试");
        product.setContactPhone("13800138001");
        product.setContactAddress("测试地址");
        product.setStatus(1);
        assertTrue(productService.publishProduct(product));

        Product update = new Product();
        update.setId(product.getId());
        update.setStatus(0);
        assertTrue(productService.updateProduct(update, 2L));
        assertEquals(0, productService.getById(product.getId()).getStatus());
        assertTrue(productService.deleteProduct(product.getId(), 2L));
    }

    @Test
    void ownerCanUpdateAndDeleteOwnSupplyDemand() {
        SupplyDemand item = new SupplyDemand();
        item.setUserId(2L);
        item.setType("supply");
        item.setTitle("测试供需");
        item.setCategory("蔬菜");
        item.setQuantity("10斤");
        item.setPrice("面议");
        item.setContent("测试");
        item.setContactName("测试");
        item.setContactPhone("13800138001");
        item.setContactAddress("测试地址");
        item.setStatus(1);
        assertTrue(supplyDemandService.publish(item));

        SupplyDemand update = new SupplyDemand();
        update.setId(item.getId());
        update.setStatus(0);
        assertTrue(supplyDemandService.update(update, 2L));
        assertEquals(0, supplyDemandService.getById(item.getId()).getStatus());
        assertTrue(supplyDemandService.delete(item.getId(), 2L));
    }
}
