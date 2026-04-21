package com.agri.config;

import com.agri.entity.Product;
import com.agri.entity.User;
import com.agri.mapper.ProductMapper;
import com.agri.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initUsers();
        initProducts();
    }

    private void initUsers() {
        if (userMapper.selectCount(null) > 0) {
            log.info("用户数据已存在，跳过初始化");
            return;
        }

        log.info("开始初始化用户数据...");

        List<User> users = Arrays.asList(
            createUser("admin", "123456", "admin", "13800000001"),
            createUser("farmer1", "123456", "farmer", "13800000002"),
            createUser("farmer2", "123456", "farmer", "13800000003"),
            createUser("farmer3", "123456", "farmer", "13800000004"),
            createUser("buyer1", "123456", "buyer", "13800000005")
        );

        for (User user : users) {
            userMapper.insert(user);
        }

        log.info("用户数据初始化完成，共 {} 条", users.size());
    }

    private User createUser(String username, String password, String role, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        user.setPhone(phone);
        user.setStatus(1);
        return user;
    }

    private void initProducts() {
        if (productMapper.selectCount(null) > 0) {
            log.info("产品数据已存在，跳过初始化");
            return;
        }

        log.info("开始初始化产品数据...");

        List<Product> products = Arrays.asList(
            createProduct(2L, "新鲜西红柿", "蔬菜", new BigDecimal("3.50"), "斤", 500, "山东寿光大棚种植，新鲜采摘，口感酸甜", "张农户", "13800000002", "山东省寿光市"),
            createProduct(2L, "有机黄瓜", "蔬菜", new BigDecimal("4.00"), "斤", 300, "有机种植，无农药，清脆爽口", "张农户", "13800000002", "山东省寿光市"),
            createProduct(2L, "紫皮洋葱", "蔬菜", new BigDecimal("2.50"), "斤", 800, "紫皮洋葱，口感脆甜，适合炒菜", "张农户", "13800000002", "山东省寿光市"),
            createProduct(3L, "红富士苹果", "水果", new BigDecimal("5.50"), "斤", 600, "陕西红富士，脆甜多汁，产地直发", "李农户", "13800000003", "河南省郑州市"),
            createProduct(3L, "新鲜草莓", "水果", new BigDecimal("15.00"), "斤", 100, "大田草莓，香甜可口，现摘现发", "李农户", "13800000003", "河南省郑州市"),
            createProduct(3L, "黄心猕猴桃", "水果", new BigDecimal("8.00"), "斤", 200, "黄心猕猴桃，营养丰富，口感细腻", "李农户", "13800000003", "河南省郑州市"),
            createProduct(4L, "东北大米", "粮油", new BigDecimal("3.80"), "斤", 1000, "东北优质大米，颗粒饱满，口感软糯", "王农户", "13800000004", "陕西省西安市"),
            createProduct(4L, "土鸡蛋", "畜牧", new BigDecimal("1.50"), "个", 500, "农家散养土鸡蛋，营养丰富", "王农户", "13800000004", "陕西省西安市"),
            createProduct(4L, "新鲜鲤鱼", "水产品", new BigDecimal("12.00"), "斤", 150, "水库养殖鲤鱼，肉质鲜美", "王农户", "13800000004", "陕西省西安市"),
            createProduct(2L, "大白菜", "蔬菜", new BigDecimal("1.20"), "斤", 1000, "新鲜大白菜，清甜可口", "张农户", "13800000002", "山东省寿光市"),
            createProduct(3L, "新疆葡萄干", "特产", new BigDecimal("25.00"), "斤", 80, "新疆吐鲁番葡萄干，自然晾晒", "李农户", "13800000003", "河南省郑州市"),
            createProduct(4L, "有机花菜", "蔬菜", new BigDecimal("4.50"), "斤", 200, "有机花菜，口感鲜嫩", "王农户", "13800000004", "陕西省西安市")
        );

        for (Product product : products) {
            productMapper.insert(product);
        }

        log.info("产品数据初始化完成，共 {} 条", products.size());
    }

    private Product createProduct(Long userId, String name, String category, BigDecimal price, String unit, Integer stock, String description, String contactName, String contactPhone, String contactAddress) {
        Product product = new Product();
        product.setUserId(userId);
        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setUnit(unit);
        product.setStock(stock);
        product.setDescription(description);
        product.setContactName(contactName);
        product.setContactPhone(contactPhone);
        product.setContactAddress(contactAddress);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        return product;
    }
}
