package com.example.demo.service;

import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Override
    public void sendActivationEmail(User user) throws Exception {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Activation de votre compte - Bibliothèque");

            String emailBody = String.format(
                    "Bonjour %s %s,\n\n" +
                            "Merci de vous être inscrit(e) sur notre plateforme de gestion de bibliothèque.\n\n" +
                            "Pour activer votre compte, veuillez cliquer sur le lien ci-dessous :\n" +
                            "http://localhost:8080/api/users/activate?email=%s\n\n" +
                            "Ou utilisez l'endpoint suivant avec votre email :\n" +
                            "POST /api/users/activate\n" +
                            "Body: {\"email\": \"%s\"}\n\n" +
                            "Si vous n'êtes pas à l'origine de cette inscription, veuillez ignorer cet email.\n\n" +
                            "Cordialement,\n" +
                            "L'équipe de la Bibliothèque",
                    user.getFirstname(),
                    user.getLastname(),
                    user.getEmail(),
                    user.getEmail()
            );

            message.setText(emailBody);
            mailSender.send(message);

            System.out.println("Email d'activation envoyé à : " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email d'activation : " + e.getMessage());
            throw new Exception("Erreur lors de l'envoi de l'email d'activation");
        }
    }

    @Override
    public void sendActivationConfirmationEmail(User user) throws Exception {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Votre compte a été activé - Bibliothèque");

            String emailBody = String.format(
                    "Bonjour %s %s,\n\n" +
                            "Félicitations ! Votre compte a été activé avec succès.\n\n" +
                            "Vous pouvez maintenant vous connecter à notre plateforme de gestion de bibliothèque " +
                            "et profiter de tous nos services.\n\n" +
                            "Pour vous connecter, utilisez l'endpoint :\n" +
                            "POST /api/users/login\n" +
                            "Body: {\"email\": \"%s\", \"password\": \"votre_mot_de_passe\"}\n\n" +
                            "Nous vous souhaitons une excellente expérience sur notre plateforme.\n\n" +
                            "Cordialement,\n" +
                            "L'équipe de la Bibliothèque",
                    user.getFirstname(),
                    user.getLastname(),
                    user.getEmail()
            );

            message.setText(emailBody);
            mailSender.send(message);

            System.out.println("Email de confirmation d'activation envoyé à : " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email de confirmation d'activation : " + e.getMessage());
            throw new Exception("Erreur lors de l'envoi de l'email de confirmation");
        }
    }

    @Override
    public void sendUnsubscribeConfirmationEmail(User user) throws Exception {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Confirmation de désinscription - Bibliothèque");

            String emailBody = String.format(
                    "Bonjour %s %s,\n\n" +
                            "Nous vous confirmons que votre compte a été supprimé avec succès.\n\n" +
                            "Nous sommes désolés de vous voir partir et espérons vous revoir bientôt " +
                            "sur notre plateforme de gestion de bibliothèque.\n\n" +
                            "Si vous avez des questions ou des suggestions pour améliorer nos services, " +
                            "n'hésitez pas à nous contacter.\n\n" +
                            "Merci d'avoir utilisé nos services.\n\n" +
                            "Cordialement,\n" +
                            "L'équipe de la Bibliothèque",
                    user.getFirstname(),
                    user.getLastname()
            );

            message.setText(emailBody);
            mailSender.send(message);

            System.out.println("Email de confirmation de désinscription envoyé à : " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email de désinscription : " + e.getMessage());
            throw new Exception("Erreur lors de l'envoi de l'email de désinscription");
        }
    }
}