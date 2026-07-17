package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.ws.rs.WebApplicationException;
 
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class StoreResourceTest {

  @Test
  public void createShouldRejectStoreWithIdSet() throws Exception {
    StoreResource resource = new StoreResource();

    // prepare test doubles
    final class TestStoreStore implements StoreStore {
      @Override
      public java.util.List<Store> listAll() { return java.util.Collections.emptyList(); }
      @Override
      public Store findById(Long id) { return null; }
      @Override
      public void persist(Store store) { if (store.id == null) store.id = 1L; }
      @Override
      public void delete(Store store) { }
    }

    final class TestLegacyGateway extends LegacyStoreManagerGateway {
      @Override public void createStoreOnLegacySystem(Store store) { /* no-op */ }
      @Override public void updateStoreOnLegacySystem(Store store) { /* no-op */ }
    }

    StoreService service = new StoreService();

    // inject dependencies into service
    java.lang.reflect.Field storeField = StoreService.class.getDeclaredField("storeStore");
    storeField.setAccessible(true);
    storeField.set(service, new TestStoreStore());

    java.lang.reflect.Field legacyField = StoreService.class.getDeclaredField("legacyStoreManagerGateway");
    legacyField.setAccessible(true);
    legacyField.set(service, new TestLegacyGateway());

    // inject a TransactionSynchronizationRegistry that returns null transaction key
    jakarta.transaction.TransactionSynchronizationRegistry txReg = new jakarta.transaction.TransactionSynchronizationRegistry() {
      @Override
      public Object getTransactionKey() {
        return null;
      }

      @Override
      public void registerInterposedSynchronization(jakarta.transaction.Synchronization sync) {}

      @Override
      public void setRollbackOnly() {}

      @Override
      public boolean getRollbackOnly() { return false; }

      @Override
      public int getTransactionStatus() { return 0; }

      @Override
      public void putResource(Object key, Object value) {}

      @Override
      public Object getResource(Object key) { return null; }

      // getContextKeys not present in this Jakarta version; omit
    };
    java.lang.reflect.Field txField = StoreService.class.getDeclaredField("transactionSynchronizationRegistry");
    txField.setAccessible(true);
    txField.set(service, txReg);

    // inject service into resource
    java.lang.reflect.Field serviceField = StoreResource.class.getDeclaredField("storeService");
    serviceField.setAccessible(true);
    serviceField.set(resource, service);

    Store store = new Store("InvalidStore-" + UUID.randomUUID().toString().substring(0, 8));
    store.id = 99L;

    WebApplicationException exception = assertThrows(WebApplicationException.class, () -> resource.create(store));

    assertEquals(422, exception.getResponse().getStatus());
  }

}
