package wallet.api.infra.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Falha de envio é logada e não propaga: a rota de forgot responde 200 genérico
    // de qualquer forma, para não vazar se o email existe na base
    public void sendPasswordResetEmail(String to, String resetLink, long expirationMinutes) {
        try {
            var message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("Wallet — recuperação de senha");
            message.setText("""
                    Olá!

                    Recebemos um pedido para redefinir a sua senha no Wallet.

                    Acesse o link abaixo para criar uma nova senha (válido por %d minutos):

                    %s

                    Se você não pediu a redefinição, ignore este email.
                    """.formatted(expirationMinutes, resetLink));
            mailSender.send(message);
            log.info("Password reset email sent to {}", to);
        } catch (MailException e) {
            log.error("Failed to send password reset email to {}: {}", to, e.getMessage());
        }
    }
}
