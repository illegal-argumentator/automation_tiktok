package com.yves_gendron.automation_tiktok.common.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;

@Slf4j
@Component
public class FileHelper {

    public byte[] readBlob(Blob blob) {
        try (InputStream is = blob.getBinaryStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            return out.toByteArray();
        } catch (IOException | SQLException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Exception while playing video");
        }
    }

    public void validateMultipart(MultipartFile multipartFile, String fileType) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("File is not present");
        }

        String mimeType = multipartFile.getContentType();
        if (mimeType == null || !mimeType.startsWith(fileType)) {
            throw new IllegalArgumentException("Incorrect file type");
        }
    }

    public byte[] retrieveBinariesFromPhotoByUrl(String url) {
        try (InputStream inputStream = new URL(url).openStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("Error retrieving binaries from photo ", e);
            throw new RuntimeException("Error retrieving binaries from photo");
        }
    }
}
