package org.remipassmoilesel.safranlices.repositories;

import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.entities.ShippingCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShippingCostRepository extends JpaRepository<ShippingCost, Long> {

    @Deprecated
    public List<ShippingCost> findAll();

    @Query("SELECT p FROM ShippingCost p WHERE p.deleted= :dl")
    public List<ShippingCost> findAll(@Param("dl") Boolean deleted);

}

