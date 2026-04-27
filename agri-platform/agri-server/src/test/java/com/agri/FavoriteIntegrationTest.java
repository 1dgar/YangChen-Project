package com.agri;

import com.agri.entity.Favorite;
import com.agri.service.FavoriteService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FavoriteIntegrationTest {

    @Autowired
    private FavoriteService favoriteService;

    @Test
    void favoritesIncludeNestedProductDetails() {
        Long userId = 6L;
        Long productId = 1L;

        favoriteService.remove(userId, productId);
        assertTrue(favoriteService.add(userId, productId));

        Page<Favorite> page = favoriteService.getByUserId(userId, 1, 10);
        Favorite favorite = page.getRecords().stream()
                .filter(item -> productId.equals(item.getProductId()))
                .findFirst()
                .orElseThrow();

        assertNotNull(favorite.getProduct());
        assertEquals(productId, favorite.getProduct().getId());
        assertFalse(favorite.getProduct().getName().isBlank());
        assertFalse(favorite.getProduct().getCategory().isBlank());
        assertNotNull(favorite.getProduct().getPrice());
        assertNotNull(favorite.getProduct().getStock());
        assertNotNull(favorite.getProduct().getUsername());
        assertTrue(favoriteService.remove(userId, productId));
    }

    @Test
    void favoriteCanBeAddedAgainAfterRemoval() {
        Long userId = 6L;
        Long productId = 59L;

        favoriteService.remove(userId, productId);
        assertTrue(favoriteService.add(userId, productId));
        assertTrue(favoriteService.remove(userId, productId));
        assertTrue(favoriteService.add(userId, productId));
        assertTrue(favoriteService.isFavorited(userId, productId));
        assertTrue(favoriteService.remove(userId, productId));
    }
}
