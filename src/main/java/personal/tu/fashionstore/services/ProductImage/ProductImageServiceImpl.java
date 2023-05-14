package personal.tu.fashionstore.services.ProductImage;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import personal.tu.fashionstore.dtos.Product.ProductDto;
import personal.tu.fashionstore.dtos.ProductImage.ProductImageDto;
import personal.tu.fashionstore.entities.Product;
import personal.tu.fashionstore.entities.ProductImage;
import personal.tu.fashionstore.exceptions.NotFoundException;
import personal.tu.fashionstore.repositories.ProductImageRepository;
import personal.tu.fashionstore.repositories.ProductRepository;
import personal.tu.fashionstore.services.Product.IProductService;
import personal.tu.fashionstore.untils.PageUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor

public class ProductImageServiceImpl implements IProductImageService {
    private final ProductImageRepository productImageRepository;
    private final IProductService iProductService;
    private final ProductRepository productRepository;
    private ModelMapper modelMapper;

    @Override
    public Page<ProductImageDto> filter(String productId, int page, int size, String sort, String column) {
        Pageable pageable = PageUtils.createPageable(page, size, sort, column);
        Page<ProductImage> productImages;
            ProductDto productDto = iProductService.getProductById(productId);
            if (productDto == null) throw new NotFoundException("Cant find product!");
        productImages = productImageRepository.
                    findAllByProduct(modelMapper.map(productDto, Product.class), pageable);
        return productImages.map(productImage -> modelMapper.map(productImage, ProductImageDto.class));
    }

    @Override
    public ProductImageDto addProductImage(ProductImageDto productImageDto, String productId) {
        ProductImage newImage = modelMapper.map(productImageDto, ProductImage.class);
        newImage.setProduct(modelMapper.map(iProductService.getProductById(productId), Product.class));
        ProductImage savedImage = productImageRepository.save(newImage);
        iProductService.addImageIntoProduct(productId, savedImage);
        return modelMapper.map(savedImage, ProductImageDto.class);
    }

    @Override
    public void deleteProductImage(String pImage) {
        Optional<ProductImage> existingPImage = productImageRepository.findById(pImage);
        if (!existingPImage.isPresent()) throw new NotFoundException("Product Image do not exist!");
        productImageRepository.delete(existingPImage.get());
    }
}
