package com.example.demo.config;

import java.util.Arrays;
import java.util.List;

public class SecurityQuestions {

    /**
     * Liste des 5 questions de sécurité prédéfinies
     */
    public static final List<String> QUESTIONS = Arrays.asList(
            "Quel est le nom de jeune fille de votre mère ?",
            "Quel était le nom de votre premier animal de compagnie ?",
            "Quelle est votre ville de naissance ?",
            "Quel était le nom de votre école primaire ?",
            "Quel est le prénom de votre meilleur(e) ami(e) d'enfance ?"
    );

    /**
     * Vérifier si une question fait partie des questions prédéfinies
     */
    public static boolean isValidQuestion(String question) {
        return QUESTIONS.contains(question);
    }

    /**
     * Obtenir toutes les questions disponibles
     */
    public static List<String> getAllQuestions() {
        return QUESTIONS;
    }
}