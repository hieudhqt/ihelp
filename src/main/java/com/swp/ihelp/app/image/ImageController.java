package com.swp.ihelp.app.image;

import com.swp.ihelp.app.image.response.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ImageController {
    private ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/images")
    public List<ImageResponse> findAll() throws Exception {
        return imageService.findAll();
    }

    @GetMapping("/images/{imageId}")
    public ImageResponse findById(@PathVariable String imageId) throws Exception {
        return imageService.findById(imageId);
    }

    @GetMapping("/images/{email}")
    public List<ImageResponse> findByAuthorEmail(@PathVariable String email) throws Exception {
        return imageService.findByAuthorEmail(email);
    }

    @PostMapping("/images")
    public ImageResponse saveImage(@RequestBody ImageEntity imageEntity) throws Exception {
        return imageService.save(imageEntity);
    }

    @PutMapping("/images")
    public ImageResponse updateImage(@RequestBody ImageEntity imageEntity) throws Exception {
        return imageService.save(imageEntity);
    }

    @DeleteMapping("/images/{imageId}")
    public String deleteImage(@PathVariable String imageId) throws Exception {
        imageService.deleteById(imageId);
        return "Image with ID: " + imageId + " deleted.";
    }
}
