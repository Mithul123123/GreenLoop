package com.greenloop.notification;

public interface Notification {
    void sendNotification(String toEmail, String subject, String message);
}
