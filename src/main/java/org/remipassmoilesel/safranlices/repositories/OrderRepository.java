package org.remipassmoilesel.safranlices.repositories;

import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<CommercialOrder, Long> {

    @Query("SELECT c FROM CommercialOrder c WHERE c.processed = :pr ORDER BY date DESC")
    public Page<CommercialOrder> findLasts(Pageable pageable,
                                           @Param("pr") boolean processed);

}

