package com.agri;

import com.agri.entity.Product;
import com.agri.entity.SupplyDemand;
import com.agri.service.ProductService;
import com.agri.service.SupplyDemandService;
import com.agri.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OwnershipControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplyDemandService supplyDemandService;

    @Test
    void ownerCanManageOwnProductViaController() throws Exception {
        Product product = new Product();
        product.setUserId(2L);
        product.setName("控制器测试产品");
        product.setCategory("蔬菜");
        product.setPrice(BigDecimal.valueOf(2.34));
        product.setUnit("斤");
        product.setStock(8);
        product.setDescription("测试");
        product.setContactName("测试");
        product.setContactPhone("13800138001");
        product.setContactAddress("测试地址");
        product.setStatus(1);
        productService.publishProduct(product);

        String token = jwtUtil.generateToken(2L, "farmer1", "farmer");

        mockMvc.perform(put("/api/product/{id}", product.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(delete("/api/product/{id}", product.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void ownerCanManageOwnSupplyDemandViaController() throws Exception {
        SupplyDemand item = new SupplyDemand();
        item.setUserId(2L);
        item.setType("supply");
        item.setTitle("控制器测试供需");
        item.setCategory("蔬菜");
        item.setQuantity("10斤");
        item.setPrice("面议");
        item.setContent("测试");
        item.setContactName("测试");
        item.setContactPhone("13800138001");
        item.setContactAddress("测试地址");
        item.setStatus(1);
        supplyDemandService.publish(item);

        String token = jwtUtil.generateToken(2L, "farmer1", "farmer");

        mockMvc.perform(put("/api/supply-demand/{id}", item.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(delete("/api/supply-demand/{id}", item.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
