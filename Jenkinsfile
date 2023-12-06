pipeline {
    agent any
    tools {
            terraform 'Terraform'
    }
    environment {
        SCANNER_HOME = tool 'sonar-scanner'
        AWS_ACCESS_KEY_ID = credentials('AWS_ACCESS_KEY_ID')
        AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_ACCESS_KEY')
        DOCKERHUB_PASSWORD = credentials('dockerhub-password-id')
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout the code from GitHub
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/motez-baccouch/docker-spring']])
            }
        }

        stage('Build project and Docker image') {
            steps {
                sh 'mvn clean install'
                sh 'docker build -t trino22/devops-automation .'
            }
        }

        stage('Push Docker Image') {
            steps {
                // Push the Docker image to DockerHub
                sh "docker login -u trino22 -p ${DOCKERHUB_PASSWORD}"
                sh 'docker push trino22/devops-automation'
            }
        }

        stage('Unit Test') {
            steps {
                // Run unit tests
                sh "mvn test"
            }
        }

        stage('Deploy to Dev') {
            steps {
                sh 'docker-compose -f docker-compose.yml up -d springbootapp mongodb'
            }
        }

        stage('Integration Test and Deploy to Test') {
            steps {
                sh 'mvn verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube-server') {
                    //sh '''$SCANNER_HOME/bin/sonar-scanner \
                      //  -Dsonar.projectName=petstore \
                    //    -Dsonar.java.binaries=. \
                     //   -Dsonar.projectKey=petstore'''
                }
            }
        }

        stage('OWASP DEPENDENCY') {
            steps {
                dependencyCheck additionalArguments: '--scan ./ ', odcInstallation: 'DP'
                dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
            }
        }
        stage('Terraform Init') {
            steps {
                sh 'terraform init'
            }
        }

        stage('Terraform Apply') {
            steps {
                script {
                    // Use withEnv to dynamically set environment variables if necessary
                    withEnv(["AWS_ACCESS_KEY_ID=${env.AWS_ACCESS_KEY_ID}", "AWS_SECRET_ACCESS_KEY=${env.AWS_SECRET_ACCESS_KEY}"]) {
                        sh 'terraform apply -auto-approve'
                        // Assign the value of the public IP to the variable EC2_PUBLIC_IP
                    }
                }
            }
        }

         stage('Grafana + proetheus') {
            steps {
                  //sh 'docker-compose -f ./docker-compose.yml up -d prometheus grafana'
                  sh 'docker-compose -f ./docker-compose.yml up -d grafana'
                }
            }

         stage('Trivy') {
            steps {
                  sh " trivy image trino22/devops-automation:latest"
                }
            }


    }

    post {
        success {
            // Notify teams in case of success
            emailext subject: 'CI/CD Pipeline Successful',
                      body: 'The CI/CD pipeline has completed successfully.',
                      to: 'motez.momyaaa@gmail.com'
        }

        failure {
            // Notify teams in case of failure
            emailext subject: 'CI/CD Pipeline Failed',
                      body: 'The CI/CD pipeline has failed. Please check the Jenkins console for details.',
                      to: 'motez.momyaaa@gmail.com'
        }
    }
}
