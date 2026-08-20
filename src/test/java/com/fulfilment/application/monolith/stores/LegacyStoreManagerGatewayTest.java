package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class LegacyStoreManagerGatewayTest {

    @Test
    void shouldCreateStoreOnLegacySystem() {

        Store store = new Store();
        store.name = "TEST_LEGACY_CREATE";
        store.quantityProductsInStock = 10;

        LegacyStoreManagerGateway gateway =
                new LegacyStoreManagerGateway();

        assertDoesNotThrow(
                () -> gateway.createStoreOnLegacySystem(store));
    }

    @Test
    void shouldUpdateStoreOnLegacySystem() {

        Store store = new Store();
        store.name = "TEST_LEGACY_UPDATE";
        store.quantityProductsInStock = 20;

        LegacyStoreManagerGateway gateway =
                new LegacyStoreManagerGateway();

        assertDoesNotThrow(
                () -> gateway.updateStoreOnLegacySystem(store));
    }
}