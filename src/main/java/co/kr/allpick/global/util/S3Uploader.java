package co.kr.allpick.global.util;

import co.kr.allpick.global.config.S3Properties;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private static final Logger logger = LogManager.getLogger(S3Uploader.class);

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    /**
     * 이미지 업로드
     * @param file 업로드할 파일
     * @param folder 저장 폴더 (예: "products", "claims")
     * @return S3 이미지 URL
     */
    public String upload(MultipartFile file, String folder) {
        validateImageFile(file);

        String fileName = buildFileName(folder, file.getOriginalFilename());

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(s3Properties.getImageBucket())
                    .key(fileName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(
                    file.getInputStream(), file.getSize()));

            String url = buildUrl(fileName);
            logger.info("S3 이미지 업로드 완료 - url: {}", url);
            return url;

        } catch (IOException e) {
            logger.error("S3 이미지 업로드 실패 - fileName: {}", fileName);
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED);
        }
    }

    /**
     * 이미지 삭제
     * @param imageUrl 삭제할 이미지 URL
     */
    public void delete(String imageUrl) {
        String fileName = extractFileName(imageUrl);

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(s3Properties.getImageBucket())
                .key(fileName)
                .build();

        s3Client.deleteObject(request);
        logger.info("S3 이미지 삭제 완료 - fileName: {}", fileName);
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_FILE);
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    private String buildFileName(String folder, String originalFileName) {
        String ext = extractExtension(originalFileName);
        return folder + "/" + UUID.randomUUID() + "." + ext;
    }

    private String extractExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE);
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private String buildUrl(String fileName) {
        return "https://" + s3Properties.getImageBucket()
                + ".s3.amazonaws.com/" + fileName;
    }

    private String extractFileName(String imageUrl) {
        return imageUrl.substring(imageUrl.indexOf(".amazonaws.com/") + 15);
    }
}