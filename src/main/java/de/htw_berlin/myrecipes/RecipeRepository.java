package de.htw_berlin.myrecipes;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {
    List<Recipe> findByOwnerNameOrderByIdAsc(String ownerName);
}
