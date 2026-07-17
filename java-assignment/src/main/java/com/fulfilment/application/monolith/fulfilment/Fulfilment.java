package com.fulfilment.application.monolith.fulfilment;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;



import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "fulfilment",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"store_id", "product_id", "warehouse_business_unit_code"}))
@Cacheable
public class Fulfilment extends PanacheEntity {

  @ManyToOne
  @JoinColumn(name = "store_id", nullable = false)
  public Store store;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  public Product product;

  @Column(name = "warehouse_business_unit_code", nullable = false)
  public String warehouseBusinessUnitCode;
}
