package org.remipassmoilesel.safranlices.repositories;

import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<CommercialOrder, Long> {

}

