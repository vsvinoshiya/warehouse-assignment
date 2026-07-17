package com.fulfilment.application.monolith.fulfilment;

import java.util.List;

public interface FulfilmentStore {

  List<Fulfilment> getAll();

  void create(Fulfilment fulfilment);

  Fulfilment findById(Long id);

  void remove(Fulfilment fulfilment);

  boolean existsAssignment(Long storeId, Long productId, String warehouseBusinessUnitCode);

  long countDistinctWarehousesForStore(Long storeId);

  long countDistinctWarehousesForProductAndStore(Long storeId, Long productId);

  long countDistinctProductsForWarehouse(String warehouseBusinessUnitCode);
}
