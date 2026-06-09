package com.example.campustrade.convert;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campustrade.vo.PageVO;

import java.util.List;

//分页转换
public class PageConvert {

    public static <T,R> PageVO<T> convert(Page<R> pageParam, List<T> records){
        PageVO<T> pageVO = new PageVO<>();
        pageVO.setPages(pageParam.getPages());
        pageVO.setSize(pageParam.getSize());
        pageVO.setTotal(pageParam.getTotal());
        pageVO.setCurrent(pageParam.getCurrent());
        pageVO.setRecords(records);
        return pageVO;
    }
}
