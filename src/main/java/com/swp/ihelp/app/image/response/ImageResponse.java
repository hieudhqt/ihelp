package com.swp.ihelp.app.image.response;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.image.ImageEntity;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ImageResponse {
    private String imageId;
    private String url;
    private String type;
    private String authorEmail;

    public ImageResponse(ImageEntity imageEntity) {
        this.imageId = imageEntity.getId();
        this.url = imageEntity.getImageUrl();
        this.type = imageEntity.getType();
        this.authorEmail = imageEntity.getAuthorAccount().getEmail();
    }

    public static ImageEntity convertToEntity(ImageResponse imageResponse) {
        AccountEntity authorAccount = new AccountEntity().setEmail(imageResponse.getAuthorEmail());
        return new ImageEntity()
                .setId(imageResponse.getImageId())
                .setImageUrl(imageResponse.getUrl())
                .setAuthorAccount(authorAccount)
                .setType(imageResponse.getType());
    }

    public static Set<ImageResponse> convertToResponseList(Set<ImageEntity> imageEntities) {
        Set<ImageResponse> responseList = new HashSet<>();
        for (ImageEntity imageEntity : imageEntities) {
            responseList.add(new ImageResponse(imageEntity));
        }
        return responseList;
    }
}
