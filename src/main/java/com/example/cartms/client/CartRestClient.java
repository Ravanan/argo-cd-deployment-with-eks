package com.example.cartms.client;

import com.example.cartms.model.Cart;
import com.example.cartms.model.CartItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CartRestClient {

    private static final Logger log = LoggerFactory.getLogger(CartRestClient.class);
    private final RestClient restClient;

    public CartRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public Cart addItem(String cartId, CartItem item, String callerApp) {
        log.info("Calling cartms addItem for cartId={} caller={}", cartId, callerApp);
        ResponseEntity<Cart> response = restClient.post()
                .uri("/cart/{cartId}/addItem", cartId)
                .header("X-Caller-App", callerApp)
                .body(item)
                .retrieve()
                .toEntity(Cart.class);
        return response != null ? response.getBody() : null;
    }

    public Cart getCart(String cartId, String callerApp) {
        log.info("Calling cartms getCart for cartId={} caller={}", cartId, callerApp);
        ResponseEntity<Cart> response = restClient.get()
                .uri("/cart/{cartId}", cartId)
                .header("X-Caller-App", callerApp)
                .retrieve()
                .toEntity(Cart.class);
        return response != null ? response.getBody() : null;
    }

    public Double getTotal(String cartId, String callerApp) {
        log.info("Calling cartms getTotal for cartId={} caller={}", cartId, callerApp);
        ResponseEntity<Double> response = restClient.get()
                .uri("/cart/{cartId}/total", cartId)
                .header("X-Caller-App", callerApp)
                .retrieve()
                .toEntity(Double.class);
        return response != null ? response.getBody() : 0.0;
    }
}
