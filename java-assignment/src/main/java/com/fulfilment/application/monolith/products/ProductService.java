package com.fulfilment.application.monolith.products;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;

@ApplicationScoped
public class ProductService {

  @Inject ProductStore productStore;

  public List<Product> listAll() {
    return productStore.listAll();
  }

  public Product getSingle(Long id) {
    Product entity = productStore.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Product with id of " + id + " does not exist.", 404);
    }
    return entity;
  }

  @Transactional
  public Product create(Product product) {
    if (product.id != null) {
      throw new WebApplicationException("Id was invalidly set on request.", 422);
    }
    productStore.persist(product);
    return product;
  }

  @Transactional
  public Product update(Long id, Product product) {
    if (product.name == null) {
      throw new WebApplicationException("Product Name was not set on request.", 422);
    }
    Product entity = productStore.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Product with id of " + id + " does not exist.", 404);
    }
    entity.name = product.name;
    entity.description = product.description;
    entity.price = product.price;
    entity.stock = product.stock;
    productStore.persist(entity);
    return entity;
  }

  @Transactional
  public void delete(Long id) {
    Product entity = productStore.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Product with id of " + id + " does not exist.", 404);
    }
    productStore.delete(entity);
  }
}
