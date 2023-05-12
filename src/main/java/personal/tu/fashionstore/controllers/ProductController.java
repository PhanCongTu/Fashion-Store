package personal.tu.fashionstore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import personal.tu.fashionstore.dtos.Product.CreateProductDto;
import personal.tu.fashionstore.dtos.Product.ProductDto;
import personal.tu.fashionstore.dtos.Product.UpdateProductDto;
import personal.tu.fashionstore.services.Category.ICategoryService;
import personal.tu.fashionstore.services.Product.IProductService;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    IProductService iProductService;

    ICategoryService iCategoryService;

    public ProductController(IProductService iProductService, ICategoryService iCategoryService) {
        this.iProductService = iProductService;
        this.iCategoryService = iCategoryService;
    }

    /***
     *
     * @param searchText: từ khóa muốn tìm kiếm (productName)
     * @param categoryId: ID category của sản phẩm
     * @param page: Số thứ tự của trang
     * @param column: Field muốn sắp xếp theo
     * @param size: Số lượng kết quả của 1 trang
     * @param sortType: sắp xếp theo:
     *                 true => tăng dần,
     *                 false => giảm dần
     * @return: Trả về 1 page các product dựa trên các thông tin đầu vào
     */
    @GetMapping("")
    public ResponseEntity<Page<ProductDto>> getAllProducts(@RequestParam(defaultValue = "") String searchText,
                                                           @RequestParam(defaultValue = "0") String categoryId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "price") String column,
                                                           @RequestParam(defaultValue = "12") int size,
                                                           @RequestParam(defaultValue = "true") boolean sortType
    ) {
        String sort = (sortType ? "asc" : "desc") ;
        return new ResponseEntity<>(iProductService.filter(searchText,categoryId, page, size, sort, column), HttpStatus.OK);
    }

    /***
     *
     * @return: Trả về 8 sản phẩm bán chạy nhất
     */
    @GetMapping("/best-selling")
    public ResponseEntity<List<ProductDto>> getTop8BestSelling() {
        List<ProductDto> listProduct = iProductService.getTop8ProductBySold();
        if (listProduct.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(listProduct, HttpStatus.OK);
    }

    /***
     *
     * @return: Trả về 8 sản phẩm mới nhập về
     */
    @GetMapping("/new")
    public ResponseEntity<List<ProductDto>> getTop8New() {
        List<ProductDto> listProduct = iProductService.getTop8NewProducts();
        if (listProduct.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(listProduct, HttpStatus.OK);
    }

    /***
     *
     * @param id: Truyền vào id của sản phẩm muốn tìm
     * @return: Trả về thông tin của sản phẩm đó
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") String id) {
        ProductDto Product = iProductService.getProductById(id);
        return new ResponseEntity<>(Product, HttpStatus.OK);
    }

    /***
     *
     * @param productDto: Truyền vào thông tin của sản phẩm muốn tạo mới
     * @return: DTO chứa thôn tin của sản phẩm mới tạo
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> createProduct(@RequestBody CreateProductDto productDto) {
        return new ResponseEntity<>(iProductService.createProduct(productDto), HttpStatus.CREATED);
    }

    /***
     *
     * @param ProductId: Truyền vào id của sản phẩm muốn cập nhật
     * @param productDto: Thông tin mới của sản phẩm đó
     * @return: DTO chứa thông tin của sản phẩm sau khi cập nhật
     */
    @PutMapping(value = "/update/{ProductId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable("ProductId") String ProductId,
                                                    @RequestBody UpdateProductDto productDto) {
        return new ResponseEntity<>(iProductService.updateProduct(ProductId, productDto), HttpStatus.OK);
    }

    /***
     *
     * @param ProductId: Truyền vào id của sản phẩm muốn đổi trạng thái
     * @return: Trả về thông báo thành công
     */
    @PutMapping(value = "/change-status/{ProductId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeStatus(@PathVariable("ProductId") String ProductId) {
        ProductDto productDto = iProductService.getProductById(ProductId);
        iProductService.changeStatus(ProductId);
        return new ResponseEntity<>(String.format("Product đã được thay đổi trạng thái từ %s thành %s",
                productDto.getIsActive(), !productDto.getIsActive()), HttpStatus.OK);
    }
}
