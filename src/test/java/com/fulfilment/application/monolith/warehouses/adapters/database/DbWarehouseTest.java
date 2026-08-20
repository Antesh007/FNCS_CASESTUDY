package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DbWarehouseTest {

    @Test
    void shouldConvertDbWarehouseToWarehouse() {

        DbWarehouse dbWarehouse = new DbWarehouse();

        dbWarehouse.id = 1L;
        dbWarehouse.businessUnitCode = "MWH.TEST";
        dbWarehouse.location = "TEST_LOCATION";
        dbWarehouse.capacity = 100;
        dbWarehouse.stock = 50;

        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime archivedAt = LocalDateTime.of(2026, 2, 1, 10, 0);

        dbWarehouse.createdAt = createdAt;
        dbWarehouse.archivedAt = archivedAt;

        Warehouse result = dbWarehouse.toWarehouse();

        assertNotNull(result);
        assertEquals("MWH.TEST", result.businessUnitCode);
        assertEquals("TEST_LOCATION", result.location);
        assertEquals(100, result.capacity);
        assertEquals(50, result.stock);
        assertEquals(createdAt, result.createdAt);
        assertEquals(archivedAt, result.archivedAt);
    }
}