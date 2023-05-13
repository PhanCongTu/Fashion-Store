package personal.tu.fashionstore.controllers;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import personal.tu.fashionstore.dtos.ProductImage.ProductImageDto;
import personal.tu.fashionstore.services.ProductImage.IProductImageService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/product-image")
public class ProductImageController {
    IProductImageService iProductImageService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ProductImageDto>> getAllProductImages(@PathVariable String productId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "id") String column,
                                                                     @RequestParam(defaultValue = "12") int size,
                                                                     @RequestParam(defaultValue = "true") boolean sortType) {
        String sort = (sortType ? "asc" : "desc") ;
        return new ResponseEntity<>(iProductImageService.filter(productId, page, size, sort, column), HttpStatus.OK);
    }

    @PostMapping("/add/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductImageDto> addImage(@RequestBody ProductImageDto productImageDto,
                                                    @PathVariable("productId") String productId){
        ProductImageDto newImage = iProductImageService.addProductImage(productImageDto, productId);
        return new ResponseEntity<>(newImage, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProductImage(@PathVariable String imageId){
        iProductImageService.deleteProductImage(imageId);
        return new ResponseEntity<>("Product Image deleted successfully !!", HttpStatus.OK);
    }
}
