package com.example.campustrade.service.impl;

import com.example.campustrade.common.AuthContext;
import com.example.campustrade.common.BusinessException;
import com.example.campustrade.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadFile(MultipartFile file) {
        Long userId = AuthContext.getCurrentUserId();

        String originalFileName = file.getOriginalFilename();
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString().replace("-","") + suffix;
        File dir = new File("/tmp/campus-trade/images/");
        if(!dir.exists()){
            dir.mkdirs();
        }
        File dest = new File(dir,fileName);

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new BusinessException("文件存储失败");
        }

        return "/images/"+fileName;
    }
}
