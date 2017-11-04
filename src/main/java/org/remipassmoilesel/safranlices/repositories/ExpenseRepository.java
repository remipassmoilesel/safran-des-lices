package org.remipassmoilesel.safranlices.repositories;

import org.remipassmoilesel.safranlices.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Deprecated
    public List<Expense> findAll();

    @Query("SELECT e FROM Expense e WHERE e.deleted= :dl")
    public List<Expense> findAll(@Param("dl") Boolean deleted);

}

