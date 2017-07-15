package org.remipassmoilesel.safranlices.repositories;

import org.remipassmoilesel.safranlices.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Deprecated
    public List<Product> findAll();

    @Query("SELECT p FROM Product p WHERE p.deleted= :dl")
    public List<Product> findAll(@Param("dl") Boolean deleted);

}

