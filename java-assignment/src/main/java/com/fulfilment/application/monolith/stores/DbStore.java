package com.fulfilment.application.monolith.stores;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class DbStore implements StoreStore, PanacheRepository<Store> {

  @Override
  public List<Store> listAll() {
    return findAll(Sort.by("name")).list();
  }

  @Override
  public Store findById(Long id) {
    return getEntityManager().find(Store.class, id);
  }

  @Override
  public void persist(Store store) {
    getEntityManager().persist(store);
  }

  @Override
  public void delete(Store store) {
    delete("id", store.id);
  }
}
