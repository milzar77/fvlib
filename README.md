For more information see:
https://stackoverflow.com/questions/14013644/hosting-a-maven-repository-on-github
https://gist.github.com/fernandezpablo85/03cf8b0cd2e7d8527063



SET HOME_MYUSER=myuser

SET HOME_MYMVN=c:\dev\asf\maven

git clone git@github.com:milzar77/fvlib.git main
cd main/
git checkout mvn-repo

#remove old files

%HOME_MYMVN%\bin\mvn.cmd install:install-file -DgroupId=com.blogspot.fravalle -DartifactId=fvlib -Dversion=0.0.1-SNAPSHOT -Dpackaging=pom -DlocalRepositoryPath=. -DcreateChecksum=true -Dfile=%HOME_MYUSER%\git\fvlib\pom.xml -DpomFile=%HOME_MYUSER%\git\fvlib\pom.xml

%HOME_MYMVN%\bin\mvn.cmd install:install-file -DgroupId=com.blogspot.fravalle -DartifactId=fvlib-res -Dversion=0.0.1-SNAPSHOT -Dfile=%HOME_MYUSER%\git\fvlib\fvlib-res\target\fvlib-res-0.0.1-SNAPSHOT.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true
%HOME_MYMVN%\bin\mvn.cmd install:install-file -DgroupId=com.blogspot.fravalle -DartifactId=fvlib-res -Dversion=0.0.1-SNAPSHOT -Dfile=%HOME_MYUSER%\git\fvlib\fvlib-res\target\fvlib-res-0.0.1-SNAPSHOT.jar -Dpackaging=jar -DlocalRepositoryPath=. -DcreateChecksum=true -DpomFile=%HOME_MYUSER%\git\fvlib\fvlib-res\pom.xml


%HOME_MYMVN%\bin\mvn.cmd install:install-file -DgroupId=com.blogspot.fravalle -DartifactId=fvlib-util -Dversion=0.0.1-SNAPSHOT -Dfile=%HOME_MYUSER%\git\fvlib\fvlib-util\target\fvlib-util-0.0.1-SNAPSHOT.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true
%HOME_MYMVN%\bin\mvn.cmd install:install-file -DgroupId=com.blogspot.fravalle -DartifactId=fvlib-util -Dversion=0.0.1-SNAPSHOT -Dfile=%HOME_MYUSER%\git\fvlib\fvlib-util\target\fvlib-util-0.0.1-SNAPSHOT.jar -Dpackaging=jar -DlocalRepositoryPath=. -DcreateChecksum=true -DpomFile=%HOME_MYUSER%\git\fvlib\fvlib-util\pom.xml


%HOME_MYMVN%\bin\mvn.cmd install:install-file -DgroupId=com.blogspot.fravalle -DartifactId=fvlib-common -Dversion=0.0.1-SNAPSHOT -Dfile=%HOME_MYUSER%\git\fvlib\fvlib-common\target\fvlib-common-0.0.1-SNAPSHOT.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true
%HOME_MYMVN%\bin\mvn.cmd install:install-file -DgroupId=com.blogspot.fravalle -DartifactId=fvlib-common -Dversion=0.0.1-SNAPSHOT -Dfile=%HOME_MYUSER%\git\fvlib\fvlib-common\target\fvlib-common-0.0.1-SNAPSHOT.jar -Dpackaging=jar -DlocalRepositoryPath=. -DcreateChecksum=true -DpomFile=%HOME_MYUSER%\git\fvlib\fvlib-common\pom.xml



%HOME_MYMVN%\bin\mvn.cmd install:install-file -DgroupId=com.blogspot.fravalle -DartifactId=fvlib-bl -Dversion=0.0.1-SNAPSHOT -Dfile=%HOME_MYUSER%\git\fvlib\fvlib-bl\target\fvlib-bl-0.0.1-SNAPSHOT.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true
%HOME_MYMVN%\bin\mvn.cmd install:install-file -DgroupId=com.blogspot.fravalle -DartifactId=fvlib-bl -Dversion=0.0.1-SNAPSHOT -Dfile=%HOME_MYUSER%\git\fvlib\fvlib-bl\target\fvlib-bl-0.0.1-SNAPSHOT.jar -Dpackaging=jar -DlocalRepositoryPath=. -DcreateChecksum=true -DpomFile=%HOME_MYUSER%\git\fvlib\fvlib-bl\pom.xml


%HOME_MYMVN%\bin\mvn.cmd install:install-file -DgroupId=com.blogspot.fravalle -DartifactId=fvlib-web -Dversion=0.0.1-SNAPSHOT -Dfile=%HOME_MYUSER%\git\fvlib\fvlib-web\target\fvlib-web-0.0.1-SNAPSHOT.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true
%HOME_MYMVN%\bin\mvn.cmd install:install-file -DgroupId=com.blogspot.fravalle -DartifactId=fvlib-web -Dversion=0.0.1-SNAPSHOT -Dfile=%HOME_MYUSER%\git\fvlib\fvlib-web\target\fvlib-web-0.0.1-SNAPSHOT.jar -Dpackaging=jar -DlocalRepositoryPath=. -DcreateChecksum=true -DpomFile=%HOME_MYUSER%\git\fvlib\fvlib-web\pom.xml



$ git add -A . && git commit -m "released version 0.0.1-SNAPSHOT with original POM files"
git push origin mvn-repo