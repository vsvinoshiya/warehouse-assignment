package com.fulfilment.application.monolith.warehouses.domain.usecases;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

public class ArchiveWarehouseUseCaseTest {

  @Test
  void archiveMarksWarehouseAsArchived() {
    var store = new StubWarehouseStore();
    var warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.001";
    warehouse.location = "ZWOLLE-001";
    warehouse.capacity = 100;
    warehouse.stock = 10;
    store.warehouses.add(warehouse);

    var useCase = new ArchiveWarehouseUseCase(store);
    useCase.archive(warehouse);

    assertNotNull(warehouse.archivedAt);
  }

  private static class StubWarehouseStore implements WarehouseStore {
    private final List<Warehouse> warehouses = new ArrayList<>();

    @Override
    public List<Warehouse> getAll() {
      return warehouses;
    }

    @Override
    public void create(Warehouse warehouse) {
      warehouses.add(warehouse);
    }

    @Override
    public void update(Warehouse warehouse) {
      warehouses.removeIf(existing -> existing.businessUnitCode.equals(warehouse.businessUnitCode));
      warehouses.add(warehouse);
    }

    @Override
    public void remove(Warehouse warehouse) {
      warehouses.removeIf(existing -> existing.businessUnitCode.equals(warehouse.businessUnitCode));
    }

    @Override
    public Warehouse findByBusinessUnitCode(String buCode) {
      return warehouses.stream()
          .filter(existing -> existing.businessUnitCode.equals(buCode))
          .findFirst()
          .orElse(null);
    }
  }
}
