@Library('SonarSource@2.1.2') _

pipeline {
  agent none
  parameters {
    string(name: 'GIT_SHA1', description: 'Git SHA1 (provided by travisci hook job)')
    string(name: 'CI_BUILD_NAME', defaultValue: 'cix-pipelines', description: 'Build Name (provided by travisci hook job)')
    string(name: 'CI_BUILD_NUMBER', description: 'Build Number (provided by travisci hook job)')
    string(name: 'GITHUB_BRANCH', defaultValue: 'branch-declarative-qa', description: 'Git branch (provided by travisci hook job)')
    string(name: 'GITHUB_REPOSITORY_OWNER', defaultValue: 'SonarSource', description: 'Github repository owner(provided by travisci hook job)')
  }
  environment {
    SONARSOURCE_QA = 'true'
    MAVEN_TOOL = 'Maven 3.5.x'
    // To simulate the build phase
    ARTIFACTORY_DEPLOY_REPO = "sonarsource-public-qa"
  }
  stages {
    stage('Notify BURGR QA start') {
      steps {
        sendAllNotificationQaStarted()
      }
    }
    stage('QA') {
      parallel {
        stage('LTS') {
          agent {
            label 'linux'
          }
          steps {
            withQAEnv {
              mavenSetBuildVersion()
              sh 'mvn test'
            }
          }
        }
      }
      post {
        always {
          sendAllNotificationQaResult()
        }
      }
    }
    stage('Promote') {
      steps {
        repoxPromoteBuild()
      }
      post {
        always {
          burgrNotifyPromote()
        }
      }
    }
  }
}

def withQAEnv(def body) {
  def jdk = tool name: 'Java 11', type: 'jdk'
  withEnv(["JAVA_HOME=${jdk}"]) {
    withMaven(maven: env.MAVEN_TOOL) {
      body.call()
    }
  }
}
