pipeline {
    agent emulateur
    stages {
        stage('Build') {
            steps {
                // Get some code from a GitHub repository
                git url: 'https://ocp-gitlab.inwi.cloud/inwi/workspaces/digitalworkspace/acteur-marche/devsecops/tests/tnr.git', branch: 'feature_develop'
            }
        }
    }
}
