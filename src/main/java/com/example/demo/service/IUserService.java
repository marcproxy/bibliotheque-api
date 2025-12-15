package com.example.demo.service;

import com.example.demo.entity.User;

public interface IUserService {

    /**
     * Créer un nouveau compte utilisateur avec le statut INACTIF
     * @param user l'utilisateur à créer
     * @throws Exception si l'email existe déjà
     */
    void registerUser(User user) throws Exception;

    /**
     * Activer le compte d'un utilisateur
     * @param email l'email de l'utilisateur à activer
     * @throws Exception si l'email est invalide ou le compte n'existe pas
     */
    void activateUser(String email) throws Exception;

    /**
     * Authentifier un utilisateur
     * @param email l'email de l'utilisateur
     * @param password le mot de passe de l'utilisateur
     * @return true si l'authentification réussit, false sinon
     * @throws Exception si le compte n'est pas actif ou les identifiants sont invalides
     */
    boolean authenticateUser(String email, String password) throws Exception;

    /**
     * Supprimer le compte d'un utilisateur (désinscription)
     * @param email l'email de l'utilisateur à supprimer
     * @throws Exception si l'utilisateur n'existe pas
     */
    void unsubscribeUser(String email) throws Exception;

    /**
     * Récupérer un utilisateur par son email
     * @param email l'email de l'utilisateur
     * @return l'utilisateur trouvé ou null
     * @throws Exception en cas d'erreur
     */
    User getUserByEmail(String email) throws Exception;

    /**
     * Mettre à jour le profil d'un utilisateur (firstname, lastname)
     * @param email l'email de l'utilisateur
     * @param firstname le nouveau prénom
     * @param lastname le nouveau nom
     * @throws Exception si l'utilisateur n'existe pas
     */
    void updateProfile(String email, String firstname, String lastname) throws Exception;

    /**
     * Mettre à jour le profil d'un utilisateur par ID (firstname, lastname)
     * @param id l'ID de l'utilisateur
     * @param firstname le nouveau prénom
     * @param lastname le nouveau nom
     * @throws Exception si l'utilisateur n'existe pas
     */
    void updateProfileById(Long id, String firstname, String lastname) throws Exception;

    /**
     * Mettre à jour le mot de passe d'un utilisateur
     * @param email l'email de l'utilisateur
     * @param oldPassword l'ancien mot de passe
     * @param newPassword le nouveau mot de passe
     * @throws Exception si l'utilisateur n'existe pas ou l'ancien mot de passe est incorrect
     */
    void updatePassword(String email, String oldPassword, String newPassword) throws Exception;

    /**
     * Mettre à jour le mot de passe d'un utilisateur par ID
     * @param id l'ID de l'utilisateur
     * @param oldPassword l'ancien mot de passe
     * @param newPassword le nouveau mot de passe
     * @throws Exception si l'utilisateur n'existe pas ou l'ancien mot de passe est incorrect
     */
    void updatePasswordById(Long id, String oldPassword, String newPassword) throws Exception;

    /**
     * Obtenir la question de sécurité d'un utilisateur
     * @param email l'email de l'utilisateur
     * @return la question de sécurité (sans révéler la réponse)
     * @throws Exception si l'utilisateur n'existe pas
     */
    String getSecurityQuestion(String email) throws Exception;

    /**
     * Vérifier la réponse à la question de sécurité
     * @param email l'email de l'utilisateur
     * @param answer la réponse fournie
     * @return true si la réponse est correcte, false sinon
     * @throws Exception si l'utilisateur n'existe pas
     */
    boolean verifySecurityAnswer(String email, String answer) throws Exception;

    /**
     * Renouveler le mot de passe avec vérification de l'historique et de l'expiration
     * @param email l'email de l'utilisateur
     * @param oldPassword l'ancien mot de passe
     * @param newPassword le nouveau mot de passe
     * @throws Exception si l'ancien mot de passe est incorrect ou le nouveau est dans l'historique
     */
    void renewPassword(String email, String oldPassword, String newPassword) throws Exception;

    /**
     * Vérifier si le mot de passe d'un utilisateur est expiré (>12 semaines)
     * @param email l'email de l'utilisateur
     * @return true si expiré, false sinon
     * @throws Exception si l'utilisateur n'existe pas
     */
    boolean isPasswordExpired(String email) throws Exception;
}