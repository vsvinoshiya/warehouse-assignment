package com.fulfilment.application.monolith.stores;

import java.util.List;

public interface StoreStore {

  List<Store> listAll();

  Store findById(Long id);

  void persist(Store store);

  void delete(Store store);

}
