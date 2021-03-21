package com.swp.ihelp.app.image.request;

import com.swp.ihelp.app.image.ImageEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;

@Data
@NoArgsConstructor
public class ImageRequest {
    private String url;

    @Length(max = 10, message = "Image type cannot be longer than 10 characters.")
    private String type;

    public static ImageEntity convertRequestToEntity(@Valid ImageRequest request) {
        return new ImageEntity()
                .setImageUrl(request.getUrl())
                .setType(request.getType());
    }
}
