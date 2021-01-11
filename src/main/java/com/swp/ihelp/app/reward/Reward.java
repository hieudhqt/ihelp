package com.swp.ihelp.app.reward;

import com.swp.ihelp.config.StringPrefixedSequenceIdGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "reward")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Reward {

    // ID format: RW_0000x
    @Id
    @GeneratedValue(generator = "sequence_generator")
    @GenericGenerator(
            name = "sequence_generator",
            strategy = "com.swp.ihelp.config.StringPrefixedSequenceIdGenerator",
            parameters = {
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "50"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "RW_"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d"),
            }
    )
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "picture")
    private String picture;

    @Column(name = "discount_percentage")
    private float discountPercentage;


}
