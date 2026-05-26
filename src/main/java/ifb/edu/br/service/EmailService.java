package ifb.edu.br.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor 
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void enviarEmail(String para, String assunto, String conteudo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("nao-responda@geoachados.com.br");
            helper.setTo(para);
            helper.setSubject(assunto);
            helper.setText(conteudo, true);

            mailSender.send(message);
            
        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail", e);
        }
    }
}