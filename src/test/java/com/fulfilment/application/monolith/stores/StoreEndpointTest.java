package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class StoreEndpointTest {

	@Test
	public void shouldGetAllStores() {

	    given()
	        .when()
	        .get("/store")
	        .then()
	        .statusCode(200)
	        .body(
	            containsString("TEST_STORE"),
	            containsString("UPDATED_STORE"));
	}

    @Test
    public void shouldGetStoreById() {

        given()
            .when()
            .get("/store/1")
            .then()
            .statusCode(200)
            .body(containsString("TONSTAD"));
    }

    @Test
    public void shouldReturn404WhenStoreDoesNotExist() {

        given()
            .when()
            .get("/store/9999")
            .then()
            .statusCode(404)
            .body(containsString("does not exist"));
    }

    @Test
    public void shouldCreateStore() {

        String storeJson = """
            {
                "name": "TEST_STORE",
                "quantityProductsInStock": 20
            }
            """;

        given()
            .contentType("application/json")
            .body(storeJson)
            .when()
            .post("/store")
            .then()
            .statusCode(201)
            .body(
                containsString("TEST_STORE"),
                containsString("20")
            );
    }

    @Test
    public void shouldRejectCreateWhenIdIsProvided() {

        String storeJson = """
            {
                "id": 999,
                "name": "INVALID_STORE",
                "quantityProductsInStock": 10
            }
            """;

        given()
            .contentType("application/json")
            .body(storeJson)
            .when()
            .post("/store")
            .then()
            .statusCode(422)
            .body(containsString("Id was invalidly set"));
    }

    @Test
    public void shouldUpdateStore() {

        String storeJson = """
            {
                "name": "UPDATED_STORE",
                "quantityProductsInStock": 25
            }
            """;

        given()
            .contentType("application/json")
            .body(storeJson)
            .when()
            .put("/store/1")
            .then()
            .statusCode(200)
            .body(
                containsString("UPDATED_STORE"),
                containsString("25")
            );
    }

    @Test
    public void shouldRejectUpdateWhenNameIsMissing() {

        String storeJson = """
            {
                "quantityProductsInStock": 25
            }
            """;

        given()
            .contentType("application/json")
            .body(storeJson)
            .when()
            .put("/store/1")
            .then()
            .statusCode(422)
            .body(containsString("Store Name was not set"));
    }

    @Test
    public void shouldReturn404WhenUpdatingUnknownStore() {

        String storeJson = """
            {
                "name": "UNKNOWN_STORE",
                "quantityProductsInStock": 10
            }
            """;

        given()
            .contentType("application/json")
            .body(storeJson)
            .when()
            .put("/store/9999")
            .then()
            .statusCode(404);
    }

    @Test
    public void shouldPatchStore() {

        String storeJson = """
            {
                "name": "PATCHED_STORE",
                "quantityProductsInStock": 30
            }
            """;

        given()
            .contentType("application/json")
            .body(storeJson)
            .when()
            .patch("/store/2")
            .then()
            .statusCode(200)
            .body(
                containsString("PATCHED_STORE"),
                containsString("30")
            );
    }

    @Test
    public void shouldRejectPatchWhenNameIsMissing() {

        String storeJson = """
            {
                "quantityProductsInStock": 30
            }
            """;

        given()
            .contentType("application/json")
            .body(storeJson)
            .when()
            .patch("/store/2")
            .then()
            .statusCode(422)
            .body(containsString("Store Name was not set"));
    }

    @Test
    public void shouldReturn404WhenPatchingUnknownStore() {

        String storeJson = """
            {
                "name": "UNKNOWN_STORE",
                "quantityProductsInStock": 10
            }
            """;

        given()
            .contentType("application/json")
            .body(storeJson)
            .when()
            .patch("/store/9999")
            .then()
            .statusCode(404);
    }

    @Test
    public void shouldDeleteStore() {

        // Create a store first so this test doesn't destroy
        // one of the initial test records.
        String storeJson = """
            {
                "name": "DELETE_TEST_STORE",
                "quantityProductsInStock": 10
            }
            """;

        Integer id =
            given()
                .contentType("application/json")
                .body(storeJson)
                .when()
                .post("/store")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
            .when()
            .delete("/store/" + id)
            .then()
            .statusCode(204);

        given()
            .when()
            .get("/store/" + id)
            .then()
            .statusCode(404);
    }

    @Test
    public void shouldReturn404WhenDeletingUnknownStore() {

        given()
            .when()
            .delete("/store/9999")
            .then()
            .statusCode(404);
    }
   
    @Test
    public void shouldHandleWebApplicationException() throws Exception {

        StoreResource.ErrorMapper mapper =
            new StoreResource.ErrorMapper();

        com.fasterxml.jackson.databind.ObjectMapper objectMapper =
            new com.fasterxml.jackson.databind.ObjectMapper();

        var field =
            StoreResource.ErrorMapper.class
                .getDeclaredField("objectMapper");

        field.setAccessible(true);
        field.set(mapper, objectMapper);

        jakarta.ws.rs.WebApplicationException exception =
            new jakarta.ws.rs.WebApplicationException(
                "Store not found", 404);

        jakarta.ws.rs.core.Response response =
            mapper.toResponse(exception);

        org.junit.jupiter.api.Assertions.assertEquals(
            404, response.getStatus());

        org.junit.jupiter.api.Assertions.assertNotNull(
            response.getEntity());
    }
    @Test
    public void shouldHandleGenericException() throws Exception {

        StoreResource.ErrorMapper mapper =
            new StoreResource.ErrorMapper();

        com.fasterxml.jackson.databind.ObjectMapper objectMapper =
            new com.fasterxml.jackson.databind.ObjectMapper();

        var field =
            StoreResource.ErrorMapper.class
                .getDeclaredField("objectMapper");

        field.setAccessible(true);
        field.set(mapper, objectMapper);

        Exception exception =
            new RuntimeException("Something went wrong");

        jakarta.ws.rs.core.Response response =
            mapper.toResponse(exception);

        org.junit.jupiter.api.Assertions.assertEquals(
            500, response.getStatus());

        org.junit.jupiter.api.Assertions.assertNotNull(
            response.getEntity());
    }
}