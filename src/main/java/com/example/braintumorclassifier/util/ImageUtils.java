package com.example.braintumorclassifier.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

public class ImageUtils {
    public static float[][][][] preprocess(MultipartFile file) throws Exception {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
        if (img == null) {
            throw new IllegalArgumentException("Invalid image file.");
        }

        int targetWidth = 224; // Change to the input size of your model
        int targetHeight = 224; // Change to the input size of your model

        BufferedImage resizedImg = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        resizedImg.getGraphics().drawImage(img, 0, 0, targetWidth, targetHeight, null);

        float[][][][] data = new float[1][3][targetHeight][targetWidth]; // Batch size 1, RGB channels

        float[] mean = {0.485f, 0.456f, 0.406f}; // Example mean values for ImageNet
        float[] std = {0.229f, 0.224f, 0.225f}; // Example std values for ImageNet

        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                int rgb = resizedImg.getRGB(x, y);
                for (int c = 0; c < 3; c++) {
                    float value = ((c == 0) ? ((rgb >> 16) & 0xFF) : 
                                   (c == 1) ? ((rgb >> 8) & 0xFF) : 
                                              (rgb & 0xFF)) / 255.0f;
                    data[0][c][y][x] = (value - mean[c]) / std[c]; // Normalization
                }
            }
        }

        return data;
    }
}