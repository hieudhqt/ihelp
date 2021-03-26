package com.swp.ihelp.app.image;

import com.swp.ihelp.app.image.response.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {
    private ImageRepository imageRepository;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public List<ImageResponse> findAll() throws Exception {
        List<ImageEntity> imageEntities = imageRepository.findAll();
        return ImageResponse.convertToResponseList(imageEntities);
    }

    @Override
    public ImageResponse findById(String imageId) throws Exception {
        ImageResponse imageResponse = new ImageResponse(imageRepository.getOne(imageId));
        return imageResponse;
    }

    @Override
    public List<ImageResponse> findByAuthorEmail(String email) throws Exception {
        List<ImageEntity> imageEntities = imageRepository.findByAuthorEmail(email);
        return ImageResponse.convertToResponseList(imageEntities);
    }

    @Override
    public ImageResponse save(ImageEntity imageEntity) throws Exception {
        ImageResponse imageResponse = new ImageResponse(imageRepository.save(imageEntity));
        return imageResponse;
    }

    @Override
    public void deleteById(String imageId) throws Exception {
        imageRepository.deleteById(imageId);
    }

    @Override
    public String findAvatarByEmail(String email) throws Exception {
        return imageRepository.findAvatarByEmail(email);
    }


}
