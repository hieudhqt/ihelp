package com.swp.ihelp.app.transaction;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.config.StringPrefixedSequenceIdGenerator;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "transaction", schema = "ihelp")
@Data
@Accessors(chain = true)
public class TransactionEntity {
    // ID format: TR_0000x
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq")
    @GenericGenerator(
            name = "transaction_seq",
            strategy = "com.swp.ihelp.config.StringPrefixedSequenceIdGenerator",
            parameters = {
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "50"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "TR_"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d")
            }
    )
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @Basic
    @Column(name = "point", nullable = false)
    private int point;

    @Basic
    @Column(name = "description", nullable = true, length = 200)
    private String description;

    @Basic
    @Column(name = "date", nullable = true)
    private long date;

    @Basic
    @Column(name = "type", nullable = true, length = 45)
    private String type;

    @ManyToOne
    @JoinColumn(name = "sender_email", referencedColumnName = "email", nullable = false)
    private AccountEntity senderAccount;

    @ManyToOne
    @JoinColumn(name = "receiver_email", referencedColumnName = "email", nullable = false)
    private AccountEntity receiverAccount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionEntity that = (TransactionEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(point, that.point) &&
                Objects.equals(description, that.description) &&
                Objects.equals(date, that.date) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, point, description, date, type);
    }

}
