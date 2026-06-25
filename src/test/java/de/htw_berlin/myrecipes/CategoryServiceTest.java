package de.htw_berlin.myrecipes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getCategoriesUsesDefaultOwnerWhenOwnerIsMissing() {
        when(categoryRepository.findByOwnerNameOrderByNameAsc("Simar"))
                .thenReturn(List.of(new Category("Hauptgericht", "Simar")));

        List<Category> result = categoryService.getCategories(null);

        assertThat(result).extracting(Category::getName).containsExactly("Hauptgericht");
    }

    @Test
    void createCategoryTrimsNameAndOwner() {
        when(categoryRepository.existsByNameIgnoreCaseAndOwnerNameIgnoreCase("Dessert", "Mina")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category result = categoryService.createCategory(new Category(" Dessert ", " Mina "));

        assertThat(result.getName()).isEqualTo("Dessert");
        assertThat(result.getOwnerName()).isEqualTo("Mina");
    }

    @Test
    void createCategoryRejectsDuplicateNameForSameOwner() {
        when(categoryRepository.existsByNameIgnoreCaseAndOwnerNameIgnoreCase("Dessert", "Simar")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(new Category("Dessert", "Simar")))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void deleteCategoryDeletesExistingCategory() {
        when(categoryRepository.existsById(2L)).thenReturn(true);

        categoryService.deleteCategory(2L);

        verify(categoryRepository).deleteById(2L);
    }
}
