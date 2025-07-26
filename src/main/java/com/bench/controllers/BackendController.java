package com.bench.controllers;

import com.bench.services.HtmlBuilderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

import static com.bench.consts.ConstantParams.*;

@RestController
@RequestMapping(BACKEND_ROOT)
public class BackendController {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BackendController.class);

    private final HtmlBuilderService builderService;
    private final ObjectMapper objectMapper;

    public BackendController(HtmlBuilderService builderService, ObjectMapper objectMapper) {
        this.builderService = builderService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(PING)
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Controller is working!");
    }

    @PostMapping(CONVERT)
    public ResponseEntity<String> convertJsonFile(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            LOGGER.info("file received: [{}]", file.getOriginalFilename());
            try (InputStream is = file.getInputStream()) {
                Map<String, Object> json = objectMapper.readValue(is, Map.class);
                String html = builderService.convertJsonToHtml(json);

                File outputDir = new File("outputHtmlFiles");
                if (!outputDir.exists()) {
                    outputDir.mkdir();
                }

                String filename = Objects
                        .requireNonNull(file.getOriginalFilename())
                        .replaceAll("\\.json$", "") + ".html";

                File outputFile = new File(outputDir, filename);
                try (FileWriter writer = new FileWriter(outputFile)) {
                    writer.write(html);
                    LOGGER.info("HTML written to file: {}", outputFile.getAbsolutePath());
                }

                return ResponseEntity.ok("HTML saved to: " + outputFile.getAbsolutePath());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Invalid JSON: " + e.getMessage());
            }
        } else {
            LOGGER.info("file is empty");
            return ResponseEntity.badRequest().body("empty file");
        }
    }
}
