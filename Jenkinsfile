pipeline {
    agent any

    environment {
        REPO_URL       = 'https://github.com/Ravanan/argo-cd-deployment-with-eks.git'
        GIT_BRANCH     = 'main'

        DOCKERHUB_USER = 'mailravan'
        IMAGE_REPO     = 'demo'
        IMAGE_NAME     = "${DOCKERHUB_USER}/${IMAGE_REPO}"
        IMAGE_TAG      = "${env.BUILD_NUMBER}-${env.GIT_COMMIT?.take(7) ?: 'local'}"
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: "${GIT_BRANCH}",
                    url: "${REPO_URL}",
                    credentialsId: 'github-creds'
            }
        }

        stage('Build & Test') {
            agent {
                docker {
                    image 'maven:3.9-eclipse-temurin-17'
                    args  '-v jenkins-m2-cache:/root/.m2'
                    reuseNode true
                }
            }
            steps {
                sh 'mvn -B -N wrapper:wrapper'
                sh 'mvn -B clean verify'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: true
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'docker-registry-cred') {
                        def img = docker.build("${IMAGE_NAME}:${IMAGE_TAG}", '.')
                        img.push()
                        img.push('latest')
                    }
                }
            }
        }

        stage('Update Manifest (GitOps)') {
            when { branch 'main' }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'github-creds',
                    usernameVariable: 'GIT_USER',
                    passwordVariable: 'GIT_TOKEN'
                )]) {
                    sh '''
                        git config user.email "jenkins@ci.local"
                        git config user.name  "jenkins-ci"

                        sed -i "s|image: .*${IMAGE_REPO}:.*|image: ${IMAGE_NAME}:${IMAGE_TAG}|" k8s/deployment.yaml

                        git add k8s/deployment.yaml
                        if git diff --cached --quiet; then
                            echo "no manifest changes"
                            exit 0
                        fi

                        git commit -m "ci: bump image to ${IMAGE_TAG} [skip ci]"
                        git push https://${GIT_USER}:${GIT_TOKEN}@github.com/Ravanan/argo-cd-deployment-with-eks.git HEAD:${GIT_BRANCH}
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "Pushed ${IMAGE_NAME}:${IMAGE_TAG} to Docker Hub. ArgoCD will sync the deployment."
        }
        failure {
            echo "Pipeline failed at stage: ${env.STAGE_NAME}"
        }
        always {
            sh 'docker image prune -f || true'
            cleanWs()
        }
    }
}
