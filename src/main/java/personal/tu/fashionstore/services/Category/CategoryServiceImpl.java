package personal.tu.fashionstore.services.Category;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import personal.tu.fashionstore.dtos.Category.CategoryDto;
import personal.tu.fashionstore.dtos.Category.CreateCategoryDto;
import personal.tu.fashionstore.dtos.Category.UpdateCategoryDto;
import personal.tu.fashionstore.entities.Category;
import personal.tu.fashionstore.exceptions.NotFoundException;
import personal.tu.fashionstore.repositories.CategoryRepository;
import personal.tu.fashionstore.untils.PageUtils;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private ModelMapper modelMapper;

    
    @Override
    public Page<CategoryDto> filter(String search, int page, int size,
                                    String sort, String column) {
        Pageable pageable = PageUtils.createPageable(page, size, sort, column);
        Page<Category> categories = categoryRepository.findByNameContainingIgnoreCase(search, pageable);
        return categories.map(category -> modelMapper.map(category, CategoryDto.class));
    }

    
    @Override
    public CategoryDto getCategoryById(String categoryId) {
        Optional<Category> categoryOp = categoryRepository.findById(categoryId);
        if (!categoryOp.isPresent())
            throw new NotFoundException("Cant find category!");
        return modelMapper.map(categoryOp.get(), CategoryDto.class);
    }

    
    @Override
    public CategoryDto createCategory(CreateCategoryDto categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);
        if (category.getIsDeleted() == null) category.setIsDeleted(false);
        return modelMapper.map(categoryRepository.save(category), CategoryDto.class);
    }

    // Cập nhật lại category (cập nhật lại toàn bộ các thuộc tính)
    
    @Override
    public CategoryDto updateCategory(String id, UpdateCategoryDto updateCategoryDto) {
        Category existingCategory = categoryRepository.findById(id).orElse(null);
        if (existingCategory == null) throw new NotFoundException("Unable to update category!");

        modelMapper.map(updateCategoryDto, existingCategory);
        existingCategory.setUpdateAt(new Date(new java.util.Date().getTime()));

        return modelMapper.map(categoryRepository.save(existingCategory), CategoryDto.class);

    }

    // Hàm deleteCategory chỉ delete bằng cách set thuộc tính IsDeleted = true chứ không xoá hẳn trong database
    
    @Override
    public void changeStatus(String categoryId) {
        Optional<Category> existingCategory = categoryRepository.findById(categoryId);
        if (!existingCategory.isPresent()) throw new NotFoundException("Unable to dalete category!");

        existingCategory.get().setIsDeleted(!existingCategory.get().getIsDeleted());
        existingCategory.get().setUpdateAt(new Date(new java.util.Date().getTime()));
        categoryRepository.save(existingCategory.get());
    }
}
