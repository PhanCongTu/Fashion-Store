package personal.tu.fashionstore.controllers;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import personal.tu.fashionstore.dtos.ProductImage.ProductImageDto;
import personal.tu.fashionstore.services.ProductImage.IProductImageService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/product-image")
public class ProductImageController {
    IProductImageService iProductImageService;
    /***
     *
     * @param productId: ID product muốn lấy ảnh
     * @param page: Số thứ tự của trang
     * @param column: Field muốn sắp xếp theo
     * @param size: Số lượng kết quả của 1 trang
     * @param sortType: sắp xếp theo:
     *                 true => tăng dần,
     *                 false => giảm dần
     * @return: Trả về 1 page các product dựa trên các thông tin đầu vào
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ProductImageDto>> getAllProductImages(@PathVariable String productId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "id") String column,
                                                                     @RequestParam(defaultValue = "12") int size,
                                                                     @RequestParam(defaultValue = "true") boolean sortType) {
        String sort = (sortType ? "asc" : "desc") ;
        return new ResponseEntity<>(iProductImageService.filter(productId, page, size, sort, column), HttpStatus.OK);
    }

    /***
     *
     * @param productImageDto: DTO chứa thông tin hỉnh ảnh muốn tạo
     * @param productId: ID của sản phẩm muốn thêm ảnh vào
     * @return: DTO chứa thông tin của ảnh mới được tạo
     */
    @PostMapping("/add/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductImageDto> addImage(@RequestBody ProductImageDto productImageDto,
                                                    @PathVariable("productId") String productId){
        ProductImageDto newImage = iProductImageService.addProductImage(productImageDto, productId);
        return new ResponseEntity<>(newImage, HttpStatus.OK);
    }

    /***
     *
     * @param imageId: ID ảnh muốn xóa
     * @return: Thông báo thành công
     */
    @DeleteMapping(value = "/delete/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProductImage(@PathVariable String imageId){
        iProductImageService.deleteProductImage(imageId);
        return new ResponseEntity<>("Product Image deleted successfully !!", HttpStatus.OK);
    }
}
