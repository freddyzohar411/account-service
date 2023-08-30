package com.avensys.rts.accountservice.validators;

import com.avensys.rts.accountservice.annotation.FileSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

/**
 * author: Koh He Xiang
 * Custom file size validator
 */
public class FileSizeValidator implements ConstraintValidator<FileSize, MultipartFile> {
    private long fileSizeLimitInBytes;

    @Override
    public void initialize(FileSize constraintAnnotation) {
        // Convert size limit from MB to Bytes
        this.fileSizeLimitInBytes = constraintAnnotation.maxSize() * 1024 * 1024;
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            // Null or empty file is considered valid here. You can change this behavior if needed.
            return true;
        }

        // Validate the file size
        long fileSizeInBytes = file.getSize();
        return fileSizeInBytes <= fileSizeLimitInBytes;
    }
}