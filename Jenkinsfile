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
    
    stage ("Unit Test"){
      steps {
        sh './gradlew test'
      }
    }

      
    stage('Build APK') {
      steps {
        // Finish building and packaging the APK
        sh 'ls -ltr'
        sh 'pwd'
        sh 'touch local.properties'
        sh './gradlew clean'
        sh './gradlew androidDependencies'
        sh './gradlew assembleDebug'
        // Archive the APKs so that they can be downloaded from Jenkins
        archiveArtifacts '**/*.apk'
      }
   }

   stage ('Generate release'){
      steps {
        sh 'ls -ltr'
        sh 'touch local.properties'
        sh './gradlew assembleRelease'
      }
   }
    
    
   stage ('App Distribution'){
      steps {
        sh "./gradlew assembleRelease appDistributionUploadRelease"
        // sh 'ls /var/jenkins/workspace/cteurs-android-dev-multi_develop/app/build/outputs/apk/release/'
      }
   }


    
  }
 
}
