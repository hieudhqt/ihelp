package com.swp.ihelp.app.event;

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
import java.util.Date;

@Entity
@Table(name = "event")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Event {

    // ID format: EV_0000x
    @Id
    @GeneratedValue(generator = "sequence_generator")
    @GenericGenerator(
            name = "sequence_generator",
            strategy = "com.swp.ihelp.config.StringPrefixedSequenceIdGenerator",
            parameters = {
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "50"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "EV_"),
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

    public Event(String title, String description, int numOfParticipants, Date createDate, Date startDate, Date endDate, String location) {
        this.title = title;
        this.description = description;
        this.numOfParticipants = numOfParticipants;
        this.createDate = createDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
    }

    //    private String statusId;
}
