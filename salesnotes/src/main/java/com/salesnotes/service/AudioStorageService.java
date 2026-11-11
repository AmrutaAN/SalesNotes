package com.salesnotes.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class AudioStorageService {

    private final Path rootPath = Paths.get("./data/uploads/audio").toAbsolutePath().normalize();

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "m4a", "aac", "mp3", "wav", "ogg", "weba", "webm"
    );

    private static final long MAX_FILE_SIZE = 30 * 1024 * 1024; // 30 MB

    public AudioStorageService() {
        try {
            Files.createDirectories(this.rootPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage folder for audio files", e);
        }
    }

    public StorageResult store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty audio file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Audio file exceeds maximum allowed limit of 30MB");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Unsupported audio format: " + extension + ". Allowed formats: " + ALLOWED_EXTENSIONS);
        }

        String uniqueFileName = UUID.randomUUID().toString() + "." + extension.toLowerCase();
        Path targetPath = this.rootPath.resolve(uniqueFileName);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store audio file", e);
        }

        String mimeType = file.getContentType() != null ? file.getContentType() : "audio/" + extension.toLowerCase();
        String publicUrl = "/api/v1/audio/stream/" + uniqueFileName;

        return new StorageResult(
                originalFilename != null ? originalFilename : uniqueFileName,
                uniqueFileName,
                publicUrl,
                mimeType,
                file.getSize()
        );
    }

    public Resource loadAsResource(String fileName) {
        try {
            Path file = rootPath.resolve(fileName).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new IllegalArgumentException("Audio file not found: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed path for audio file: " + fileName, e);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "m4a";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public static class StorageResult {
        private final String originalFileName;
        private final String storageKey;
        private final String publicUrl;
        private final String mimeType;
        private final long fileSizeBytes;

        public StorageResult(String originalFileName, String storageKey, String publicUrl, String mimeType, long fileSizeBytes) {
            this.originalFileName = originalFileName;
            this.storageKey = storageKey;
            this.publicUrl = publicUrl;
            this.mimeType = mimeType;
            this.fileSizeBytes = fileSizeBytes;
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public String getStorageKey() {
            return storageKey;
        }

        public String getPublicUrl() {
            return publicUrl;
        }

        public String getMimeType() {
            return mimeType;
        }

        public long getFileSizeBytes() {
            return fileSizeBytes;
        }
    }
}
