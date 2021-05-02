package com.swp.ihelp.app.point.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class EventPointHistory implements Serializable {
    private String email;
    private String name;
    private String imageUrl;
    private int point;
    private int rating;
    private int bonus;
}
