package com.fulfilment.application.monolith.products;

import java.util.List;

public interface ProductStore {

  List<Product> listAll();

  Product findById(Long id);

  void persist(Product product);

  void delete(Product product);
}
