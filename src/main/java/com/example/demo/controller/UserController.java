package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * POST /api/users/register
     * Créer un nouveau compte utilisateur avec le statut INACTIF
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        try {
            userService.registerUser(user);
            response.put("message", "Compte créé avec succès. Un email d'activation vous a été envoyé.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * POST /api/users/activate
     * Activer le compte utilisateur associé à l'email
     */
    @PostMapping("/activate")
    public ResponseEntity<Map<String, String>> activateUser(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                response.put("error", "L'email est requis");
                return ResponseEntity.badRequest().body(response);
            }

            userService.activateUser(email);
            response.put("message", "Votre compte a été activé avec succès. Vous pouvez maintenant vous connecter.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * POST /api/users/login
     * Authentifier un utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, String> credentials) {
        Map<String, String> response = new HashMap<>();
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                response.put("error", "Email et mot de passe sont requis");
                return ResponseEntity.badRequest().body(response);
            }

            boolean isAuthenticated = userService.authenticateUser(email, password);
            if (isAuthenticated) {
                response.put("message", "Authentification réussie. Bienvenue !");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Échec de l'authentification");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * DELETE /api/users/unsubscribe
     * Supprimer le compte utilisateur (désinscription)
     */
    @DeleteMapping("/unsubscribe")
    public ResponseEntity<Map<String, String>> unsubscribeUser(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                response.put("error", "L'email est requis");
                return ResponseEntity.badRequest().body(response);
            }

            userService.unsubscribeUser(email);
            response.put("message", "Votre compte a été supprimé avec succès. Nous sommes désolés de vous voir partir.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * PUT /api/users/profile
     * Mettre à jour le profil de l'utilisateur (prénom et nom)
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, String>> updateProfile(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        try {
            String email = request.get("email");
            String firstname = request.get("firstname");
            String lastname = request.get("lastname");

            if (email == null || email.isEmpty()) {
                response.put("error", "L'email est requis");
                return ResponseEntity.badRequest().body(response);
            }

            userService.updateProfile(email, firstname, lastname);
            response.put("message", "Profil mis à jour avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * PUT /api/users/password
     * Mettre à jour le mot de passe de l'utilisateur
     */
    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> updatePassword(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        try {
            String email = request.get("email");
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            if (email == null || email.isEmpty()) {
                response.put("error", "L'email est requis");
                return ResponseEntity.badRequest().body(response);
            }

            if (oldPassword == null || oldPassword.isEmpty()) {
                response.put("error", "L'ancien mot de passe est requis");
                return ResponseEntity.badRequest().body(response);
            }

            if (newPassword == null || newPassword.isEmpty()) {
                response.put("error", "Le nouveau mot de passe est requis");
                return ResponseEntity.badRequest().body(response);
            }

            userService.updatePassword(email, oldPassword, newPassword);
            response.put("message", "Mot de passe mis à jour avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * PUT /api/users/{id}/profile
     * Mettre à jour le profil de l'utilisateur par ID (Devoir 3)
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<Map<String, String>> updateProfileById(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        try {
            String firstname = request.get("firstname");
            String lastname = request.get("lastname");

            userService.updateProfileById(id, firstname, lastname);
            response.put("message", "Profil mis à jour avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * PUT /api/users/{id}/{oldPwd}/{newPwd}
     * Mettre à jour le mot de passe de l'utilisateur par ID (Devoir 3)
     */
    @PutMapping("/{id}/{oldPwd}/{newPwd}")
    public ResponseEntity<Map<String, String>> updatePasswordById(
            @PathVariable Long id,
            @PathVariable String oldPwd,
            @PathVariable String newPwd) {
        Map<String, String> response = new HashMap<>();
        try {
            userService.updatePasswordById(id, oldPwd, newPwd);
            response.put("message", "Mot de passe mis à jour avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * GET /api/users/security-questions
     * Obtenir la liste des questions de sécurité disponibles
     */
    @GetMapping("/security-questions")
    public ResponseEntity<Map<String, Object>> getSecurityQuestions() {
        Map<String, Object> response = new HashMap<>();
        response.put("questions", com.example.demo.config.SecurityQuestions.getAllQuestions());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/users/{email}/security-question
     * Obtenir la question de sécurité d'un utilisateur
     */
    @GetMapping("/{email}/security-question")
    public ResponseEntity<Map<String, String>> getUserSecurityQuestion(@PathVariable String email) {
        Map<String, String> response = new HashMap<>();
        try {
            String question = userService.getSecurityQuestion(email);
            response.put("question", question);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * POST /api/users/verify-security-answer
     * Vérifier la réponse à la question de sécurité (double authentification)
     */
    @PostMapping("/verify-security-answer")
    public ResponseEntity<Map<String, Object>> verifySecurityAnswer(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = request.get("email");
            String answer = request.get("answer");

            if (email == null || email.isEmpty()) {
                response.put("error", "L'email est requis");
                return ResponseEntity.badRequest().body(response);
            }

            if (answer == null || answer.isEmpty()) {
                response.put("error", "La réponse est requise");
                return ResponseEntity.badRequest().body(response);
            }

            boolean isValid = userService.verifySecurityAnswer(email, answer);
            response.put("valid", isValid);

            if (isValid) {
                response.put("message", "Authentification réussie");
            } else {
                response.put("message", "Réponse incorrecte");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * PUT /api/users/{email}/password/renew
     * Renouveler le mot de passe avec vérification de l'historique (Rotation des mots de passe)
     */
    @PutMapping("/{email}/password/renew")
    public ResponseEntity<Map<String, String>> renewPassword(
            @PathVariable String email,
            @RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        try {
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            if (oldPassword == null || oldPassword.isEmpty()) {
                response.put("error", "L'ancien mot de passe est requis");
                return ResponseEntity.badRequest().body(response);
            }

            if (newPassword == null || newPassword.isEmpty()) {
                response.put("error", "Le nouveau mot de passe est requis");
                return ResponseEntity.badRequest().body(response);
            }

            userService.renewPassword(email, oldPassword, newPassword);
            response.put("message", "Mot de passe renouvelé avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * GET /api/users/{email}/password/expired
     * Vérifier si le mot de passe d'un utilisateur est expiré
     */
    @GetMapping("/{email}/password/expired")
    public ResponseEntity<Map<String, Object>> checkPasswordExpiration(@PathVariable String email) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isExpired = userService.isPasswordExpired(email);
            response.put("expired", isExpired);
            if (isExpired) {
                response.put("message", "Votre mot de passe a expiré. Veuillez le renouveler.");
            } else {
                response.put("message", "Votre mot de passe est encore valide.");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}