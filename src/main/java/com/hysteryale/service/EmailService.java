package com.hysteryale.service;

import com.mailjet.client.MailjetRequest;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;

public interface EmailService {


    /**
     * To send confirmation email to users when admin creates a new account
     * Email contains password
     */
    public void sendRegistrationEmail(String emailTo) throws MailjetSocketTimeoutException, MailjetException;
}
