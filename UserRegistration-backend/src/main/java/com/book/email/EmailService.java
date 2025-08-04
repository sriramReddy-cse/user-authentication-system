package com.book.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.hibernate.metamodel.mapping.internal.EmbeddedIdentifierMappingImpl;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmailService{


    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendMail(
             String to,
             String username,
             EmailTemplateName emailTemplate,
             String confirmationUrl,
             String activationCode,
             String subject
    ) throws MessagingException {

        String templateName;
        if(emailTemplate == null){
            templateName = "Confirm_email";
        }else{
            templateName = emailTemplate.getName();
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                //here Multi-part-mixed mode means we can send the email with attachments
                mimeMessage,MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );

        Map<String,Object> properties = new HashMap<>();
        properties.put("username",username);
        properties.put("confirmationUrl",confirmationUrl);
        properties.put("activation_Code",activationCode);

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom("sreeramr279@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        String template = templateEngine.process(templateName,context);
        helper.setText(template,true);
        mailSender.send(mimeMessage);
    }

}
