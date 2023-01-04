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
    

    stage('Compile') {
      steps {
        // Compile the app and its dependencies
        sh './gradlew compileReleaseSources'
      }
    }




    
  }
 
}
