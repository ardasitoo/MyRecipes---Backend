package de.htw_berlin.myrecipes;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findByOwnerNameOrderByNameAsc(String ownerName);

    boolean existsByNameIgnoreCaseAndOwnerNameIgnoreCase(String name, String ownerName);
}
