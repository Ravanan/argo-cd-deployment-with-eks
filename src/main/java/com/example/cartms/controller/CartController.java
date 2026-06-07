package com.example.cartms.controller;

import com.example.cartms.model.Cart;
import com.example.cartms.model.CartItem;
import com.example.cartms.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller exposing cart operations under {@code /cart}.
 * Backed by the in-memory {@code Map<String, Cart>} bean defined in
 * {@link com.example.cartms.config.CartDataConfig} — all state is
 * process-local and lost on restart.
 *
 * Concurrency: the injected map is a plain {@link java.util.HashMap},
 * so concurrent writes from multiple requests are unsafe. Acceptable
 * for the demo; swap for {@link java.util.concurrent.ConcurrentHashMap}
 * or a real repository before production.
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    // Injected from CartDataConfig#carts() — shared, mutable, process-local state.
    @Autowired
    private Map<String, Cart> carts;

    /**
     * Adds an item to the cart, creating the cart on first use.
     * CartId is managed via cookie; if not present, a new one is generated and set.
     */
    @PostMapping("/addItem")
    public Cart addItem(@RequestBody CartItem item,
                        @RequestHeader(value = "X-Caller-App", required = false) String callerApp,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        logCallerApp(callerApp, "addItem");
        String cartId = CookieUtil.getCartIdFromCookie(request);
        if (cartId == null) {
            cartId = generateCartId();
            CookieUtil.setCartIdCookie(response, cartId);
        }
        Cart cart = carts.computeIfAbsent(cartId, id -> new Cart(id, "user123"));
        cart.addItem(item);
        return cart;
    }

    /**
     * Removes every line item with the given productId from the cart.
     * CartId is retrieved from cookies; returns 404 if cookie missing or cart not found.
     */
    @DeleteMapping("/removeItem/{productId}")
    public Cart removeItem(@PathVariable String productId,
                           @RequestHeader(value = "X-Caller-App", required = false) String callerApp,
                           HttpServletRequest request) {
        logCallerApp(callerApp, "removeItem");
        String cartId = CookieUtil.getCartIdFromCookie(request);
        if (cartId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No cart session found");
        }
        Cart cart = carts.get(cartId);
        if (cart != null) {
            cart.removeItem(productId);
        }
        return cart;
    }

    /**
     * Returns the cart for the current session, or 404 if no cart exists.
     * CartId is retrieved from cookies.
     */
    @GetMapping
    public Cart getCart(@RequestHeader(value = "X-Caller-App", required = false) String callerApp,
                        HttpServletRequest request) {
        logCallerApp(callerApp, "getCart");
        String cartId = CookieUtil.getCartIdFromCookie(request);
        if (cartId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No cart session found");
        }
        Cart cart = carts.get(cartId);
        if (cart == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found: " + cartId);
        }
        return cart;
    }

    /** Returns the cart total for the current session, or 0.0 when the cart is missing. */
    @GetMapping("/total")
    public double getTotal(@RequestHeader(value = "X-Caller-App", required = false) String callerApp,
                           HttpServletRequest request) {
        logCallerApp(callerApp, "getTotal");
        String cartId = CookieUtil.getCartIdFromCookie(request);
        if (cartId == null) {
            return 0.0;
        }
        Cart cart = carts.get(cartId);
        return cart != null ? cart.calculateTotal() : 0.0;
    }

    private String generateCartId() {
        return "cart-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private void logCallerApp(String callerApp, String operation) {
        if (callerApp == null || callerApp.isBlank()) {
            log.info("CartController {} called without X-Caller-App", operation);
        } else {
            log.info("CartController {} called by {}", operation, callerApp);
        }
    }
}
