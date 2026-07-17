package com.fulfilment.application.monolith.fulfilment;

import com.fulfilment.application.monolith.stores.Store;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DefaultStoreFinder implements StoreFinder {

  @Override
  public Store findById(Long id) {
    return Store.findById(id);
  }
}
