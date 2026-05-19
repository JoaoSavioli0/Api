package com.condolives.api.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    List<String> uploadImages(List<MultipartFile> files);
    String generateSignedDownloadUrl(String storedUrl);
}
