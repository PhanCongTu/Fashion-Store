package personal.tu.fashionstore.services.Product;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import personal.tu.fashionstore.dtos.Category.CategoryDto;
import personal.tu.fashionstore.dtos.Product.CreateProductDto;
import personal.tu.fashionstore.dtos.Product.ProductDto;
import personal.tu.fashionstore.dtos.Product.UpdateProductDto;
import personal.tu.fashionstore.entities.Category;
import personal.tu.fashionstore.entities.Product;
import personal.tu.fashionstore.entities.ProductImage;
import personal.tu.fashionstore.exceptions.InvalidException;
import personal.tu.fashionstore.exceptions.NotFoundException;
import personal.tu.fashionstore.repositories.ProductRepository;
import personal.tu.fashionstore.services.Category.ICategoryService;
import personal.tu.fashionstore.untils.PageUtils;

import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;
    private final ICategoryService iCategoryService;
    private ModelMapper modelMapper;

    @Override
    public Page<ProductDto> filter(String searchText, String categoryId, int page, int size,
                                   String sort, String column) {
        Pageable pageable = PageUtils.createPageable(page, size, sort, column);
        Page<Product> products;
        if (Objects.equals(categoryId, "0")) {
            products = productRepository.findByProductNameContainingIgnoreCaseAndIsActive(searchText, true, pageable);
        } else {
            CategoryDto categoryDto = iCategoryService.getCategoryById(categoryId);
            if (categoryDto == null)
                throw new NotFoundException("Cant find category!");
            products = productRepository.
                    findByProductNameContainingAndCategoryAllIgnoreCaseAndIsActive(searchText, modelMapper.map(categoryDto, Category.class), true, pageable);
        }
        return products.map(product -> modelMapper.map(product, ProductDto.class));
    }

    @Override
    public ProductDto getProductById(String ProductId) {
        Optional<Product> ProductOp = productRepository.findById(ProductId);
        if (!ProductOp.isPresent())
            throw new NotFoundException("Cant find Product!");
        return modelMapper.map(ProductOp.get(), ProductDto.class);
    }

    @Override
    public ProductDto createProduct(CreateProductDto productDto) {
        CategoryDto categoryDto = iCategoryService.getCategoryById(productDto.getCategory().getId());
        if (categoryDto.getIsDeleted()) {
            throw new InvalidException("Category has been deleted !");
        }
        productDto.setCategory(categoryDto);
        return modelMapper.map(productRepository.save(modelMapper.map(productDto, Product.class)), ProductDto.class);
    }


    // Cập nhật lại Product (Cập nhật lại toàn bộ các thuộc tính)
    @Override
    public ProductDto updateProduct(String id, UpdateProductDto updateProductDto) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct == null) throw new NotFoundException("Can not found product !Unable to update Product!");

//        modelMapper.map(updateProductDto, existingProduct);
        existingProduct.setProductName(updateProductDto.getProductName());
        existingProduct.setDescription(updateProductDto.getDescription());
        existingProduct.setQuantity(updateProductDto.getQuantity());
        existingProduct.setPrice(updateProductDto.getPrice());
        CategoryDto newCategoryDto = iCategoryService.getCategoryById(updateProductDto.getCategory().getId());
        Category newCategory = modelMapper.map(newCategoryDto, Category.class);
        existingProduct.setCategory(newCategory);

        existingProduct.setUpdateAt(new Date(new java.util.Date().getTime()));
        Product updatedProduct = productRepository.save(existingProduct);
        return modelMapper.map(updatedProduct, ProductDto.class);

    }

    @Override
    public void changeStatus(String ProductId) {
        Optional<Product> existingProduct = productRepository.findById(ProductId);
        if (!existingProduct.isPresent()) throw new NotFoundException("Unable to dalete Product!");

        existingProduct.get().setIsActive(!existingProduct.get().getIsActive());
        existingProduct.get().setUpdateAt(new Date(new java.util.Date().getTime()));
        productRepository.save(existingProduct.get());
    }

    @Override
    public List<ProductDto> getTop8ProductBySold() {
        List<Product> Products = productRepository.findTop8ByOrderBySoldDesc();
        List<ProductDto> ProductDtos = new ArrayList<>();
        for (Product Product : Products
        ) {
            ProductDto ProductDto = modelMapper.map(Product, ProductDto.class);
            ProductDtos.add(ProductDto);
        }

        return ProductDtos;
    }


    @Override
    public List<ProductDto> getTop8NewProducts() {
        List<Product> Products = productRepository.findTop8ByOrderByCreateAtDesc();
        List<ProductDto> ProductDtos = new ArrayList<>();
        for (Product Product : Products
        ) {
            ProductDto ProductDto = modelMapper.map(Product, ProductDto.class);
            ProductDtos.add(ProductDto);
        }

        return ProductDtos;
    }

    @Override
    public List<ProductDto> getProductByCategoryId(String category) {
        List<Product> products = productRepository.findByCategoryId(category);
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = modelMapper.map(product, ProductDto.class);
            productDtos.add(productDto);
        }
        if (productDtos.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "Do not find any product");
        }
        return productDtos;
    }

    @Override
    public void addImageIntoProduct(String productId, ProductImage productImage) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            if (product.getImages() == null) {
                List<ProductImage> images = new ArrayList<>();
                images.add(productImage);
                product.setImages(images);
            } else {
                product.getImages().add(productImage);
            }
            productRepository.save(product);
        }
    }
}
