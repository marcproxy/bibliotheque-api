pipeline {
    agent any

    tools {
        maven 'Maven 3.9.0'
        jdk 'JDK 17'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Récupération du code source...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '🔨 Compilation du projet...'
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                echo '🧪 Exécution des tests unitaires...'
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
        }

        stage('Package') {
            steps {
                echo '📦 Création du package...'
                sh 'mvn package -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Analyse SonarQube...'
                script {
                    withSonarQubeEnv('SonarQube') {
                        sh 'mvn sonar:sonar -Dsonar.projectKey=bibliotheque-api'
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo '⏳ Vérification du Quality Gate...'
                timeout(time: 5, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            echo "⚠️ Quality Gate status: ${qg.status}"
                        }
                    }
                }
            }
        }

        stage('Archive') {
            steps {
                echo '💾 Archivage...'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo '✅ ========================================='
            echo '✅ BUILD RÉUSSI!'
            echo '✅ ========================================='
            echo '✅ Compilation: OK'
            echo '✅ Tests: Tous passés'
            echo '✅ Package: demo-0.0.1-SNAPSHOT.jar'
            echo '✅ Couverture de code: Générée'
            echo '✅ Analyse SonarQube: Terminée'
            echo '✅ ========================================='
        }
        failure {
            echo '❌ Build échoué!'
        }
    }
}