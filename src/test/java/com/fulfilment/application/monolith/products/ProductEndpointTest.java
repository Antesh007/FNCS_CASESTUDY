package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductEndpointTest {

    @Test
    public void shouldGetAllProducts() {

        given()
            .when()
            .get("/product")
            .then()
            .statusCode(200)
            .body(
                containsString("TONSTAD"),
                containsString("KALLAX"),
                containsString("BESTÅ")
            );
    }

    @Test
    public void shouldGetProductById() {

        given()
            .when()
            .get("/product/1")
            .then()
            .statusCode(200)
            .body(containsString("TONSTAD"));
    }

    @Test
    public void shouldReturn404WhenProductDoesNotExist() {

        given()
            .when()
            .get("/product/9999")
            .then()
            .statusCode(404)
            .body(containsString("does not exist"));
    }

    @Test
    public void shouldCreateProduct() {

        String productJson = """
            {
                "name": "TEST_PRODUCT",
                "description": "Test product",
                "price": 100.50,
                "stock": 20
            }
            """;

        given()
            .contentType("application/json")
            .body(productJson)
            .when()
            .post("/product")
            .then()
            .statusCode(201)
            .body(
                containsString("TEST_PRODUCT"),
                containsString("100.5"),
                containsString("20")
            );
    }

    @Test
    public void shouldRejectCreateWhenIdIsProvided() {

        String productJson = """
            {
                "id": 999,
                "name": "INVALID_PRODUCT",
                "stock": 10
            }
            """;

        given()
            .contentType("application/json")
            .body(productJson)
            .when()
            .post("/product")
            .then()
            .statusCode(422)
            .body(containsString("Id was invalidly set"));
    }

    @Test
    public void shouldUpdateProduct() {

        // Create a product specifically for this test
        String createJson = """
            {
                "name": "UPDATE_TEST_PRODUCT",
                "description": "Original description",
                "price": 100,
                "stock": 10
            }
            """;

        Integer id =
            given()
                .contentType("application/json")
                .body(createJson)
                .when()
                .post("/product")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        String updateJson = """
            {
                "name": "UPDATED_PRODUCT",
                "description": "Updated description",
                "price": 200.50,
                "stock": 30
            }
            """;

        given()
            .contentType("application/json")
            .body(updateJson)
            .when()
            .put("/product/" + id)
            .then()
            .statusCode(200)
            .body(
                containsString("UPDATED_PRODUCT"),
                containsString("Updated description"),
                containsString("200.5"),
                containsString("30")
            );
    }

    @Test
    public void shouldRejectUpdateWhenNameIsMissing() {

        String productJson = """
            {
                "description": "No name",
                "price": 100,
                "stock": 10
            }
            """;

        given()
            .contentType("application/json")
            .body(productJson)
            .when()
            .put("/product/1")
            .then()
            .statusCode(422)
            .body(containsString("Product Name was not set"));
    }

    @Test
    public void shouldReturn404WhenUpdatingUnknownProduct() {

        String productJson = """
            {
                "name": "UNKNOWN_PRODUCT",
                "stock": 10
            }
            """;

        given()
            .contentType("application/json")
            .body(productJson)
            .when()
            .put("/product/9999")
            .then()
            .statusCode(404)
            .body(containsString("does not exist"));
    }

    @Test
    public void shouldDeleteProduct() {

        // Create a product specifically for this test
        String productJson = """
            {
                "name": "DELETE_TEST_PRODUCT",
                "description": "Delete test",
                "price": 50,
                "stock": 10
            }
            """;

        Integer id =
            given()
                .contentType("application/json")
                .body(productJson)
                .when()
                .post("/product")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
            .when()
            .delete("/product/" + id)
            .then()
            .statusCode(204);

        given()
            .when()
            .get("/product/" + id)
            .then()
            .statusCode(404);
    }

    @Test
    public void shouldReturn404WhenDeletingUnknownProduct() {

        given()
            .when()
            .delete("/product/9999")
            .then()
            .statusCode(404)
            .body(containsString("does not exist"));
    }
    
    @Test
    public void shouldCreateProductWithAllFields() {
        String json = """
            {
                "name": "COVERAGE_PRODUCT",
                "description": "Coverage test",
                "price": 150.50,
                "stock": 25
            }
            """;

        given()
            .contentType("application/json")
            .body(json)
            .when()
            .post("/product")
            .then()
            .statusCode(201)
            .body(
                containsString("COVERAGE_PRODUCT"),
                containsString("Coverage test"),
                containsString("150.5"),
                containsString("25"));
    }

    @Test
    public void shouldReturn404WhenGettingDeletedProduct() {
        String json = """
            {
                "name": "DELETE_COVERAGE_PRODUCT",
                "description": "Delete coverage",
                "price": 50,
                "stock": 5
            }
            """;

        Integer id =
            given()
                .contentType("application/json")
                .body(json)
                .when()
                .post("/product")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
            .when()
            .delete("/product/" + id)
            .then()
            .statusCode(204);

        given()
            .when()
            .get("/product/" + id)
            .then()
            .statusCode(404);
    }

    @Test
    public void shouldRejectUpdateWithInvalidProduct() {
        String json = """
            {
                "name": "INVALID_PRODUCT",
                "description": "Invalid",
                "price": 10,
                "stock": 5
            }
            """;

        given()
            .contentType("application/json")
            .body(json)
            .when()
            .put("/product/99999")
            .then()
            .statusCode(404);
    }
}