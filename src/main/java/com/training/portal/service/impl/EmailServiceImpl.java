package com.training.portal.service.impl;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.training.portal.service.IEmailService;

import java.io.IOException;

@Service
public class EmailServiceImpl implements IEmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.template.newCourseId}")
    private String newCourseTemplateId;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Value("${sendgrid.from.name}")
    private String fromName;

    @Value("${sendgrid.replyto.email:}")
    private String replyToEmail;

    @Value("${sendgrid.replyto.name:}")
    private String replyToName;

    @Override
    public void sendNewCourseEmail(String toEmail, String studentName, String courseName, String courseUrl) throws IOException {

        Email from = new Email(fromEmail, fromName);
        Email to = new Email(toEmail);

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setTemplateId(newCourseTemplateId);

        Personalization p = new Personalization();
        p.addTo(to);
        p.addDynamicTemplateData("student_name", studentName);
        p.addDynamicTemplateData("course_name", courseName);
        p.addDynamicTemplateData("course_url", courseUrl);
        mail.addPersonalization(p);

        if (replyToEmail != null && !replyToEmail.isBlank()) {
            mail.setReplyTo(new Email(replyToEmail, replyToName));
        }

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request req = new Request();

        try {

            Response response = sg.api(req);

            if (response.getStatusCode() >= 400) {
                System.err.println("Error al enviar correo. Verifica remitente, plantilla o API Key.");
            }

        } catch (IOException ex) {
            System.err.println("Error crítico al enviar correo con SendGrid:");
            ex.printStackTrace();
            throw ex;
        }
    }
}
