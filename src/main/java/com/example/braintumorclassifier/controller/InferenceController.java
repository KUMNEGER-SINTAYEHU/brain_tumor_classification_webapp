package com.example.braintumorclassifier.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.braintumorclassifier.service.ModelService;
import com.example.braintumorclassifier.util.ImageUtils;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class InferenceController {

    private static final Logger logger = LoggerFactory.getLogger(InferenceController.class);

    @Autowired
    private ModelService modelService;

    @PostMapping("/predict")
    public ResponseEntity<Map<String, Object>> predict(@RequestParam("image") MultipartFile image) {
        try {
            float[][][][] input = ImageUtils.preprocess(image);
            Map<String, Float> result = modelService.predict(input);
            Map<String, Object> response = new HashMap<>();
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Prediction failed", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Prediction error: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}