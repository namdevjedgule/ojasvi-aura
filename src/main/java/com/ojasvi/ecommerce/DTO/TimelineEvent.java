package com.ojasvi.ecommerce.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimelineEvent {

    private String title;

    private String date;

    private boolean completed;

}
