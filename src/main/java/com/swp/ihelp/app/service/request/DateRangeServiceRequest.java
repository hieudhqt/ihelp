package com.swp.ihelp.app.service.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class DateRangeServiceRequest implements Serializable {
    private Date fromDate;
    private Date toDate;
    private List<Integer> status;
}
