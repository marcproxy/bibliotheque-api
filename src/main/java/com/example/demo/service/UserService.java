package com.example.demo.service;

import com.example.demo.entity.PasswordHistory;
import com.example.demo.entity.User;
import com.example.demo.entity.UserStatus;
import com.example.demo.repository.IPasswordHistoryRepository;
import com.example.demo.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private IPasswordHistoryRepository passwordHistoryRepository;

    @Override
    public void registerUser(User user) throws Exception {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new Exception("Un compte avec cet email existe déjà");
        }

        // Vérifier que la question de sécurité est valide
        if (user.getSecurityQuestion() == null || user.getSecurityQuestion().isEmpty()) {
            throw new Exception("Une question de sécurité est requise");
        }

        if (user.getSecurityAnswer() == null || user.getSecurityAnswer().isEmpty()) {
            throw new Exception("Une réponse à la question de sécurité est requise");
        }

        // Vérifier la longueur de la réponse (max 32 caractères)
        if (user.getSecurityAnswer().length() > 32) {
            throw new Exception("La réponse à la question de sécurité ne doit pas dépasser 32 caractères");
        }

        // Hacher le mot de passe avec BCrypt avant de l'enregistrer
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // Hacher la réponse de sécurité avec BCrypt
        String hashedAnswer = passwordEncoder.encode(user.getSecurityAnswer().toLowerCase().trim());
        user.setSecurityAnswer(hashedAnswer);

        // Définir le statut à INACTIF par défaut
        user.setStatus(UserStatus.INACTIF);

        // Sauvegarder l'utilisateur
        userRepository.save(user);

        // Envoyer l'email d'activation
        try {
            emailService.sendActivationEmail(user);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email d'activation : " + e.getMessage());
            // On ne lance pas d'exception pour ne pas bloquer l'inscription
        }
    }

    @Override
    public void activateUser(String email) throws Exception {
        // Rechercher l'utilisateur par email
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new Exception("Aucun compte trouvé avec cet email");
        }

        User user = optionalUser.get();

        // Vérifier si le compte est déjà actif
        if (user.getStatus() == UserStatus.ACTIF) {
            throw new Exception("Ce compte est déjà actif");
        }

        // Activer le compte
        user.setStatus(UserStatus.ACTIF);
        userRepository.save(user);

        // Envoyer l'email de confirmation d'activation
        try {
            emailService.sendActivationConfirmationEmail(user);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email de confirmation : " + e.getMessage());
            // On ne lance pas d'exception pour ne pas bloquer l'activation
        }
    }

    @Override
    public boolean authenticateUser(String email, String password) throws Exception {
        // Rechercher l'utilisateur par email uniquement
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new Exception("Email ou mot de passe incorrect");
        }

        User user = optionalUser.get();

        // Vérifier le mot de passe avec BCrypt
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Email ou mot de passe incorrect");
        }

        // Vérifier si le compte est actif
        if (user.getStatus() == UserStatus.INACTIF) {
            throw new Exception("Votre compte n'est pas encore activé. Veuillez vérifier votre email");
        }

        // Vérifier si le mot de passe est expiré
        if (isPasswordExpired(email)) {
            throw new Exception("Votre mot de passe a expiré (plus de 12 semaines). Veuillez le renouveler via /api/users/" + email + "/password/renew");
        }

        return true;
    }

    @Override
    public void unsubscribeUser(String email) throws Exception {
        // Rechercher l'utilisateur par email
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new Exception("Aucun compte trouvé avec cet email");
        }

        User user = optionalUser.get();

        // Envoyer l'email de confirmation de désinscription AVANT la suppression
        try {
            emailService.sendUnsubscribeConfirmationEmail(user);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email de désinscription : " + e.getMessage());
            // On continue quand même avec la suppression
        }

        // Supprimer l'utilisateur
        userRepository.delete(user);
    }

    @Override
    public User getUserByEmail(String email) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElse(null);
    }

    @Override
    public void updateProfile(String email, String firstname, String lastname) throws Exception {
        // Rechercher l'utilisateur par email
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new Exception("Aucun compte trouvé avec cet email");
        }

        User user = optionalUser.get();

        // Mettre à jour le prénom et le nom
        if (firstname != null && !firstname.isEmpty()) {
            user.setFirstname(firstname);
        }
        if (lastname != null && !lastname.isEmpty()) {
            user.setLastname(lastname);
        }

        // Sauvegarder les modifications
        userRepository.save(user);
    }

    @Override
    public void updatePassword(String email, String oldPassword, String newPassword) throws Exception {
        // Rechercher l'utilisateur par email
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new Exception("Aucun compte trouvé avec cet email");
        }

        User user = optionalUser.get();

        // Vérifier que l'ancien mot de passe est correct avec BCrypt
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new Exception("L'ancien mot de passe est incorrect");
        }

        // Vérifier que le nouveau mot de passe n'est pas vide
        if (newPassword == null || newPassword.isEmpty()) {
            throw new Exception("Le nouveau mot de passe ne peut pas être vide");
        }

        // Hacher le nouveau mot de passe avec BCrypt
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);

        // Sauvegarder les modifications
        userRepository.save(user);
    }

    @Override
    public void updateProfileById(Long id, String firstname, String lastname) throws Exception {
        // Rechercher l'utilisateur par ID
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new Exception("Aucun compte trouvé avec cet ID");
        }

        User user = optionalUser.get();

        // Mettre à jour le prénom et le nom
        if (firstname != null && !firstname.isEmpty()) {
            user.setFirstname(firstname);
        }
        if (lastname != null && !lastname.isEmpty()) {
            user.setLastname(lastname);
        }

        // Sauvegarder les modifications
        userRepository.save(user);
    }

    @Override
    public void updatePasswordById(Long id, String oldPassword, String newPassword) throws Exception {
        // Rechercher l'utilisateur par ID
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new Exception("Aucun compte trouvé avec cet ID");
        }

        User user = optionalUser.get();

        // Vérifier que l'ancien mot de passe est correct avec BCrypt
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new Exception("L'ancien mot de passe est incorrect");
        }

        // Vérifier que le nouveau mot de passe n'est pas vide
        if (newPassword == null || newPassword.isEmpty()) {
            throw new Exception("Le nouveau mot de passe ne peut pas être vide");
        }

        // Hacher le nouveau mot de passe avec BCrypt
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);

        // Sauvegarder les modifications
        userRepository.save(user);
    }

    @Override
    public String getSecurityQuestion(String email) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new Exception("Aucun compte trouvé avec cet email");
        }

        User user = optionalUser.get();

        if (user.getSecurityQuestion() == null || user.getSecurityQuestion().isEmpty()) {
            throw new Exception("Aucune question de sécurité n'est définie pour ce compte");
        }

        return user.getSecurityQuestion();
    }

    @Override
    public boolean verifySecurityAnswer(String email, String answer) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new Exception("Aucun compte trouvé avec cet email");
        }

        User user = optionalUser.get();

        if (user.getSecurityAnswer() == null || user.getSecurityAnswer().isEmpty()) {
            throw new Exception("Aucune réponse de sécurité n'est définie pour ce compte");
        }

        // Vérifier la réponse avec BCrypt (en minuscules et sans espaces)
        return passwordEncoder.matches(answer.toLowerCase().trim(), user.getSecurityAnswer());
    }

    @Override
    public void renewPassword(String email, String oldPassword, String newPassword) throws Exception {
        // Rechercher l'utilisateur
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new Exception("Aucun compte trouvé avec cet email");
        }

        User user = optionalUser.get();

        // Vérifier que l'ancien mot de passe est correct
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new Exception("L'ancien mot de passe est incorrect");
        }

        // Vérifier que le nouveau mot de passe n'est pas vide
        if (newPassword == null || newPassword.isEmpty()) {
            throw new Exception("Le nouveau mot de passe ne peut pas être vide");
        }

        // Récupérer les 5 derniers mots de passe de l'historique
        List<PasswordHistory> history = passwordHistoryRepository
                .findTopNByUserIdOrderByChangedAtDesc(user.getId());

        // Limiter à 5 entrées maximum
        int limit = Math.min(5, history.size());
        List<PasswordHistory> last5 = history.subList(0, limit);

        // Vérifier que le nouveau mot de passe n'est pas dans les 5 derniers
        for (PasswordHistory ph : last5) {
            if (passwordEncoder.matches(newPassword, ph.getHashedPassword())) {
                throw new Exception("Vous ne pouvez pas réutiliser l'un de vos 5 derniers mots de passe");
            }
        }

        // Sauvegarder l'ancien mot de passe dans l'historique
        PasswordHistory oldPasswordEntry = new PasswordHistory();
        oldPasswordEntry.setUser(user);
        oldPasswordEntry.setHashedPassword(user.getPassword());
        oldPasswordEntry.setChangedAt(LocalDateTime.now());
        passwordHistoryRepository.save(oldPasswordEntry);

        // Nettoyer l'historique si > 5 entrées
        List<PasswordHistory> allHistory = passwordHistoryRepository
                .findTopNByUserIdOrderByChangedAtDesc(user.getId());
        if (allHistory.size() > 5) {
            // Garder seulement les 5 plus récents
            for (int i = 5; i < allHistory.size(); i++) {
                passwordHistoryRepository.delete(allHistory.get(i));
            }
        }

        // Hacher et mettre à jour le nouveau mot de passe
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        user.setPasswordLastUpdated(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public boolean isPasswordExpired(String email) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new Exception("Aucun compte trouvé avec cet email");
        }

        User user = optionalUser.get();

        // Si la date n'est pas définie, considérer comme expiré
        if (user.getPasswordLastUpdated() == null) {
            return true;
        }

        // Calculer la différence en semaines (12 semaines = 84 jours)
        LocalDateTime expirationDate = user.getPasswordLastUpdated().plusWeeks(12);
        return LocalDateTime.now().isAfter(expirationDate);
    }
}