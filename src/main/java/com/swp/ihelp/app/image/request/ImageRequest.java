package com.swp.ihelp.app.image.request;

import com.swp.ihelp.app.image.ImageEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageRequest {
    private String url;
    private String type;

    public static ImageEntity convertRequestToEntity(ImageRequest request) {
        return new ImageEntity()
                .setImageUrl(request.getUrl())
                .setType(request.getType());
    }
}
