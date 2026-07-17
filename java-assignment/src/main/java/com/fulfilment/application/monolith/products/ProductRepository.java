package com.fulfilment.application.monolith.products;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductRepository implements ProductStore, PanacheRepository<Product> {

	@Override
	public List<Product> listAll() {
		return findAll(Sort.by("name")).list();
	}

	@Override
	public Product findById(Long id) {
		return getEntityManager().find(Product.class, id);
	}

	@Override
	public void persist(Product product) {
		getEntityManager().persist(product);
	}

	@Override
	public void delete(Product product) {
		delete("id", product.id);
	}
}
