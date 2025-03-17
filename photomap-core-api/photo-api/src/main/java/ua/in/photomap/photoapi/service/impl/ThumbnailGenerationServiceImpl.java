package ua.in.photomap.photoapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.in.photomap.photoapi.service.ThumbnailGenerationService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static ua.in.photomap.photoapi.constant.ThumbDimensions.WIDTH_IN_PIXELS;

@Service
@RequiredArgsConstructor
public class ThumbnailGenerationServiceImpl implements ThumbnailGenerationService {

    @Override
    public byte[] generate(byte[] fileContent) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileContent)) {
            BufferedImage sourceImg = ImageIO.read(byteArrayInputStream);

            int originalWidth = sourceImg.getWidth();
            int originalHeight = sourceImg.getHeight();
            int targetWidth = WIDTH_IN_PIXELS.getValue();
            int targetHeight = WIDTH_IN_PIXELS.getValue();

            // Calculate the scaling factor to maintain aspect ratio
            double scaleFactor = Math.max(1d * targetWidth / originalWidth, 1d * targetHeight / originalHeight);
            int scaledWidth = Math.max((int) (originalWidth * scaleFactor), targetWidth);
            int scaledHeight = Math.max((int) (originalHeight * scaleFactor), targetHeight);

            BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.drawImage(sourceImg, 0, 0, scaledWidth, scaledHeight, null);
            g2d.dispose();

            int x = (scaledWidth - targetWidth) / 2;
            int y = (scaledHeight - targetHeight) / 2;
            BufferedImage croppedImage = scaledImage.getSubimage(x, y, targetWidth, targetHeight);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(croppedImage, "jpg", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create thumbnail", e);
        }
    }
}

