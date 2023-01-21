package com.example.weight_inspection.controllers;

import com.example.weight_inspection.models.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("api/image")
public class ImageController {
    private final String UPLOAD_DIRECTORY = "images/";

    public ImageController() {
    }

    @RequestMapping(path = "{imageName}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadImage(@PathVariable("imageName") String imageName) throws IOException {
        File file = new File(Paths.get(UPLOAD_DIRECTORY, imageName).toUri());
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping()
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        Files.write(Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename()), file.getBytes());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
