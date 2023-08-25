package com.hysteryale.service.impl;

import com.hysteryale.service.EmailService;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;

public class EmailServiceImpl implements EmailService {

    //for testing only, should put into the configuration
    private String emailFrom = "bao.khuu@edge-works.net";

    @Override
    public void sendRegistrationEmail(String emailTo) throws MailjetSocketTimeoutException, MailjetException {

        String subject = "Hello";
        String textPart = "Hello!";
        String htmlPart = "<html> Hello </html>";
        String customID = "Hello!!!";

        //create email body
        MailjetRequest request = createEmailRequest(emailTo, subject, textPart, htmlPart,customID);
        sendEmail(request);
    }

    private MailjetRequest createEmailRequest(String emailTo, String subject, String textPart, String htmlPart, String customID){
        return new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", emailFrom)
                                        .put(Emailv31.Message.TO, new JSONArray()
                                                .put(new JSONObject()
                                                        .put("Email", emailTo)))
                                        .put(Emailv31.Message.SUBJECT, subject)
                                        .put(Emailv31.Message.TEXTPART, textPart)
                                        .put(Emailv31.Message.HTMLPART, htmlPart)
                                        .put(Emailv31.Message.CUSTOMID, customID))));
    }

    private void sendEmail(MailjetRequest request) throws MailjetSocketTimeoutException, MailjetException {
        MailjetClient client;
        MailjetResponse response;

        client = new MailjetClient("7d6bd9c34e16e6f8e63bd37cf4d64005",
                "c1f828eb13eee6f7c438775820ee92a5", new ClientOptions("v3.1"));

        response = client.post(request);
    }
}
