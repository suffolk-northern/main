pipeline {
    agent {
        docker {
            image 'openjdk:8-jdk'
        }
    }
    stages {
        stage ('Build') {
            steps {
                sh '''
                    cd $WORKSPACE
                    wget https://bitbucket.org/xerial/sqlite-jdbc/downloads/sqlite-jdbc-3.21.0.jar
                    wget -O absolutelayout.jar http://bits.netbeans.org/maven2/org/netbeans/external/AbsoluteLayout/RELEASE65/AbsoluteLayout-RELEASE65.jar
                    javac -cp .:sqlite-jdbc-3.21.0.jar:absolutelayout.jar integration/Main.java 
                    '''
            }
        }
    }
}
