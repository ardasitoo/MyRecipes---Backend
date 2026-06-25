package de.htw_berlin.myrecipes;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CategoryService {

    private static final String DEFAULT_OWNER = "Simar";

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getCategories(String ownerName) {
        String owner = hasText(ownerName) ? ownerName.trim() : DEFAULT_OWNER;
        return categoryRepository.findByOwnerNameOrderByNameAsc(owner);
    }

    public Category createCategory(Category category) {
        if (!hasText(category.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kategorie braucht einen Namen");
        }

        String name = category.getName().trim();
        String owner = hasText(category.getOwnerName()) ? category.getOwnerName().trim() : DEFAULT_OWNER;

        if (categoryRepository.existsByNameIgnoreCaseAndOwnerNameIgnoreCase(name, owner)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Kategorie existiert bereits");
        }

        category.setName(name);
        category.setOwnerName(owner);
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategorie nicht gefunden");
        }

        categoryRepository.deleteById(id);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
