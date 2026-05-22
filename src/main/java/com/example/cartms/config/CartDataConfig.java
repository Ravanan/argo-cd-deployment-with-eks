package com.example.cartms.config;

import com.example.cartms.model.Cart;
import com.example.cartms.model.CartItem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring configuration that seeds an in-memory cart store at startup.
 * The {@code carts()} bean is injected into {@link com.example.cartms.controller.CartController}
 * and acts as the single source of cart state for the running process.
 * Replace this with a persistent repository before any production use —
 * data here is lost on every restart.
 */
@Configuration
public class CartDataConfig {

    /**
     * Builds the seed cart map keyed by {@code cartId}. Two demo carts
     * ({@code cart-001} / {@code cart-002}) are pre-populated so the
     * HTTP endpoints return non-empty data without first calling addItem.
     */
    @Bean
    public Map<String, Cart> carts() {
        Map<String, Cart> carts = new HashMap<>();

        Cart cart1 = new Cart("cart-001", "userA");
        cart1.addItem(new CartItem("prod-101", "Laptop", 1, 1200.00));
        cart1.addItem(new CartItem("prod-102", "Mouse", 2, 25.00));

        Cart cart2 = new Cart("cart-002", "userB");
        cart2.addItem(new CartItem("prod-201", "Phone", 1, 800.00));
        cart2.addItem(new CartItem("prod-202", "Headphones", 1, 150.00));

        carts.put(cart1.getCartId(), cart1);
        carts.put(cart2.getCartId(), cart2);

        return carts;
    }
}
