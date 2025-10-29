package com.training.portal.service;

import org.springframework.web.multipart.MultipartFile;

public interface IMinioService {
    String uploadFile(MultipartFile file, String folder) throws Exception;
}
