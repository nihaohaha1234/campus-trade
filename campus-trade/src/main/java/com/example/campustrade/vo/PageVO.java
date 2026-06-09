package com.example.campustrade.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageVO<T> {

    private Long total;//总数量

    private Long pages;//总页数

    private Long current;//当前页面

    private Long size;//每页数量

    private List<T> records;

}
