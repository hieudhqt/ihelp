package com.swp.ihelp.app.image;


import com.swp.ihelp.app.image.response.ImageResponse;

import java.util.List;

public interface ImageService {
    List<ImageResponse> findAll() throws Exception;

    ImageResponse findById(String imageId) throws Exception;

    ImageResponse save(ImageEntity imageEntity) throws Exception;

    void deleteById(String imageId) throws Exception;

    List<ImageResponse> findByAuthorEmail(String email) throws Exception;
//    List<ImageEntity> findByEventId(String email) throws Exception;
//    List<ImageEntity> findByServiceId(String email) throws Exception;
}
