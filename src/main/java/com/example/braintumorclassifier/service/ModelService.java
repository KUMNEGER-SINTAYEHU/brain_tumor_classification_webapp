package com.example.braintumorclassifier.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

@Service
public class ModelService {

    private static final Logger logger = LoggerFactory.getLogger(ModelService.class);

    private final OrtEnvironment environment;
    private final OrtSession session;

    public ModelService() throws OrtException {
        try {
            this.environment = OrtEnvironment.getEnvironment();
            
            
            String resourcePath = "/models/brain_tumor_model1.onnx";
            InputStream inputStream = ModelService.class.getResourceAsStream(resourcePath);
            if (inputStream == null) {
                logger.error("Failed to find model file at path: " + resourcePath);
                throw new RuntimeException("Model file not found in resources");
            }

            this.session = environment.createSession(inputStream.readAllBytes(), new OrtSession.SessionOptions());
            logger.info("Model loaded successfully.");
        } catch (IOException e) {
            logger.error("Error loading model from resources", e);
            throw new RuntimeException("Failed to load model from resources", e);
        }
    }

    public Map<String, Float> predict(float[][][][] input) throws OrtException {
        OnnxTensor tensor = OnnxTensor.createTensor(environment, input);
        logger.info("Input tensor shape: " + Arrays.toString(tensor.getInfo().getShape()));

        OrtSession.Result results = session.run(Map.of("input", tensor));

        float[][] output = (float[][]) results.get(0).getValue();
        logger.info("Raw model output: " + Arrays.deepToString(output));

        float[] probabilities = softmax(output[0]);
        logger.info("Softmax results: " + Arrays.toString(probabilities));

        String[] labels = {"Glioma", "Meningioma", "No Tumor", "Pituitary"};
        Map<String, Float> resultMap = new HashMap<>();

        for (int i = 0; i < labels.length; i++) {
            resultMap.put(labels[i], probabilities[i] * 100);
        }

        return resultMap;
    }

    private float[] softmax(float[] logits) {
        float sum = 0f;
        for (float logit : logits) {
            sum += Math.exp(logit);
        }

        float[] probabilities = new float[logits.length];
        for (int i = 0; i < logits.length; i++) {
            probabilities[i] = (float) Math.exp(logits[i]) / sum;
        }

        return probabilities;
    }
}
