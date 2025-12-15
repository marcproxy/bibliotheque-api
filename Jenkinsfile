pipeline {
    agent any

    tools {
        maven 'Maven 3.9.0'
        jdk 'JDK 17'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Récupération du code source...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Compilation du projet...'
                sh 'mvn clean compile'
            }
        }

        stage('Package') {
            steps {
                echo 'Création du package (tests désactivés temporairement)...'
                sh 'mvn package -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo 'Analyse SonarQube...'
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=bibliotheque-api -DskipTests'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    post {
        success {
            echo '✅ Build réussi!'
            echo '✅ Package créé: target/demo-0.0.1-SNAPSHOT.jar'
            echo '✅ Analyse SonarQube terminée!'
        }
        failure {
            echo '❌ Build échoué!'
        }
    }
}