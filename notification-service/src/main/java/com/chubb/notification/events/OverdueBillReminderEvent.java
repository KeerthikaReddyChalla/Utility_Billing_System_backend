package com.chubb.notification.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverdueBillReminderEvent {
    private String billId;
    private String consumerEmail;
    private double amount;
}
