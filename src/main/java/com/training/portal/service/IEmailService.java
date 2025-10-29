package com.training.portal.service;

import java.io.IOException;

public interface IEmailService {

    void sendNewCourseEmail(String toEmail, String studentName, String courseName, String courseUrl) throws IOException;
}
