package com.fulfilment.application.monolith.products;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkus.panache.common.Sort;

@ExtendWith(MockitoExtension.class)
class ProductResourceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductResource productResource;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.id = 1L;
        product.name = "TONSTAD";
        product.description = "Test product";
        product.price = new BigDecimal("100.50");
        product.stock = 10;
    }

    @Test
    void shouldGetAllProducts() {

        when(productRepository.listAll(any(Sort.class)))
                .thenReturn(List.of(product));

        List<Product> result = productResource.get();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TONSTAD", result.get(0).name);

        verify(productRepository).listAll(any(Sort.class));
    }

    @Test
    void shouldGetProductById() {

        when(productRepository.findById(1L))
                .thenReturn(product);

        Product result = productResource.getSingle(1L);

        assertNotNull(result);
        assertEquals(1L, result.id);
        assertEquals("TONSTAD", result.name);

        verify(productRepository).findById(1L);
    }

    @Test
    void shouldThrow404WhenProductDoesNotExist() {

        when(productRepository.findById(999L))
                .thenReturn(null);

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> productResource.getSingle(999L));

        assertEquals(404, exception.getResponse().getStatus());
        assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    void shouldCreateProduct() {

        Product newProduct = new Product();
        newProduct.name = "NEW_PRODUCT";
        newProduct.description = "New description";
        newProduct.price = new BigDecimal("150.50");
        newProduct.stock = 20;

        doNothing().when(productRepository).persist(newProduct);

        Response response = productResource.create(newProduct);

        assertEquals(201, response.getStatus());
        assertSame(newProduct, response.getEntity());

        verify(productRepository).persist(newProduct);
    }

    @Test
    void shouldRejectCreateWhenIdIsProvided() {

        Product invalidProduct = new Product();
        invalidProduct.id = 100L;
        invalidProduct.name = "INVALID";

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> productResource.create(invalidProduct));

        assertEquals(422, exception.getResponse().getStatus());
    }

    @Test
    void shouldUpdateProduct() {

        Product updatedProduct = new Product();
        updatedProduct.name = "UPDATED_PRODUCT";
        updatedProduct.description = "Updated description";
        updatedProduct.price = new BigDecimal("200.50");
        updatedProduct.stock = 30;

        when(productRepository.findById(1L))
                .thenReturn(product);

        Product result =
                productResource.update(1L, updatedProduct);

        assertNotNull(result);
        assertEquals("UPDATED_PRODUCT", result.name);
        assertEquals("Updated description", result.description);
        assertEquals(new BigDecimal("200.50"), result.price);
        assertEquals(30, result.stock);

        verify(productRepository).findById(1L);
        verify(productRepository).persist(product);
    }

    @Test
    void shouldRejectUpdateWhenNameIsMissing() {

        Product invalidProduct = new Product();
        invalidProduct.description = "No name";
        invalidProduct.stock = 10;

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> productResource.update(1L, invalidProduct));

        assertEquals(422, exception.getResponse().getStatus());

        verify(productRepository, never()).findById(anyLong());
    }

    @Test
    void shouldReturn404WhenUpdatingUnknownProduct() {

        Product updatedProduct = new Product();
        updatedProduct.name = "UNKNOWN";
        updatedProduct.stock = 10;

        when(productRepository.findById(999L))
                .thenReturn(null);

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> productResource.update(999L, updatedProduct));

        assertEquals(404, exception.getResponse().getStatus());
    }

    @Test
    void shouldDeleteProduct() {

        when(productRepository.findById(1L))
                .thenReturn(product);

        Response response =
                productResource.delete(1L);

        assertEquals(204, response.getStatus());

        verify(productRepository).findById(1L);
        verify(productRepository).delete(product);
    }

    @Test
    void shouldReturn404WhenDeletingUnknownProduct() {

        when(productRepository.findById(999L))
                .thenReturn(null);

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> productResource.delete(999L));

        assertEquals(404, exception.getResponse().getStatus());

        verify(productRepository, never()).delete(any());
    }

    @Test
    void shouldMapWebApplicationExceptionToResponse() throws Exception {

        ProductResource.ErrorMapper mapper =
                new ProductResource.ErrorMapper();

        ObjectMapper objectMapper = new ObjectMapper();

        var field =
                ProductResource.ErrorMapper.class
                        .getDeclaredField("objectMapper");

        field.setAccessible(true);
        field.set(mapper, objectMapper);

        WebApplicationException exception =
                new WebApplicationException("Product not found", 404);

        Response response = mapper.toResponse(exception);

        assertEquals(404, response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void shouldMapGenericExceptionTo500Response() throws Exception {

        ProductResource.ErrorMapper mapper =
                new ProductResource.ErrorMapper();

        ObjectMapper objectMapper = new ObjectMapper();

        var field =
                ProductResource.ErrorMapper.class
                        .getDeclaredField("objectMapper");

        field.setAccessible(true);
        field.set(mapper, objectMapper);

        Exception exception =
                new RuntimeException("Something went wrong");

        Response response = mapper.toResponse(exception);

        assertEquals(500, response.getStatus());
        assertNotNull(response.getEntity());
    }
}