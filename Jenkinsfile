pipeline {
    agent emulateur
    stages {
        stage('Build') {
            steps {
                // Get some code from a GitHub repository
                git url: 'https://github.com/tahajadid/MASCover', branch: 'preprod'
            }
        }
    }
}
