package com.fulfilment.application.monolith.warehouses.domain.usecases;

import java.time.LocalDateTime;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  @Transactional
  public void create(Warehouse warehouse) {
    if (warehouse == null) {
      throw new WebApplicationException("Warehouse payload was not provided.", 422);
    }
    if (warehouse.businessUnitCode == null || warehouse.businessUnitCode.isBlank()) {
      throw new WebApplicationException("Business Unit Code was not set on request.", 422);
    }
    if (warehouse.location == null || warehouse.location.isBlank()) {
      throw new WebApplicationException("Location was not set on request.", 422);
    }
    if (warehouse.capacity == null || warehouse.capacity < 1) {
      throw new WebApplicationException("Capacity must be greater than zero.", 422);
    }
    if (warehouse.stock == null || warehouse.stock < 0) {
      throw new WebApplicationException("Stock must be zero or greater.", 422);
    }
    if (warehouse.stock > warehouse.capacity) {
      throw new WebApplicationException("Stock cannot exceed the warehouse capacity.", 422);
    }

    var existingWarehouse = warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode);
    if (existingWarehouse != null) {
      throw new WebApplicationException("Business Unit Code already exists.", 409);
    }

    var location = resolveLocation(warehouse.location);
    var activeWarehousesAtLocation = warehouseStore.getAll().stream()
        .filter(existing -> warehouse.location.equals(existing.location))
        .toList();

    if (activeWarehousesAtLocation.size() >= location.maxNumberOfWarehouses) {
      throw new WebApplicationException("Maximum number of warehouses for this location has been reached.", 422);
    }

    int totalCapacity = activeWarehousesAtLocation.stream().mapToInt(existing -> existing.capacity).sum();
    if (totalCapacity + warehouse.capacity > location.maxCapacity) {
      throw new WebApplicationException("Warehouse capacity exceeds the location maximum capacity.", 422);
    }

    warehouse.createdAt = LocalDateTime.now();
    warehouse.archivedAt = null;
    warehouseStore.create(warehouse);
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Location resolveLocation(String identifier) {
    try {
      return locationResolver.resolveByIdentifier(identifier);
    } catch (IllegalArgumentException ex) {
      throw new WebApplicationException("Location is invalid.", 422);
    }
  }
}
