package com.example.demo.service;

import com.example.demo.entity.User;

public interface IEmailService {

    /**
     * Envoyer un email d'activation après inscription
     * @param user l'utilisateur qui vient de s'inscrire
     * @throws Exception en cas d'erreur d'envoi
     */
    void sendActivationEmail(User user) throws Exception;

    /**
     * Envoyer un email de confirmation après activation
     * @param user l'utilisateur qui vient d'être activé
     * @throws Exception en cas d'erreur d'envoi
     */
    void sendActivationConfirmationEmail(User user) throws Exception;

    /**
     * Envoyer un email de confirmation de désinscription
     * @param user l'utilisateur qui se désinscrit
     * @throws Exception en cas d'erreur d'envoi
     */
    void sendUnsubscribeConfirmationEmail(User user) throws Exception;
}