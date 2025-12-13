package com.assignment.products.service;

import com.assignment.products.entity.Product;
import com.assignment.products.mapper.ProductMapper;
import com.assignment.products.model.ProductRequestDTO;
import com.assignment.products.model.ProductUpdateRequestDTO;
import com.assignment.products.model.ProductsResponseDTO;
import com.assignment.products.repository.ProductSpecification;
import com.assignment.products.repository.ProductsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock private ProductsRepository productsRepository;
    @Mock private ProductMapper productMapper;
    @Mock private ProductSpecification productSpecification;

    @Mock private SecurityContext securityContext;

    @InjectMocks
    private ProductsService productService;

    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;

    @BeforeEach
    void setUp() {
        mockedSecurityContextHolder = Mockito.mockStatic(SecurityContextHolder.class);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityContextHolder.close();
    }

    @Test
    void createProducts_ShouldSaveAndReturnDTOs() {
        ProductRequestDTO requestDTO = new ProductRequestDTO("Phone", "Desc", BigDecimal.valueOf(100), 10);
        Product product = Product.builder()
                .name("Phone")
                .description("Desc")
                .quantity(10)
                .price(BigDecimal.valueOf(100)).build();
        ProductsResponseDTO responseDTO = new ProductsResponseDTO();

        when(productMapper.convertFromDTO(any())).thenReturn(product);
        when(productsRepository.saveAllAndFlush(anyList())).thenReturn(List.of(product));
        when(productMapper.convertToDTO(any())).thenReturn(responseDTO);

        List<ProductsResponseDTO> result = productService.createProducts(List.of(requestDTO));

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productsRepository).saveAllAndFlush(anyList());
    }

    @Test
    void updateProducts_ShouldSaveAndReturnDTOs_whenProductExists() {
        long productId = 1L;
        ProductUpdateRequestDTO updateRequestDTO = new ProductUpdateRequestDTO();
        updateRequestDTO.setName("New Name");
        updateRequestDTO.setQuantity(10);

        Product existingProduct = Product.builder()
                .name("name")
                .description("desc")
                .quantity(1)
                .price(BigDecimal.valueOf(100)).build();

        when(productsRepository.findById(any())).thenReturn(Optional.ofNullable((existingProduct)));
        when(productsRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productMapper.convertToDTO(any())).thenReturn(new ProductsResponseDTO());

        productService.updateProducts(productId, updateRequestDTO);

        assertEquals("New Name", existingProduct.getName());
        assertEquals(10, existingProduct.getQuantity());
        verify(productsRepository).save(existingProduct);
    }

    @Test
    void updateProducts_ShouldSaveAndReturnDTOs_whenProductDoesNotExist() {
        when(productsRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                productService.updateProducts(99L, new ProductUpdateRequestDTO())
        );
    }

    @Test
    void reduceProductStock_ShouldReduceQuantityAndSave() {
        Product product = new Product();
        product.setQuantity(10);
        int reductionAmount = 2;

        productService.reduceProductStock(product, reductionAmount);

        assertEquals(8, product.getQuantity());
        verify(productsRepository).save(product);
    }

    @Test
    void findProductById_ShouldReturnProduct_WhenExists() {
        Product product = new Product();
        when(productsRepository.findById(1L)).thenReturn(Optional.of(product));
        Product result = productService.findProductById(1L);
        assertNotNull(result);
    }

    @Test
    void findAllProducts_ShouldCallRepositoryWithSpec() {

        when(securityContext.getAuthentication()).thenReturn(Mockito.mock(Authentication.class));
        when(productSpecification.filterProducts(
                isNull(), isNull(), isNull(), eq(false), anyBoolean())
        ).thenReturn(Mockito.mock(Specification.class));
        when(productsRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(new Product()));
        when(productMapper.convertToDTO(any())).thenReturn(new ProductsResponseDTO());

        productService.findAllProducts();

        verify(productSpecification, times(1)).filterProducts(any(), any(), any(), anyBoolean(), anyBoolean());
    }

}
