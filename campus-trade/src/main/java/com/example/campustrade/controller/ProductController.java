package com.example.campustrade.controller;

import com.example.campustrade.common.Result;
import com.example.campustrade.dto.ProductDTO;
import com.example.campustrade.service.ProductService;
import com.example.campustrade.vo.PageVO;
import com.example.campustrade.vo.ProductVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public Result<Void> productPublish(@RequestBody @Valid ProductDTO productDTO){
        productService.productPublish(productDTO);
        return Result.success();
    }

    @GetMapping
    public Result<PageVO<ProductVO>> getAllProducts(@RequestParam(defaultValue = "1") Long page,
                                                    @RequestParam(defaultValue = "20") Long pageSize){
        return Result.success(productService.getAllProducts(page,pageSize));
    }

    @GetMapping("/recommend")
    public Result<PageVO<ProductVO>> getRecommendProducts(@RequestParam(defaultValue = "1") Long page,
                                                          @RequestParam(defaultValue = "20") Long pageSize){
        return Result.success(productService.getRecommendProducts(page, pageSize));
    }

    @GetMapping("/hot")
    public Result<List<ProductVO>> getHotProducts(){
        return Result.success(productService.getHotProducts());
    }

    @GetMapping("/my")
    public Result<PageVO<ProductVO>> getAllMyProducts(@RequestParam(required = false) Integer status,
                                                      @RequestParam(defaultValue = "1") Long page,
                                                      @RequestParam(defaultValue = "20") Long pageSize){
        return Result.success(productService.getAllMyProducts(status,page,pageSize));
    }

    @GetMapping("/my/{id}")
    public Result<ProductVO> getMyProductById(@PathVariable Long id){
        return Result.success(productService.getMyProductById(id));
    }

    @GetMapping("/search")
    public Result<PageVO<ProductVO>> searchProducts(@RequestParam(required = false) String keyWord,
                                                    @RequestParam(defaultValue = "1") Long page,
                                                    @RequestParam(defaultValue = "20") Long pageSize){
        return Result.success(productService.searchProducts(keyWord,page,pageSize));
    }

    @GetMapping("/{id}")
    public Result<ProductVO> getProductById(@PathVariable Long id){
        return Result.success(productService.getProductById(id));
    }


    @PutMapping("/{productId}/off")
    public Result<Void> productOff(@PathVariable Long productId){
        productService.productOff(productId);
        return Result.success();
    }

    @PutMapping("/{productId}")
    public Result<Void> updateProduct(@RequestBody @Valid ProductDTO productDTO,@PathVariable Long productId){
        productService.updateProduct(productDTO,productId);
        return Result.success();
    }

}
