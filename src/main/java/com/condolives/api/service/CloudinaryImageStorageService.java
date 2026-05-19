package com.condolives.api.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.condolives.api.exception.ServiceException;

import jakarta.annotation.PostConstruct;

@Service
public class CloudinaryImageStorageService implements ImageStorageService {

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    private Cloudinary cloudinary;

    @PostConstruct
    void init() {
        if (!cloudName.isEmpty() && !apiKey.isEmpty() && !apiSecret.isEmpty()) {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret,
                    "secure", true));
        }
    }

    @Override
    public List<String> uploadImages(List<MultipartFile> files) {
        if (cloudinary == null) {
            throw new ServiceException("Cloudinary não configurado: defina CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY e CLOUDINARY_API_SECRET", 500);
        }
        return files.stream().map(this::uploadOne).toList();
    }

    @Override
    public String generateSignedDownloadUrl(String storedUrl) {
        if (cloudinary == null) {
            throw new ServiceException("Cloudinary não configurado", 500);
        }
        // URL format: https://res.cloudinary.com/{cloud}/image/upload/v{version}/{publicId}.{ext}
        int uploadIdx = storedUrl.indexOf("/upload/");
        if (uploadIdx < 0) return storedUrl;

        String afterUpload = storedUrl.substring(uploadIdx + 8);

        // Extract version number and publicId separately
        // The SDK needs the exact version used at upload time to produce a valid signature
        Long version = null;
        String publicId = afterUpload;

        if (afterUpload.startsWith("v")) {
            int slashIdx = afterUpload.indexOf('/');
            if (slashIdx > 1) {
                try {
                    version = Long.parseLong(afterUpload.substring(1, slashIdx));
                    publicId = afterUpload.substring(slashIdx + 1);
                } catch (NumberFormatException ignored) {}
            }
        }

        com.cloudinary.Url url = cloudinary.url()
                .transformation(new Transformation().flags("attachment"))
                .signed(true);

        if (version != null) {
            url = url.version(version);
        }

        return url.generate(publicId);
    }

    @SuppressWarnings("unchecked")
    private String uploadOne(MultipartFile file) {
        try {
            Map<String, Object> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "condolives/tickets", "resource_type", "image"));
            return (String) result.get("secure_url");
        } catch (Exception e) {
            throw new ServiceException("Falha ao enviar imagem: " + e.getMessage(), 500);
        }
    }
}
