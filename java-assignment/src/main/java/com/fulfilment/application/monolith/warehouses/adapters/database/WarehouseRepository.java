package com.fulfilment.application.monolith.warehouses.adapters.database;

import java.util.List;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public List<Warehouse> getAll() {
    return this.listAll().stream()
        .filter(entity -> entity.archivedAt == null)
        .map(DbWarehouse::toWarehouse)
        .toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    var entity = DbWarehouse.fromWarehouse(warehouse);
    this.persist(entity);
  }

  @Override
  public void update(Warehouse warehouse) {
    var entity = this.find("businessUnitCode", warehouse.businessUnitCode).firstResult();
    if (entity == null) {
      throw new IllegalArgumentException("Warehouse not found for business unit code: " + warehouse.businessUnitCode);
    }

    entity.businessUnitCode = warehouse.businessUnitCode;
    entity.location = warehouse.location;
    entity.capacity = warehouse.capacity;
    entity.stock = warehouse.stock;
    entity.createdAt = warehouse.createdAt;
    entity.archivedAt = warehouse.archivedAt;
    // merge changes into persistence context
    getEntityManager().merge(entity);
  }

  @Override
  public void remove(Warehouse warehouse) {
    var entity = this.find("businessUnitCode", warehouse.businessUnitCode).firstResult();
    if (entity != null) {
      this.delete(entity);
    }
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    var entity = this.find("businessUnitCode", buCode).firstResult();
    return entity == null ? null : entity.toWarehouse();
  }

  @Override
  public Warehouse findById(String id) {
    try {
      Long numericId = Long.parseLong(id);
      var entity = this.findById(numericId);
      return entity == null ? null : entity.toWarehouse();
    } catch (NumberFormatException ex) {
      return null;
    }
  }
}
