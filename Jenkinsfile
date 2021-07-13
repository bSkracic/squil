node{
   stage('Git Checkout'){
       git branch: 'main', credentialsId: 'git-creds', url: 'https://github.com/bSkracic/squil'
   }
   stage('Mvn Package'){
     def mvnHome = tool name: 'maven-3', type: 'maven'
     def mvnCMD = "${mvnHome}/bin/mvn"
     sh "${mvnCMD} clean package"
   }
   stage('Docker Build'){
     sh 'docker build -t bskracic/squil-service:1.0.0 .'
   }
   stage('Docker Push'){
     withCredentials([string(credentialsId: 'docker-pwd', variable: 'dockerHubPwd')]) {
        sh "docker login -u bskracic -p ${dockerHubPwd}"
     }
     sh 'docker push bskracic/squil-service:1.0.0'
   }
   stage('AWS Deployment'){
     def dockerRun = 'docker run -p 8080:8081 -d --name squil-service -v /var/run/docker.sock:/var/run/docker.sock -v $(which docker):/usr/bin/docker bskracic/squil-service:1.0.0'
     sshagent(['dev-server']) {
       sh "ssh -o StrictHostKeyChecking=no ec2-user@3.65.33.203 ${dockerRun}"
     }
   }
}