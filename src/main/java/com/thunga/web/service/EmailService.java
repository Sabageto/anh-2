package com.thunga.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Email Service - Clean version without emojis and console logs
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Send staff registration email
     *
     * @return true if sent successfully, false otherwise
     */
    public boolean sendStaffRegistrationEmail(String toEmail, String fullName,
                                              String username, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to Book Library - Staff Account Created");

            String htmlContent = buildStaffRegistrationEmailContent(fullName, username, password, toEmail);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Send staff update notification email
     *
     * @return true if sent successfully, false otherwise
     */
    public boolean sendStaffUpdateNotification(String toEmail, String staffName,
                                               List<String> changedFields,
                                               String oldEmail, String newEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Thông báo Admin cập nhật tài khoản Staff");

            String htmlContent = buildStaffUpdateEmailContent(
                    staffName, changedFields, oldEmail, newEmail
            );
            helper.setText(htmlContent, true);

            mailSender.send(message);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Send email changed security alert to old email
     *
     * @return true if sent successfully, false otherwise
     */
    public boolean sendEmailChangedAlert(String oldEmail, String staffName, String newEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(oldEmail);
            helper.setSubject("Cảnh báo: Email tài khoản đã được thay đổi");

            String htmlContent = buildEmailChangedAlertContent(oldEmail, staffName, newEmail);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // ========================================
    // EMAIL TEMPLATES
    // ========================================

    private String buildStaffRegistrationEmailContent(String fullName, String username,
                                                      String password, String email) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }" +
                "        .content { background-color: #f9f9f9; padding: 30px; border: 1px solid #ddd; }" +
                "        .credentials { background-color: #fff; padding: 15px; border-left: 4px solid #4CAF50; margin: 20px 0; }" +
                "        .credential-item { margin: 10px 0; }" +
                "        .credential-label { font-weight: bold; color: #555; }" +
                "        .credential-value { color: #000; font-family: 'Courier New', monospace; }" +
                "        .warning { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; }" +
                "        .footer { text-align: center; margin-top: 20px; color: #777; font-size: 12px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">" +
                "            <h1>Welcome to Book Library</h1>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <h2>Xin chào " + fullName + ",</h2>" +
                "            <p>Tài khoản Staff của bạn đã được tạo thành công!</p>" +
                "            " +
                "            <div class=\"credentials\">" +
                "                <h3 style=\"margin-top: 0;\">Thông tin đăng nhập</h3>" +
                "                <div class=\"credential-item\">" +
                "                    <span class=\"credential-label\">Họ tên:</span> " +
                "                    <span class=\"credential-value\">" + fullName + "</span>" +
                "                </div>" +
                "                <div class=\"credential-item\">" +
                "                    <span class=\"credential-label\">Username:</span> " +
                "                    <span class=\"credential-value\">" + username + "</span>" +
                "                </div>" +
                "                <div class=\"credential-item\">" +
                "                    <span class=\"credential-label\">Password:</span> " +
                "                    <span class=\"credential-value\">" + password + "</span>" +
                "                </div>" +
                "                <div class=\"credential-item\">" +
                "                    <span class=\"credential-label\">Email:</span> " +
                "                    <span class=\"credential-value\">" + email + "</span>" +
                "                </div>" +
                "            </div>" +
                "            " +
                "            <div class=\"warning\">" +
                "                <strong>Lưu ý bảo mật:</strong>" +
                "                <ul style=\"margin: 10px 0;\">" +
                "                    <li>Vui lòng đổi mật khẩu sau lần đăng nhập đầu tiên</li>" +
                "                    <li>Không chia sẻ thông tin đăng nhập với người khác</li>" +
                "                    <li>Xóa email này sau khi đã đổi mật khẩu</li>" +
                "                </ul>" +
                "            </div>" +
                "            " +
                "            <p>Trân trọng,<br><strong>Book Library Team</strong></p>" +
                "        </div>" +
                "        <div class=\"footer\">" +
                "            <p>&copy; 2023 Book Library. All rights reserved.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    private String buildStaffUpdateEmailContent(String staffName, List<String> changedFields,
                                                String oldEmail, String newEmail) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        String updateTime = dateFormat.format(new Date());

        StringBuilder changesList = new StringBuilder();
        for (String field : changedFields) {
            switch (field) {
                case "email":
                    changesList.append("<li><strong>Email mới:</strong> ")
                            .append(newEmail)
                            .append(" <span style=\"color: #999;\">(Email cũ: ")
                            .append(oldEmail)
                            .append(")</span></li>");
                    break;
                case "name":
                    changesList.append("<li><strong>Họ tên</strong> đã được cập nhật</li>");
                    break;
                case "phone":
                    changesList.append("<li><strong>Số điện thoại</strong> đã được cập nhật</li>");
                    break;
                case "address":
                    changesList.append("<li><strong>Địa chỉ</strong> đã được cập nhật</li>");
                    break;
            }
        }

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "        .header { background-color: #FF9800; color: white; padding: 20px; text-align: center; }" +
                "        .content { background-color: #f9f9f9; padding: 30px; border: 1px solid #ddd; }" +
                "        .info-box { background-color: #fff; padding: 20px; border-left: 4px solid #FF9800; margin: 20px 0; }" +
                "        .warning-box { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; }" +
                "        .time-info { background-color: #e3f2fd; padding: 10px; border-radius: 3px; margin: 15px 0; }" +
                "        .footer { text-align: center; margin-top: 20px; color: #777; font-size: 12px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">" +
                "            <h1>Thông báo cập nhật tài khoản</h1>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <h2>Xin chào " + staffName + ",</h2>" +
                "            <p>Quản trị viên hệ thống đã cập nhật thông tin tài khoản Staff của bạn.</p>" +
                "            " +
                "            <div class=\"info-box\">" +
                "                <h3>Thông tin được thay đổi:</h3>" +
                "                <ul>" + changesList.toString() + "</ul>" +
                "            </div>" +
                "            " +
                "            <div class=\"time-info\">" +
                "                <strong>Thời gian cập nhật:</strong> " + updateTime +
                "            </div>" +
                "            " +
                "            <div class=\"warning-box\">" +
                "                <strong>Lưu ý quan trọng:</strong>" +
                "                <ul style=\"margin: 10px 0;\">" +
                "                    <li>Nếu bạn không yêu cầu thay đổi này, vui lòng liên hệ Admin ngay</li>" +
                "                    <li>Tài khoản của bạn có thể đã bị truy cập trái phép</li>" +
                "                    <li>Đề nghị đổi mật khẩu để bảo mật</li>" +
                "                </ul>" +
                "            </div>" +
                "            " +
                "            <p>Trân trọng,<br><strong>Hệ thống Book Library</strong></p>" +
                "        </div>" +
                "        <div class=\"footer\">" +
                "            <p>&copy; 2023 Book Library. All rights reserved.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    private String buildEmailChangedAlertContent(String oldEmail, String staffName, String newEmail) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        String updateTime = dateFormat.format(new Date());

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset=\"UTF-8\"></head>" +
                "<body style=\"font-family: Arial, sans-serif; padding: 20px;\">" +
                "    <div style=\"max-width: 600px; margin: 0 auto; border: 1px solid #ddd;\">" +
                "        <div style=\"background-color: #dc3545; color: white; padding: 20px; text-align: center;\">" +
                "            <h1 style=\"margin: 0;\">Cảnh báo bảo mật</h1>" +
                "        </div>" +
                "        <div style=\"padding: 30px;\">" +
                "            <h2>Xin chào " + staffName + ",</h2>" +
                "            <p>Email tài khoản Staff của bạn đã được thay đổi:</p>" +
                "            <div style=\"background-color: #f8d7da; padding: 15px; margin: 20px 0; border-left: 4px solid #dc3545;\">" +
                "                <p style=\"margin: 5px 0;\"><strong>Email cũ:</strong> " + oldEmail + "</p>" +
                "                <p style=\"margin: 5px 0;\"><strong>Email mới:</strong> " + newEmail + "</p>" +
                "                <p style=\"margin: 5px 0;\"><strong>Thời gian:</strong> " + updateTime + "</p>" +
                "            </div>" +
                "            <div style=\"background-color: #fff3cd; padding: 15px; margin: 20px 0;\">" +
                "                <strong>Nếu bạn không thực hiện thay đổi này:</strong>" +
                "                <ul>" +
                "                    <li>Liên hệ Admin ngay lập tức</li>" +
                "                    <li>Tài khoản của bạn có thể bị xâm phạm</li>" +
                "                    <li>Hotline: 0812 567 889</li>" +
                "                </ul>" +
                "            </div>" +
                "            <p>Trân trọng,<br>Hệ thống Book Library</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    // Add this method to your EmailService class:

    /**
     * Send OTP email for staff registration verification
     */
    public void sendOTPEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("sihira7075@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Email Verification - Staff Registration OTP");

            String htmlContent = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "    <meta charset='UTF-8'>" +
                    "    <style>" +
                    "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                    "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                    "        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                    "        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }" +
                    "        .otp-box { background: white; border: 2px dashed #667eea; border-radius: 10px; padding: 20px; margin: 20px 0; text-align: center; }" +
                    "        .otp-code { font-size: 36px; font-weight: bold; color: #667eea; letter-spacing: 8px; margin: 10px 0; }" +
                    "        .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 5px; }" +
                    "        .footer { text-align: center; margin-top: 30px; color: #666; font-size: 12px; }" +
                    "    </style>" +
                    "</head>" +
                    "<body>" +
                    "    <div class='container'>" +
                    "        <div class='header'>" +
                    "            <h1>📧 Email Verification</h1>" +
                    "            <p>Staff Registration - Book Library System</p>" +
                    "        </div>" +
                    "        <div class='content'>" +
                    "            <h2>Hello!</h2>" +
                    "            <p>You are receiving this email because a staff account is being created with this email address.</p>" +
                    "            <p>Please use the following <strong>One-Time Password (OTP)</strong> to verify your email:</p>" +
                    "            <div class='otp-box'>" +
                    "                <p style='margin: 0; color: #666;'>Your verification code is:</p>" +
                    "                <div class='otp-code'>" + otp + "</div>" +
                    "                <p style='margin: 0; color: #666; font-size: 14px;'>This code will expire in 5 minutes</p>" +
                    "            </div>" +
                    "            <div class='warning'>" +
                    "                <strong>⚠️ Security Notice:</strong><br>" +
                    "                • Do not share this OTP with anyone<br>" +
                    "                • Our staff will never ask for your OTP<br>" +
                    "                • If you didn't request this, please ignore this email" +
                    "            </div>" +
                    "            <p>After successful verification, you will receive another email with your account credentials.</p>" +
                    "        </div>" +
                    "        <div class='footer'>" +
                    "            <p>© 2023 Book Library System. All rights reserved.</p>" +
                    "            <p>This is an automated message, please do not reply to this email.</p>" +
                    "        </div>" +
                    "    </div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }
}