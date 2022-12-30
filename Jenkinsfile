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
    stage('Unit Test') {
        steps {
         // Execute your Unit Test
         sh './gradlew testStagingDebug'
        }
    }
  }

}
