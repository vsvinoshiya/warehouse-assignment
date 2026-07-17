package com.fulfilment.application.monolith.warehouses.domain.usecases;

import java.time.LocalDateTime;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  @Transactional
  public void replace(Warehouse newWarehouse) {
    if (newWarehouse == null) {
      throw new WebApplicationException("Warehouse payload was not provided.", 422);
    }
    if (newWarehouse.businessUnitCode == null || newWarehouse.businessUnitCode.isBlank()) {
      throw new WebApplicationException("Business Unit Code was not set on request.", 422);
    }
    if (newWarehouse.location == null || newWarehouse.location.isBlank()) {
      throw new WebApplicationException("Location was not set on request.", 422);
    }
    if (newWarehouse.capacity == null || newWarehouse.capacity < 1) {
      throw new WebApplicationException("Capacity must be greater than zero.", 422);
    }
    if (newWarehouse.stock == null || newWarehouse.stock < 0) {
      throw new WebApplicationException("Stock must be zero or greater.", 422);
    }
    if (newWarehouse.stock > newWarehouse.capacity) {
      throw new WebApplicationException("Stock cannot exceed the warehouse capacity.", 422);
    }

    var existingWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
    if (existingWarehouse == null) {
      throw new WebApplicationException("Warehouse to replace was not found.", 404);
    }

    var location = resolveLocation(newWarehouse.location);
    if (newWarehouse.capacity > location.maxCapacity) {
      throw new WebApplicationException("Warehouse capacity exceeds the location maximum capacity.", 422);
    }
    if (newWarehouse.capacity < existingWarehouse.stock) {
      throw new WebApplicationException("Warehouse capacity is too small for the stock being replaced.", 422);
    }
    if (!newWarehouse.stock.equals(existingWarehouse.stock)) {
      throw new WebApplicationException("The replacement warehouse stock does not match the warehouse being replaced.", 422);
    }

    existingWarehouse.archivedAt = LocalDateTime.now();
    // ensure the store replaces the existing record with the archived one (avoid duplicate entries in in-memory stubs)
    warehouseStore.remove(existingWarehouse);
    warehouseStore.create(existingWarehouse);

    newWarehouse.createdAt = existingWarehouse.createdAt != null ? existingWarehouse.createdAt : LocalDateTime.now();
    newWarehouse.archivedAt = null;
    warehouseStore.create(newWarehouse);
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Location resolveLocation(String identifier) {
    try {
      return locationResolver.resolveByIdentifier(identifier);
    } catch (IllegalArgumentException ex) {
      throw new WebApplicationException("Location is invalid.", 422);
    }
  }
}
