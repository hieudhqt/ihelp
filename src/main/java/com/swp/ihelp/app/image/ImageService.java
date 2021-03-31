package com.swp.ihelp.app.image;


import com.swp.ihelp.app.image.response.ImageResponse;

import java.util.Set;

public interface ImageService {
    Set<ImageResponse> findAll() throws Exception;

    ImageResponse findById(String imageId) throws Exception;

    ImageResponse save(ImageEntity imageEntity) throws Exception;

    void deleteById(String imageId) throws Exception;

    Set<ImageResponse> findByAuthorEmail(String email) throws Exception;

    String findAvatarByEmail(String email) throws Exception;
//    List<ImageEntity> findByEventId(String email) throws Exception;
//    List<ImageEntity> findByServiceId(String email) throws Exception;
}
