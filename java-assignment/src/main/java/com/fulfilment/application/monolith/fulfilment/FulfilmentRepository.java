package com.fulfilment.application.monolith.fulfilment;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import jakarta.persistence.TypedQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class FulfilmentRepository implements FulfilmentStore, PanacheRepository<Fulfilment> {

  @Override
  public List<Fulfilment> getAll() {
    return listAll();
  }

  @Override
  public void create(Fulfilment fulfilment) {
    persist(fulfilment);
  }

  @Override
  public Fulfilment findById(Long id) {
    return find("id", id).firstResult();
  }

  @Override
  public void remove(Fulfilment fulfilment) {
    delete("id", fulfilment.id);
  }

  @Override
  public boolean existsAssignment(Long storeId, Long productId, String warehouseBusinessUnitCode) {
    return find(
            "store.id = ?1 and product.id = ?2 and warehouseBusinessUnitCode = ?3",
            storeId,
            productId,
            warehouseBusinessUnitCode)
        .firstResult() != null;
  }

  @Override
  public long countDistinctWarehousesForStore(Long storeId) {
    TypedQuery<Long> query =
        getEntityManager()
            .createQuery(
                "select count(distinct f.warehouseBusinessUnitCode) from Fulfilment f where f.store.id = :storeId",
                Long.class)
            .setParameter("storeId", storeId);
    return query.getSingleResult();
  }

  @Override
  public long countDistinctWarehousesForProductAndStore(Long storeId, Long productId) {
    TypedQuery<Long> query =
        getEntityManager()
            .createQuery(
                "select count(distinct f.warehouseBusinessUnitCode) from Fulfilment f where f.store.id = :storeId and f.product.id = :productId",
                Long.class)
            .setParameter("storeId", storeId)
            .setParameter("productId", productId);
    return query.getSingleResult();
  }

  @Override
  public long countDistinctProductsForWarehouse(String warehouseBusinessUnitCode) {
    TypedQuery<Long> query =
        getEntityManager()
            .createQuery(
                "select count(distinct f.product.id) from Fulfilment f where f.warehouseBusinessUnitCode = :warehouseBusinessUnitCode",
                Long.class)
            .setParameter("warehouseBusinessUnitCode", warehouseBusinessUnitCode);
    return query.getSingleResult();
  }
}
