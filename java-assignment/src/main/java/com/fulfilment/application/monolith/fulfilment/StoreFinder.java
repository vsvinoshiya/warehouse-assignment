package com.fulfilment.application.monolith.fulfilment;

import com.fulfilment.application.monolith.stores.Store;

public interface StoreFinder {

  Store findById(Long id);
}
