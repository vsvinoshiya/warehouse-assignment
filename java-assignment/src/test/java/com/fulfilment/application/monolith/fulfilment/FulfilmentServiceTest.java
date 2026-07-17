package com.fulfilment.application.monolith.fulfilment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FulfilmentServiceTest {

  private TestFulfilmentStore fulfilmentStore;
  private TestProductRepository productRepository;
  private TestWarehouseStore warehouseStore;
  private TestStoreFinder storeFinder;
  private FulfilmentService fulfilmentService;

  @BeforeEach
  public void setup() {
    fulfilmentStore = new TestFulfilmentStore();
    productRepository = new TestProductRepository();
    warehouseStore = new TestWarehouseStore();
    storeFinder = new TestStoreFinder();
    fulfilmentService = new FulfilmentService();
    injectFields();
  }

  @Test
  public void shouldCreateFulfilmentWhenConstraintsAreSatisfied() {
    Store store = new Store("Store A");
    store.id = 1L;
    storeFinder.store = store;

    Product product = new Product("Product A");
    product.id = 1L;
    productRepository.products.add(product);

    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "W1";
    warehouseStore.warehouses.add(warehouse);

    FulfilmentRequest request = new FulfilmentRequest();
    request.storeId = 1L;
    request.productId = 1L;
    request.warehouseBusinessUnitCode = "W1";

    FulfilmentResponse response = fulfilmentService.create(request);

    assertEquals(1L, response.storeId);
    assertEquals("W1", response.warehouseBusinessUnitCode);
  }

  @Test
  public void shouldReject_WhenMoreThanTwoWarehousesPerProductPerStore() {
    setupStoreProductWarehouse();
    fulfilmentStore.assignments.add(createFulfilment(1L, 1L, "W1"));
    fulfilmentStore.assignments.add(createFulfilment(1L, 1L, "W2"));

    FulfilmentRequest request = new FulfilmentRequest();
    request.storeId = 1L;
    request.productId = 1L;
    request.warehouseBusinessUnitCode = "W3";

    WebApplicationException exception = assertThrows(WebApplicationException.class, () -> fulfilmentService.create(request));
    assertEquals(422, exception.getResponse().getStatus());
  }

  private Fulfilment createFulfilment(Long storeId, Long productId, String warehouseCode) {
    Fulfilment fulfilment = new Fulfilment();
    fulfilment.id = (long) (fulfilmentStore.assignments.size() + 1);
    Store store = new Store();
    store.id = storeId;
    Product product = new Product();
    product.id = productId;
    fulfilment.store = store;
    fulfilment.product = product;
    fulfilment.warehouseBusinessUnitCode = warehouseCode;
    return fulfilment;
  }

  private void setupStoreProductWarehouse() {
    Store store = new Store("Store A");
    store.id = 1L;
    storeFinder.store = store;
    Product product = new Product("Product A");
    product.id = 1L;
    productRepository.products.add(product);
    warehouseStore.warehouses.add(createWarehouse("W1"));
    warehouseStore.warehouses.add(createWarehouse("W2"));
    warehouseStore.warehouses.add(createWarehouse("W3"));
  }

  private Warehouse createWarehouse(String code) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = code;
    return warehouse;
  }

  private void injectFields() {
    try {
      var fulfilmentField = FulfilmentService.class.getDeclaredField("fulfilmentStore");
      fulfilmentField.setAccessible(true);
      fulfilmentField.set(fulfilmentService, fulfilmentStore);
      var productField = FulfilmentService.class.getDeclaredField("productRepository");
      productField.setAccessible(true);
      productField.set(fulfilmentService, productRepository);
      var warehouseField = FulfilmentService.class.getDeclaredField("warehouseStore");
      warehouseField.setAccessible(true);
      warehouseField.set(fulfilmentService, warehouseStore);
      var storeField = FulfilmentService.class.getDeclaredField("storeFinder");
      storeField.setAccessible(true);
      storeField.set(fulfilmentService, storeFinder);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static class TestFulfilmentStore implements FulfilmentStore {
    final List<Fulfilment> assignments = new ArrayList<>();

    @Override
    public List<Fulfilment> getAll() {
      return new ArrayList<>(assignments);
    }

    @Override
    public void create(Fulfilment fulfilment) {
      fulfilment.id = (long) (assignments.size() + 1);
      assignments.add(fulfilment);
    }

    @Override
    public Fulfilment findById(Long id) {
      return assignments.stream().filter(f -> f.id.equals(id)).findFirst().orElse(null);
    }

    @Override
    public void remove(Fulfilment fulfilment) {
      assignments.removeIf(f -> f.id.equals(fulfilment.id));
    }

    @Override
    public boolean existsAssignment(Long storeId, Long productId, String warehouseBusinessUnitCode) {
      return assignments.stream()
          .anyMatch(
              f -> f.store.id.equals(storeId)
                  && f.product.id.equals(productId)
                  && f.warehouseBusinessUnitCode.equals(warehouseBusinessUnitCode));
    }

    @Override
    public long countDistinctWarehousesForStore(Long storeId) {
      return assignments.stream()
          .filter(f -> f.store.id.equals(storeId))
          .map(f -> f.warehouseBusinessUnitCode)
          .distinct()
          .count();
    }

    @Override
    public long countDistinctWarehousesForProductAndStore(Long storeId, Long productId) {
      return assignments.stream()
          .filter(f -> f.store.id.equals(storeId) && f.product.id.equals(productId))
          .map(f -> f.warehouseBusinessUnitCode)
          .distinct()
          .count();
    }

    @Override
    public long countDistinctProductsForWarehouse(String warehouseBusinessUnitCode) {
      return assignments.stream()
          .filter(f -> f.warehouseBusinessUnitCode.equals(warehouseBusinessUnitCode))
          .map(f -> f.product.id)
          .distinct()
          .count();
    }
  }

  private static class TestProductRepository extends ProductRepository {
    final List<Product> products = new ArrayList<>();

    @Override
    public Product findById(Long id) {
      return products.stream().filter(p -> p.id.equals(id)).findFirst().orElse(null);
    }
  }

  private static class TestWarehouseStore implements WarehouseStore {
    final List<Warehouse> warehouses = new ArrayList<>();

    @Override
    public List<Warehouse> getAll() {
      return new ArrayList<>(warehouses);
    }

    @Override
    public void create(Warehouse warehouse) {
      warehouses.add(warehouse);
    }

    @Override
    public void update(Warehouse warehouse) {
      // not used
    }

    @Override
    public void remove(Warehouse warehouse) {
      warehouses.removeIf(w -> w.businessUnitCode.equals(warehouse.businessUnitCode));
    }

    @Override
    public Warehouse findByBusinessUnitCode(String buCode) {
      return warehouses.stream().filter(w -> w.businessUnitCode.equals(buCode)).findFirst().orElse(null);
    }
  }

  private static class TestStoreFinder implements StoreFinder {
    Store store;

    @Override
    public Store findById(Long id) {
      return store != null && store.id.equals(id) ? store : null;
    }
  }
}
