package ua.in.photomap.photoapi.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;
import ua.in.photomap.photoapi.model.ImageFile;
import ua.in.photomap.photoapi.model.ImageFileType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public final class FileUtils {
    public static Optional<ImageFile> getImageFileByType(Set<ImageFile> files, ImageFileType fileType) {
        if (files == null || files.isEmpty()) {
            return Optional.empty();
        }
        return files.stream()
                .filter(file -> file.getFileType().equals(fileType))
                .findFirst();
    }

    public File convertMultiPartToFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new InternalError("File name is null");
        }
        fileName = file.getOriginalFilename().replace("/", ":");

        File convFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
            return convFile;
        } catch (IOException e) {
            throw new InternalError("Error while converting multipart file to file");
        }
    }

    public List<File> convertMultiPartFilesToFiles(List<MultipartFile> files) {
        return files.stream()
                .map(FileUtils::convertMultiPartToFile)
                .collect(Collectors.toList());
    }
}
