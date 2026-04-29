package co.kr.allpick.global.service;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;

    @Value("${cloud.aws.s3.image-bucket}")
    private String bucket;

    public String upload(MultipartFile file, String directory) throws IOException {
        String key = directory + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        s3Template.upload(bucket, key, file.getInputStream());
        return "https://" + bucket + ".s3.amazonaws.com/" + key;
    }

    public void delete(String imageUrl) {
        String key = imageUrl.substring(imageUrl.indexOf(".amazonaws.com/") + ".amazonaws.com/".length());
        s3Template.deleteObject(bucket, key);
    }
}
