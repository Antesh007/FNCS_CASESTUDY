package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
class WarehouseRepositoryTest {

    @Inject
    WarehouseRepository warehouseRepository;

    private Warehouse createWarehouse(String code) {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = code;
        warehouse.location = "TEST_LOCATION";
        warehouse.capacity = 100;
        warehouse.stock = 20;
        return warehouse;
    }

    @Test
    void shouldGetAllWarehouses() {

        List<Warehouse> warehouses = warehouseRepository.getAll();

        assertNotNull(warehouses);
        assertTrue(warehouses.size() >= 3);
    }

    @Test
    @Transactional
    void shouldCreateWarehouse() {

        Warehouse warehouse = createWarehouse("TEST_CREATE");

        warehouseRepository.create(warehouse);

        Warehouse result =
                warehouseRepository.findByBusinessUnitCode("TEST_CREATE");

        assertNotNull(result);
        assertEquals("TEST_CREATE", result.businessUnitCode);
        assertEquals("TEST_LOCATION", result.location);
        assertEquals(100, result.capacity);
        assertEquals(20, result.stock);
    }

    @Test
    @Transactional
    void shouldUpdateExistingWarehouse() {

        Warehouse warehouse = createWarehouse("TEST_UPDATE");

        warehouseRepository.create(warehouse);

        Warehouse updated = createWarehouse("TEST_UPDATE");
        updated.location = "UPDATED_LOCATION";
        updated.capacity = 200;
        updated.stock = 50;

        warehouseRepository.update(updated);

        Warehouse result =
                warehouseRepository.findByBusinessUnitCode("TEST_UPDATE");

        assertNotNull(result);
        assertEquals("UPDATED_LOCATION", result.location);
        assertEquals(200, result.capacity);
        assertEquals(50, result.stock);
    }

    @Test
    @Transactional
    void shouldNotFailWhenUpdatingUnknownWarehouse() {

        Warehouse warehouse = createWarehouse("UNKNOWN_UPDATE");

        assertDoesNotThrow(
                () -> warehouseRepository.update(warehouse));
    }

    @Test
    @Transactional
    void shouldRemoveWarehouse() {

        Warehouse warehouse = createWarehouse("TEST_REMOVE");

        warehouseRepository.create(warehouse);

        assertNotNull(
                warehouseRepository.findByBusinessUnitCode("TEST_REMOVE"));

        warehouseRepository.remove(warehouse);

        assertNull(
                warehouseRepository.findByBusinessUnitCode("TEST_REMOVE"));
    }

    @Test
    void shouldFindWarehouseByBusinessUnitCode() {

        Warehouse result =
                warehouseRepository.findByBusinessUnitCode("MWH.001");

        assertNotNull(result);
        assertEquals("MWH.001", result.businessUnitCode);
        assertEquals("ZWOLLE-001", result.location);
    }

    @Test
    void shouldReturnNullWhenWarehouseDoesNotExist() {

        Warehouse result =
                warehouseRepository.findByBusinessUnitCode("DOES_NOT_EXIST");

        assertNull(result);
    }
}