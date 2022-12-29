pipeline {
    
  agent { 
    node { label 'maître' }
  }

  stages {
    stage('Unit Test') {
        steps {
         // Execute your Unit Test
         sh './gradlew testStagingDebug'
        }
    }
 }

  post {
    always {
      archiveArtifacts(allowEmptyArchive: true, artifacts: 'app/build/outputs/apk/production/release/*.apk')
    }
  }

}
