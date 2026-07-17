package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CreateWarehouseUseCaseTest {

  @Test
  void createRejectsDuplicateBusinessUnitCode() {
    var store = new StubWarehouseStore();
    store.warehouses.add(existingWarehouse("MWH.001"));

    var useCase = new CreateWarehouseUseCase(store, new StubLocationResolver());
    var warehouse = validWarehouse();
    warehouse.businessUnitCode = "MWH.001";

    assertThrows(WebApplicationException.class, () -> useCase.create(warehouse));
  }

  private Warehouse existingWarehouse(String businessUnitCode) {
    var warehouse = new Warehouse();
    warehouse.businessUnitCode = businessUnitCode;
    warehouse.location = "ZWOLLE-001";
    warehouse.capacity = 100;
    warehouse.stock = 10;
    return warehouse;
  }

  private Warehouse validWarehouse() {
    var warehouse = new Warehouse();
    warehouse.businessUnitCode = "MWH.999";
    warehouse.location = "ZWOLLE-001";
    warehouse.capacity = 100;
    warehouse.stock = 10;
    return warehouse;
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
