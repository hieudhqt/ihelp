package com.swp.ihelp.app.notification;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.config.StringPrefixedSequenceIdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "notification", schema = "ihelp")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class NotificationEntity {

    // ID format: NT_0000x
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq")
    @GenericGenerator(
            name = "notification_seq",
            strategy = "com.swp.ihelp.config.StringPrefixedSequenceIdGenerator",
            parameters = {
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "50"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "NT_"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d")
            }
    )
    @Column(name = "id")
    private String id;

    @Basic
    @Column(name = "title")
    private String title;

    @Basic
    @Column(name = "message")
    private String message;

    @Basic
    @Column(name = "date")
    private Timestamp date;

    @ManyToOne
    @JoinColumn(name = "account_email", referencedColumnName = "email")
    private AccountEntity accountEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationEntity that = (NotificationEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(message, that.message) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, message, date);
    }
}
