package com.example.campustrade.controller;


import com.example.campustrade.common.Result;
import com.example.campustrade.service.FavoriteService;
import com.example.campustrade.vo.PageVO;
import com.example.campustrade.vo.ProductVO;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{productId}")
    public Result<Void> addFavorite(@PathVariable Long productId){
        favoriteService.addFavorite(productId);
        return Result.success();
    }

    @DeleteMapping("/{productId}")
    public Result<Void> removeFavorite(@PathVariable Long productId){
        favoriteService.removeFavorite(productId);
        return Result.success();
    }

    @GetMapping
    public Result<PageVO<ProductVO>> getAllProducts(@RequestParam(defaultValue = "1") Long page,
                                                    @RequestParam(defaultValue = "10") Long pageSize){
        PageVO<ProductVO> productVOList = favoriteService.getAllFavorites(page,pageSize);
        return Result.success(productVOList);
    }

    @GetMapping("/{productId}/isFavorite")
    public Result<Boolean> isFavorite(@PathVariable Long productId){
        return Result.success(favoriteService.isFavorite(productId));
    }
}
