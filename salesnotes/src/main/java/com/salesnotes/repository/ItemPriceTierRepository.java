package com.salesnotes.repository;

import com.salesnotes.entity.ItemPriceTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPriceTierRepository extends JpaRepository<ItemPriceTier, String> {
    List<ItemPriceTier> findByCatalogueItemIdOrderByMinQtyAsc(String catalogueItemId);
}
