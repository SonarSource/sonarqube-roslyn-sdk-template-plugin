@Library('SonarSource@1.2') _

pipeline {
  agent { 
    label 'linux' 
  }
  parameters {
    string(name: 'GIT_SHA1', description: 'Git SHA1 (provided by travisci hook job)')
    string(name: 'CI_BUILD_NAME', defaultValue: 'cix-pipelines', description: 'Build Name (provided by travisci hook job)')
    string(name: 'CI_BUILD_NUMBER', description: 'Build Number (provided by travisci hook job)')
    string(name: 'GITHUB_BRANCH', defaultValue: 'branch-declarative-qa', description: 'Git branch (provided by travisci hook job)')
    string(name: 'GITHUB_REPOSITORY_OWNER', defaultValue: 'SonarSource', description: 'Github repository owner(provided by travisci hook job)')
  }
  environment { 
    SONARSOURCE_QA = 'true'
    MAVEN_TOOL = 'Maven 3.5.2'
    // To simulate the build phase
    ARTIFACTORY_DEPLOY_REPO = "sonarsource-public-qa"
  }
  stages {
    stage('Fake notifications from GitHub') {
      steps {
        burgrFakeCommit()
      }
    }
    stage('Build') {
      agent {
        label 'linux'
      }
      steps {
        //sendAllNotificationBuildStarted()
        burgrNotifyBuildStarted()
        withMaven(maven: MAVEN_TOOL) {
          mavenSetBuildVersion()
          sh 'mvn deploy -Pdeploy-sonarsource'
        }
      }
      post {
        always {
          //sendAllNotificationBuildResult()
          burgrNotifyBuildResult()
        }
      }
    }
    stage('Notify BURGR QA start') {
      steps {                
        //sendAllNotificationQaStarted()
        burgrNotifyQaStarted()
      }
    }
    stage('QA') {            
      parallel {            
        stage('LTS') {
          agent {
            label 'linux'
          }
          steps {  
            withMaven(maven: MAVEN_TOOL) {
              mavenSetBuildVersion()
              sh 'mvn test'                            
            }                        
          }
        }
        stage('DEV') {
          agent {
            label 'linux'
          }
          steps {
            withMaven(maven: MAVEN_TOOL) {
              mavenSetBuildVersion()
              sh 'mvn test'
            }                        
          }
        }
      }                
      post {
        always {
          //sendAllNotificationQaResult()
          burgrNotifyQaResult()
        }
      }
    }
    stage('Promote') {
      steps {
        repoxPromoteBuild()
      }
      post {
        always {
          //sendAllNotificationPromote()
          burgrNotifyPromote()
        }
      }
    }
  }
}
