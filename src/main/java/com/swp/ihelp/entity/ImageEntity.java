package com.swp.ihelp.entity;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.config.StringPrefixedSequenceIdGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "image", schema = "ihelp")
@Data
@NoArgsConstructor
public class ImageEntity {
    // ID format: IMG_0000x
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_seq")
    @GenericGenerator(
            name = "image_seq",
            strategy = "com.swp.ihelp.config.StringPrefixedSequenceIdGenerator",
            parameters = {
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "50"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "IMG_"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d")
            }
    )
    private String id;

    @Basic
    @Column(name = "image_url", nullable = false, length = 2083)
    private String imageUrl;

    @Basic
    @Column(name = "type", nullable = false, length = 10)
    private String type;

    @ManyToOne
    @JoinColumn(name = "account_email", referencedColumnName = "email", nullable = false)
    private AccountEntity accountByAccountEmail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageEntity that = (ImageEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(imageUrl, that.imageUrl) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, imageUrl, type);
    }
}
