package com.swp.ihelp.app.servicevolunteer;

import com.swp.ihelp.config.StringPrefixedSequenceIdGenerator;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "volunteer_service")
@Getter
@Setter
@ToString
public class ServiceVolunteer {

    // ID format: VS_0000x
    @Id
    @GeneratedValue(generator = "sequence_generator")
    @GenericGenerator(
            name = "sequence_generator",
            strategy = "com.swp.ihelp.config.StringPrefixedSequenceIdGenerator",
            parameters = {
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "50"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "VS_"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d"),
            }
    )
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "num_of_participants")
    private int numOfParticipants;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "location")
    private String location;

    public ServiceVolunteer() {
    }

    public ServiceVolunteer(String title, String description, int numOfParticipants, Date createDate, Date startDate, Date endDate, String location) {
        this.title = title;
        this.description = description;
        this.numOfParticipants = numOfParticipants;
        this.createDate = createDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
    }
}
