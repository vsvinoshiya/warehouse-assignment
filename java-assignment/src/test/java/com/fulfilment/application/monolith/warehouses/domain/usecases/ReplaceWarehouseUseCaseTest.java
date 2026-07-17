package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ReplaceWarehouseUseCaseTest {

  @Test
  void replaceArchivesExistingWarehouseAndCreatesReplacement() {
    var store = new StubWarehouseStore();
    var original = new Warehouse();
    original.businessUnitCode = "MWH.001";
    original.location = "ZWOLLE-001";
    original.capacity = 100;
    original.stock = 10;
    store.warehouses.add(original);

    var useCase = new ReplaceWarehouseUseCase(store, new StubLocationResolver());
    var replacement = new Warehouse();
    replacement.businessUnitCode = "MWH.001";
    replacement.location = "ZWOLLE-001";
    replacement.capacity = 120;
    replacement.stock = 10;

    useCase.replace(replacement);

    assertEquals(2, store.warehouses.size());
    var archived = store.warehouses.stream().filter(warehouse -> warehouse.businessUnitCode.equals("MWH.001") && warehouse.archivedAt != null).findFirst();
    assertNotNull(archived);
    var replacementStored = store.warehouses.stream().filter(warehouse -> warehouse.businessUnitCode.equals("MWH.001") && warehouse.archivedAt == null).findFirst();
    assertNotNull(replacementStored);
    assertNull(replacementStored.get().archivedAt);
  }

  private static class StubLocationResolver implements LocationResolver {
    @Override
    public Location resolveByIdentifier(String identifier) {
      return new Location(identifier, 5, 200);
    }
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
      warehouses.removeIf(existing -> existing.businessUnitCode.equals(warehouse.businessUnitCode) && existing.archivedAt == null);
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
