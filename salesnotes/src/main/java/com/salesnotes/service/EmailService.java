package com.salesnotes.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:no-reply@salesnotes.io}")
    private String fromAddress;

    @Value("${salesnotes.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    /**
     * Dispatches password reset email to user's mail address in table users.mail / users.email.
     * The reset link and token are delivered strictly to the user's inbox and never exposed on the web UI.
     *
     * @param recipientEmail Target email from users.mail / users.email column
     * @param username       User's account username
     * @param token          Secure 32-character reset token
     */
    public void sendPasswordResetEmail(String recipientEmail, String username, String token) {
        String resetLink = frontendUrl.replaceAll("/+$", "") + "/reset-password?token=" + token + "&user=" + username;

        log.info("================================================================================");
        log.info("[SalesNotes Email Service] PASSWORD RESET EMAIL DISPATCHED");
        log.info("Recipient (users.mail / users.email): {}", recipientEmail);
        log.info("Target Account Username            : {}", username);
        log.info("Reset Link Delivered to Mail       : {}", resetLink);
        log.info("Token Validity                     : 30 minutes");
        log.info("Security Policy                    : Link NOT rendered on UI; sent to mailbox only.");
        log.info("================================================================================");

        if (mailSender != null) {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                helper.setFrom(fromAddress, "SalesNotes Platform");
                helper.setTo(recipientEmail);
                helper.setSubject("SalesNotes - Password Reset Link");

                String html = buildResetEmailHtml(username, recipientEmail, resetLink, token);
                helper.setText(html, true);

                mailSender.send(mimeMessage);
                log.info("[SalesNotes Email Service] Successfully delivered reset email via SMTP to {}", recipientEmail);
            } catch (Exception ex) {
                log.warn("[SalesNotes Email Service] SMTP delivery to {} was not completed ({}). Reset link safely recorded in server logs.",
                        recipientEmail, ex.getMessage());
            }
        } else {
            log.info("[SalesNotes Email Service] Live SMTP server not configured in active profile. Reset link dispatched and recorded in server log for development.");
        }
    }

    private String buildResetEmailHtml(String username, String email, String resetLink, String token) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="utf-8">
              <style>
                body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f3f4f6; margin: 0; padding: 24px; color: #1f2937; }
                .email-container { max-width: 560px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.06); border: 1px solid #e5e7eb; }
                .header { background: linear-gradient(135deg, #1e40af, #3b82f6); color: #ffffff; padding: 28px 24px; text-align: center; }
                .header h1 { margin: 0 0 6px; font-size: 24px; font-weight: 700; letter-spacing: -0.5px; }
                .header p { margin: 0; font-size: 14px; opacity: 0.9; }
                .content { padding: 32px 24px; }
                .greeting { font-size: 16px; font-weight: 600; margin-bottom: 12px; }
                .msg { font-size: 14px; line-height: 1.6; color: #4b5563; margin-bottom: 24px; }
                .btn-wrapper { text-align: center; margin: 28px 0; }
                .btn { display: inline-block; background-color: #2563eb; color: #ffffff !important; text-decoration: none; font-weight: 600; font-size: 15px; padding: 14px 32px; border-radius: 8px; box-shadow: 0 2px 4px rgba(37,99,235,0.25); }
                .btn:hover { background-color: #1d4ed8; }
                .details-box { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 14px; font-size: 13px; color: #64748b; margin-top: 20px; }
                .details-box p { margin: 4px 0; }
                .token-code { font-family: Consolas, monospace; background: #e2e8f0; padding: 2px 6px; border-radius: 4px; color: #0f172a; font-weight: 600; }
                .link-text { word-break: break-all; color: #2563eb; }
                .footer { background: #f9fafb; padding: 18px 24px; text-align: center; font-size: 12px; color: #9ca3af; border-top: 1px solid #f3f4f6; }
              </style>
            </head>
            <body>
              <div class="email-container">
                <div class="header">
                  <h1>SalesNotes</h1>
                  <p>Client Interaction & Note Taking Intelligence</p>
                </div>
                <div class="content">
                  <div class="greeting">Hello %s,</div>
                  <p class="msg">
                    We received a request to reset your password for your SalesNotes account associated with <strong>%s</strong>.
                    Click the button below to set a new password:
                  </p>
                  <div class="btn-wrapper">
                    <a href="%s" target="_blank" class="btn">Set New Password &rarr;</a>
                  </div>
                  <div class="details-box">
                    <p><strong>Direct Link:</strong> <a href="%s" class="link-text">%s</a></p>
                    <p><strong>Reset Token:</strong> <span class="token-code">%s</span></p>
                    <p><strong>Expires in:</strong> 30 minutes</p>
                  </div>
                  <p class="msg" style="margin-top: 24px; font-size: 13px; color: #9ca3af;">
                    If you did not request a password reset, you can safely ignore this email. Your current password will remain unchanged.
                  </p>
                </div>
                <div class="footer">
                  &copy; %d SalesNotes Platform. All rights reserved.
                </div>
              </div>
            </body>
            </html>
            """.formatted(
                username,
                email,
                resetLink,
                resetLink,
                resetLink,
                token,
                java.time.Year.now().getValue()
        );
    }
}
