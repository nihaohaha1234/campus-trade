package com.example.campustrade.controller;

import com.example.campustrade.common.Result;
import com.example.campustrade.service.FileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam(name = "file") MultipartFile file){
        return Result.success(fileService.uploadFile(file));
    }
}
