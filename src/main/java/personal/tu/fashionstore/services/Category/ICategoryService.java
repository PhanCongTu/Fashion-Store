package personal.tu.fashionstore.services.Category;

import org.springframework.data.domain.Page;
import personal.tu.fashionstore.dtos.Category.CategoryDto;
import personal.tu.fashionstore.dtos.Category.CreateCategoryDto;
import personal.tu.fashionstore.dtos.Category.UpdateCategoryDto;

public interface ICategoryService {
    Page<CategoryDto> filter(String search, int page, int size,
                             String sort, String column);

    CategoryDto getCategoryById(String categoryId);

    CategoryDto createCategory(CreateCategoryDto categoryDto);

    CategoryDto updateCategory(String id, UpdateCategoryDto updateCategoryDto);

    void changeStatus(String categoryId);
}
