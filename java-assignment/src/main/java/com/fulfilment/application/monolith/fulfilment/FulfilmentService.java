package com.fulfilment.application.monolith.fulfilment;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FulfilmentService {

  @Inject FulfilmentStore fulfilmentStore;

  @Inject ProductRepository productRepository;

  @Inject WarehouseStore warehouseStore;

  @Inject StoreFinder storeFinder;

  public List<FulfilmentResponse> listAll() {
    return fulfilmentStore.getAll().stream().map(this::toResponse).collect(Collectors.toList());
  }

  @Transactional
  public FulfilmentResponse create(FulfilmentRequest request) {
    if (request == null) {
      throw new WebApplicationException("Fulfilment payload was not provided.", 422);
    }
    if (request.storeId == null) {
      throw new WebApplicationException("Store Id was not set on request.", 422);
    }
    if (request.productId == null) {
      throw new WebApplicationException("Product Id was not set on request.", 422);
    }
    if (request.warehouseBusinessUnitCode == null || request.warehouseBusinessUnitCode.isBlank()) {
      throw new WebApplicationException("Warehouse Business Unit Code was not set on request.", 422);
    }

    Store store = storeFinder.findById(request.storeId);
    if (store == null) {
      throw new WebApplicationException("Store not found.", 404);
    }

    Product product = productRepository.findById(request.productId);
    if (product == null) {
      throw new WebApplicationException("Product not found.", 404);
    }

    Warehouse warehouse = warehouseStore.findByBusinessUnitCode(request.warehouseBusinessUnitCode);
    if (warehouse == null) {
      throw new WebApplicationException("Warehouse not found.", 404);
    }

    boolean alreadyAssigned = fulfilmentStore.existsAssignment(store.id, product.id, warehouse.businessUnitCode);
    if (alreadyAssigned) {
      throw new WebApplicationException("This fulfilment assignment already exists.", 409);
    }

    long warehousesForProductAndStore = fulfilmentStore.countDistinctWarehousesForProductAndStore(store.id, product.id);
    if (warehousesForProductAndStore >= 2) {
      throw new WebApplicationException(
          "Each product can be fulfilled by a maximum of 2 warehouses per store.", 422);
    }

    long warehousesForStore = fulfilmentStore.countDistinctWarehousesForStore(store.id);
    if (warehousesForStore >= 3) {
      throw new WebApplicationException("Each store can be fulfilled by a maximum of 3 warehouses.", 422);
    }

    long productsForWarehouse = fulfilmentStore.countDistinctProductsForWarehouse(warehouse.businessUnitCode);
    if (productsForWarehouse >= 5) {
      throw new WebApplicationException("Each warehouse can store a maximum of 5 products.", 422);
    }

    Fulfilment fulfilment = new Fulfilment();
    fulfilment.store = store;
    fulfilment.product = product;
    fulfilment.warehouseBusinessUnitCode = warehouse.businessUnitCode;
    fulfilmentStore.create(fulfilment);

    return toResponse(fulfilment);
  }

  @Transactional
  public void delete(Long id) {
    Fulfilment fulfilment = fulfilmentStore.findById(id);
    if (fulfilment == null) {
      throw new WebApplicationException("Fulfilment assignment not found.", 404);
    }
    fulfilmentStore.remove(fulfilment);
  }

  private FulfilmentResponse toResponse(Fulfilment fulfilment) {
    if (fulfilment == null) {
      return null;
    }
    FulfilmentResponse response = new FulfilmentResponse();
    response.id = fulfilment.id;
    response.storeId = fulfilment.store.id;
    response.storeName = fulfilment.store.name;
    response.productId = fulfilment.product.id;
    response.productName = fulfilment.product.name;
    response.warehouseBusinessUnitCode = fulfilment.warehouseBusinessUnitCode;
    return response;
  }
}
