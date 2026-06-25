package de.htw_berlin.myrecipes;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface AppUserRepository extends CrudRepository<AppUser, Long> {
    List<AppUser> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);
}
