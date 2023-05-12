package personal.tu.fashionstore.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import personal.tu.fashionstore.dtos.Category.CategoryDto;
import personal.tu.fashionstore.dtos.Category.CreateCategoryDto;
import personal.tu.fashionstore.dtos.Category.UpdateCategoryDto;
import personal.tu.fashionstore.services.Category.ICategoryService;


@RestController
@RequestMapping("/api/category")
public class CategoryController {
    ICategoryService iCategoryService;

    public CategoryController(ICategoryService iCategoryService) {
        this.iCategoryService = iCategoryService;
    }

    /***
     *
     * @param searchText: từ khóa muốn tìm kiếm (name)
     * @param page: Số thứ tự của trang
     * @param column: Field muốn sắp xếp theo
     * @param size: Số lượng kết quả của 1 trang
     * @param sortType: sắp xếp theo:
     *                true => tăng dần,
     *                false => giảm dần
     * @return: Trả về 1 page các category dựa trên các thông tin đầu vào
     */
    @GetMapping("")
    public ResponseEntity<Page<CategoryDto>> getAllCategory(@RequestParam(defaultValue = "") String searchText,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "name") String column,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(defaultValue = "true") boolean sortType) {
        String sort = (sortType ? "asc" : "desc") ;
        return new ResponseEntity<>(iCategoryService.filter(searchText, page, size, sort, column), HttpStatus.OK);
    }

    /***
     *
     * @param id: Truyền vào id của category muốn tìm
     * @return: Trả về thông tin của category đó
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable("id") String id) {
        return new ResponseEntity<>(iCategoryService.getCategoryById(id), HttpStatus.OK);
    }

    /***
     *
     * @param createCategoryDto: Truyền vào DTO chứa thông tin
     * @return: Trả về CategoryDto chứa Category mới tạo
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CreateCategoryDto createCategoryDto) {
        return new ResponseEntity<>(iCategoryService.createCategory(createCategoryDto), HttpStatus.CREATED);
    }

    /***
     *
     * @param categoryId: Truyền vào id của category muốn cập nhật
     * @param updateCategoryDto: Truyền vào DTO chứa các thông tin
     * @return: Trả về CategoryDto chứa Category sau khi được cập nhật
     */
    @PutMapping(value = "/update/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable String categoryId,
                                                      @RequestBody UpdateCategoryDto updateCategoryDto) {
        CategoryDto updatedCategory = iCategoryService.updateCategory(categoryId, updateCategoryDto);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    /***
     *
     * @param categoryId: Truyền vào id của category muốn đổi trạng thái
     * @return: Thông báo thay đổi thành công
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/change-status/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeStatus(@PathVariable String categoryId) {

        CategoryDto categoryDto = iCategoryService.getCategoryById(categoryId);
        iCategoryService.changeStatus(categoryId);
        return new ResponseEntity<>(String.format("Category đã được thay đổi trạng thái từ %s thành %s",
                categoryDto.getIsDeleted(), !categoryDto.getIsDeleted()), HttpStatus.OK);
    }
}













