package com.swp.ihelp.app.image.request;

import com.swp.ihelp.app.image.ImageEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class UpdateImageRequest {
    private String id;

    private String url;

    @Length(max = 10, message = "Image type cannot be longer than 10 characters.")
    private String type;

    public static ImageEntity convertRequestToEntity(@Valid UpdateImageRequest request) {
        return new ImageEntity()
                .setId(request.getId())
                .setImageUrl(request.getUrl())
                .setType(request.getType());
    }

    public static Set<ImageEntity> convertRequestsToEntities(Set<UpdateImageRequest> requests) {
        Set<ImageEntity> imageEntities = new HashSet<>();
        for (UpdateImageRequest request : requests) {
            imageEntities.add(convertRequestToEntity(request));
        }
        return imageEntities;
    }
}
