package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
//import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;
import com.warehouse.api.beans.Warehouse;
import jakarta.ws.rs.WebApplicationException;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WarehouseResourceImplTest {

    @Mock
    WarehouseRepository warehouseRepository;

    @Mock
    CreateWarehouseUseCase createWarehouseUseCase;

    @Mock
    ReplaceWarehouseUseCase replaceWarehouseUseCase;

    @Mock
    ArchiveWarehouseUseCase archiveWarehouseUseCase;

    @InjectMocks
    WarehouseResourceImpl resource;

    private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domainWarehouse;
    private Warehouse apiWarehouse;

    @BeforeEach
    void setup() {

    	domainWarehouse =
    	        new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
        domainWarehouse.businessUnitCode = "MWH.001";
        domainWarehouse.location = "ZWOLLE-001";
        domainWarehouse.capacity = 100;
        domainWarehouse.stock = 10;
        domainWarehouse.createdAt = LocalDateTime.now();

        apiWarehouse = new Warehouse();
        apiWarehouse.setBusinessUnitCode("MWH.001");
        apiWarehouse.setLocation("ZWOLLE-001");
        apiWarehouse.setCapacity(100);
        apiWarehouse.setStock(10);
    }

    @Test
    void shouldListOnlyActiveWarehouses() {

    	com.fulfilment.application.monolith.warehouses.domain.models.Warehouse active =
    	        domainWarehouse;

    	com.fulfilment.application.monolith.warehouses.domain.models.Warehouse archived =
    	        new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
        archived.businessUnitCode = "MWH.002";
        archived.location = "AMSTERDAM-001";
        archived.capacity = 50;
        archived.stock = 5;
        archived.archivedAt = LocalDateTime.now();

        when(warehouseRepository.getAll())
                .thenReturn(List.of(active, archived));

        List<com.warehouse.api.beans.Warehouse> result =
                resource.listAllWarehousesUnits();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("MWH.001", result.get(0).getBusinessUnitCode());
        assertEquals("ZWOLLE-001", result.get(0).getLocation());

        verify(warehouseRepository).getAll();
    }

    @Test
    void shouldCreateWarehouse() {

        doNothing().when(createWarehouseUseCase).create(any());

        Warehouse result =
                resource.createANewWarehouseUnit(apiWarehouse);

        assertNotNull(result);
        assertEquals("MWH.001", result.getBusinessUnitCode());
        assertEquals("ZWOLLE-001", result.getLocation());
        assertEquals(100, result.getCapacity());
        assertEquals(10, result.getStock());

        verify(createWarehouseUseCase).create(any());
    }

    @Test
    void shouldGetWarehouseByBusinessUnitCode() {

        when(warehouseRepository.findByBusinessUnitCode("MWH.001"))
                .thenReturn(domainWarehouse);

        Warehouse result =
                resource.getAWarehouseUnitByID("MWH.001");

        assertNotNull(result);
        assertEquals("MWH.001", result.getBusinessUnitCode());
        assertEquals("ZWOLLE-001", result.getLocation());
        assertEquals(100, result.getCapacity());
        assertEquals(10, result.getStock());
    }

    @Test
    void shouldThrow404WhenWarehouseDoesNotExist() {

        when(warehouseRepository.findByBusinessUnitCode("UNKNOWN"))
                .thenReturn(null);

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> resource.getAWarehouseUnitByID("UNKNOWN"));

        assertEquals(404, exception.getResponse().getStatus());
    }

    @Test
    void shouldThrow404WhenWarehouseIsArchived() {

    	com.fulfilment.application.monolith.warehouses.domain.models.Warehouse archived =
    	        new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
        archived.archivedAt = LocalDateTime.now();

        when(warehouseRepository.findByBusinessUnitCode("MWH.001"))
                .thenReturn(archived);

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> resource.getAWarehouseUnitByID("MWH.001"));

        assertEquals(404, exception.getResponse().getStatus());
    }

    @Test
    void shouldArchiveWarehouse() {

        when(warehouseRepository.findByBusinessUnitCode("MWH.001"))
                .thenReturn(domainWarehouse);

        resource.archiveAWarehouseUnitByID("MWH.001");

        verify(archiveWarehouseUseCase).archive(domainWarehouse);
    }

    @Test
    void shouldThrow404WhenArchivingUnknownWarehouse() {

        when(warehouseRepository.findByBusinessUnitCode("UNKNOWN"))
                .thenReturn(null);

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> resource.archiveAWarehouseUnitByID("UNKNOWN"));

        assertEquals(404, exception.getResponse().getStatus());

        verify(archiveWarehouseUseCase, never()).archive(any());
    }

    @Test
    void shouldReplaceWarehouse() {

        when(warehouseRepository.findByBusinessUnitCode("MWH.001"))
                .thenReturn(domainWarehouse);

        Warehouse result =
                resource.replaceTheCurrentActiveWarehouse(
                        "MWH.001",
                        apiWarehouse);

        assertNotNull(result);
        assertEquals("MWH.001", result.getBusinessUnitCode());
        assertEquals("ZWOLLE-001", result.getLocation());

        verify(replaceWarehouseUseCase).replace(any());
    }

    @Test
    void shouldThrow404WhenReplacingUnknownWarehouse() {

        when(warehouseRepository.findByBusinessUnitCode("UNKNOWN"))
                .thenReturn(null);

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> resource.replaceTheCurrentActiveWarehouse(
                                "UNKNOWN",
                                apiWarehouse));

        assertEquals(404, exception.getResponse().getStatus());

        verify(replaceWarehouseUseCase, never()).replace(any());
    }
}