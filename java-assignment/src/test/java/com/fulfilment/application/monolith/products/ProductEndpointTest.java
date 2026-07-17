package com.fulfilment.application.monolith.products;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class ProductEndpointTest {

  @Test
  public void testCrudProduct() throws Exception {
    TestProductRepository repository = new TestProductRepository();
    ProductResource resource = new ProductResource();
    ProductService service = new ProductService();
    // inject test repository into service
    java.lang.reflect.Field storeField = ProductService.class.getDeclaredField("productStore");
    storeField.setAccessible(true);
    storeField.set(service, repository);
    // inject service into resource
    java.lang.reflect.Field serviceField = ProductResource.class.getDeclaredField("productService");
    serviceField.setAccessible(true);
    serviceField.set(resource, service);

    Product payload = new Product();
    payload.name = "PRODUCT-" + UUID.randomUUID().toString().substring(0, 8);
    payload.description = "New product";
    payload.price = new BigDecimal("19.99");
    payload.stock = 10;

    Response createResponse = resource.create(payload);
    assertEquals(201, createResponse.getStatus());
    assertNotNull(payload.id);

    Product created = resource.getSingle(payload.id);
    assertEquals(payload.name, created.name);

    payload.description = "Updated description";
    payload.price = new BigDecimal("24.50");
    payload.stock = 25;

    Product updated = resource.update(payload.id, payload);
    assertEquals("Updated description", updated.description);
    assertEquals(new BigDecimal("24.50"), updated.price);

    Response deleteResponse = resource.delete(payload.id);
    assertEquals(204, deleteResponse.getStatus());
    assertNull(repository.findById(payload.id));
  }

  private void injectRepository(ProductResource resource, ProductRepository repository) throws Exception {
    Field field = ProductResource.class.getDeclaredField("productRepository");
    field.setAccessible(true);
    field.set(resource, repository);
  }

  private static class TestProductRepository extends ProductRepository {
    private final List<Product> products = new ArrayList<>();

    @Override
    public void persist(Product entity) {
      if (entity.id == null) {
        entity.id = (long) (products.size() + 1);
      }
      products.removeIf(existing -> existing.id != null && existing.id.equals(entity.id));
      products.add(entity);
    }

    @Override
    public void delete(Product entity) {
      products.removeIf(existing -> existing.id != null && existing.id.equals(entity.id));
    }

    @Override
    public Product findById(Long id) {
      return products.stream().filter(existing -> existing.id != null && existing.id.equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Product> listAll() {
      return new ArrayList<>(products);
    }
  }
}
