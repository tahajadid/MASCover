pipeline {
  
  agent any
  environment {
    APP_NAME = 'Mas Cover'
  }
  options {
    // Stop the build early in case of compile or test failures
    skipStagesAfterUnstable()
  }
  stages {
    
    stage('Detect build type') {
      steps {
        script {
          if (env.BRANCH_NAME == 'develop' || env.CHANGE_TARGET == 'develop') {
            env.BUILD_TYPE = 'debug'
          } else if (env.BRANCH_NAME == 'master' || env.CHANGE_TARGET == 'master') {
            env.BUILD_TYPE = 'release'
          }
        }
      }
    }
    
    stage('Compile') {
      steps {
        // Compile the app and its dependencies
        sh './gradlew compileReleaseSources'
      }
    }
    
      
    stage('Publish') {
       steps {
          // Archive the APKs so that they can be downloaded from Jenkins
          archiveArtifacts "**/${APP_NAME}.apk"
          // Archive the ARR and POM so that they can be downloaded from Jenkins
          // archiveArtifacts "**/${APP_NAME}-${BUILD_TYPE}.aar, **/*pom-   default.xml*"
        }
      }
    
    
  }
 
}
