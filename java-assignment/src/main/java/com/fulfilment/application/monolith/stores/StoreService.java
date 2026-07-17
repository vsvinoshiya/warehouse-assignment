package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;

@ApplicationScoped
public class StoreService {

  @Inject StoreStore storeStore;

  @Inject LegacyStoreManagerGateway legacyStoreManagerGateway;

  @Inject TransactionSynchronizationRegistry transactionSynchronizationRegistry;

  public List<Store> listAll() {
    return storeStore.listAll();
  }

  public Store getSingle(Long id) {
    Store entity = storeStore.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
    }
    return entity;
  }

  @Transactional
  public Store create(Store store) {
    if (store.id != null) {
      throw new WebApplicationException("Id was invalidly set on request.", 422);
    }
    storeStore.persist(store);

    runAfterCommit(() -> legacyStoreManagerGateway.createStoreOnLegacySystem(store));

    return store;
  }

  @Transactional
  public Store update(Long id, Store updatedStore) {
    if (updatedStore.name == null) {
      throw new WebApplicationException("Store Name was not set on request.", 422);
    }
    Store entity = storeStore.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
    }
    entity.name = updatedStore.name;
    entity.quantityProductsInStock = updatedStore.quantityProductsInStock;

    runAfterCommit(() -> legacyStoreManagerGateway.updateStoreOnLegacySystem(entity));

    storeStore.persist(entity);
    return entity;
  }

  @Transactional
  public Store patch(Long id, Store updatedStore) {
    if (updatedStore.name == null) {
      throw new WebApplicationException("Store Name was not set on request.", 422);
    }
    Store entity = storeStore.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
    }

    if (updatedStore.name != null) {
      entity.name = updatedStore.name;
    }

    if (updatedStore.quantityProductsInStock != 0) {
      entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
    }

    runAfterCommit(() -> legacyStoreManagerGateway.updateStoreOnLegacySystem(entity));

    storeStore.persist(entity);
    return entity;
  }

  @Transactional
  public void delete(Long id) {
    Store entity = storeStore.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
    }
    storeStore.delete(entity);
  }

  private void runAfterCommit(Runnable action) {
    if (transactionSynchronizationRegistry.getTransactionKey() == null) {
      action.run();
      return;
    }

    transactionSynchronizationRegistry.registerInterposedSynchronization(
        new Synchronization() {
          @Override
          public void beforeCompletion() {}

          @Override
          public void afterCompletion(int status) {
            if (status == Status.STATUS_COMMITTED) {
              action.run();
            }
          }
        });
  }
}
