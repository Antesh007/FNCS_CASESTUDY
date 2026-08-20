package com.fulfilment.application.monolith.location;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;

public class LocationGatewayTest {

    @Test
    void shouldResolveExistingLocation() {

        // given
        LocationGateway gateway = new LocationGateway();

        // when
        Location location = gateway.resolveByIdentifier("ZWOLLE-001");

        // then
        assertNotNull(location);
        assertEquals("ZWOLLE-001", location.identification);
    }

    @Test
    void shouldReturnNullForUnknownLocation() {

        // given
        LocationGateway gateway = new LocationGateway();

        // when
        Location location = gateway.resolveByIdentifier("UNKNOWN");

        // then
        assertNull(location);
    }
}