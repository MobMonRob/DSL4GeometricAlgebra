#!/bin/bash
RED='\033[0;31m\033[1m'
GREEN='\033[0;32m\033[1m'
BLUE='\033[0;34m\033[1m'
NC='\033[0m' # No Color
execStep=true
installErrors=0

displayMsg()
{
 printf "\n$1\n"
 read -t 5 -p "(CTRL+C to cancel, S to skip this step, continuing in 5s) " skip
 printf "\n\n"
 if [[ $skip == [sS] ]]; then
  execStep=false
 else
  execStep=true
 fi
}

installationEval()
{
 installErrors=$((installErrors+1))
}

dependencyPath=$1
if [ -n "$dependencyPath" ]; then
 if [ ! -d "$dependencyPath" ]; then
  mkdir -p "$dependencyPath"
  if [ ! $? -eq 0 ]; then
   printf "$dependencyPath ${RED}could not be created${NC}!"
   exit 2
  fi
 fi
fi
cd "$dependencyPath"

displayMsg "First, we need ${BLUE}sudo to install a number of dependencies${NC}."

if $execStep; then
# Normal apt installs
sudo apt install git -y
sudo apt install build-essential -y
sudo apt install mingw-w64 mingw-w64-tools -y
sudo apt install cmake -y
sudo apt install wget -y
sudo apt install tar -y
	
# Install swig 4.0.1 for compatability with Ubuntu24
wget -O swig4.0.1_amd64.deb
https://launchpad.net/ubuntu/+archive/primary/+files/swig4.0_4.0.1-5build1_amd64.deb
sudo dpkg -i swig4.0.1_amd64.deb
rm -f swig4.0.1_amd64.deb

wget -O swig4.0.1_all.deb
https://launchpad.net/ubuntu/+archive/primary/+files/swig_4.0.1-5build1_all.deb
sudo dpkg -i swig4.0.1_all.deb
rm -f swig4.0.1_all.deb

sudo apt-mark hold swig
fi

displayMsg "Now, we use ${BLUE}git to clone (or update) necessary repositories${NC}."

if $execStep; then
git clone https://github.com/orat/ConformalGeometricAlgebra &> /dev/null || ( cd ConformalGeometricAlgebra && git pull && cd .. )
git clone https://github.com/orat/GeometricAlgebra &> /dev/null || ( cd GeometricAlgebra && git pull && cd .. )
git clone https://github.com/orat/Euclid3DViewAPI &> /dev/null || ( cd Euclid3DViewAPI && git pull && cd .. )
git clone https://github.com/orat/EuclidView3d &> /dev/null || ( cd EuclidView3d && git pull && cd .. )
git clone https://github.com/orat/SparseMatrix &> /dev/null || ( cd SparseMatrix && git pull && cd .. )
git clone https://github.com/orat/CGACasADi &> /dev/null || ( cd CGACasADi && git pull && cd .. )
git clone https://github.com/MobMonRob/JCasADi &> /dev/null || ( cd JCasADi && git pull && cd .. )
git clone https://github.com/MobMonRob/JNativeLibLoader &> /dev/null || ( cd JNativeLibLoader && git pull && cd .. )
git clone https://github.com/orat/GACalcAPI &> /dev/null || ( cd GACalcAPI && git pull && cd .. )
wget -O vecmath.jar https://jogamp.org/deployment/java3d/1.7.1-build-20200222/vecmath.jar
fi

displayMsg "Now, we ${BLUE}download and install GraalVM and Maven${NC}."

if $execStep; then
if mkdir GraalVM21 &> /dev/null; then
	wget -O graalvm21.tar.gz https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-21.0.2/graalvm-community-jdk-21.0.2_linux-x64_bin.tar.gz
	tar -xzf graalvm21.tar.gz -C ./GraalVM21 --strip-components=1
	rm -f graalvm21.tar.gz
fi
if mkdir Maven3.9.9 &> /dev/null; then
	wget -O maven.tar.gz https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz
	tar -xzf maven.tar.gz -C ./Maven3.9.9 --strip-components=1
	rm -f maven.tar.gz
fi
cd ./GraalVM21
export JAVA_HOME=$(pwd)
cd ../Maven3.9.9
export M2_HOME=$(pwd)
export PATH=$JAVA_HOME/bin:$M2_HOME/bin:$PATH
cd ..
fi


displayMsg "Finally, we ${BLUE}build the repositories with Maven${NC}."

if $execStep; then
mvn -v ||  { printf "\n${RED}Maven can't be executed${NC}. Try running the install script again without skipping the '${BLUE}download and install GraalVM and Maven${NC}' step or install it manually.\n"; exit 1; }
mvnInstallCmd="mvn --no-transfer-progress install"
cd ./SparseMatrix && $mvnInstallCmd || installationEval
cd ../GeometricAlgebra && $mvnInstallCmd || installationEval
mvn install:install-file -Dfile=../vecmath.jar -DgroupId=org.jogamp.java3d -DartifactId=vecmath -Dversion=1.7.1 -Dpackaging=jar -DgeneratePom=true || installationEval
cd ../EuclidView3d && $mvnInstallCmd || installationEval
cd ../Euclid3DViewAPI && $mvnInstallCmd || installationEval
cd ../ConformalGeometricAlgebra && $mvnInstallCmd || installationEval
cd ../JNativeLibLoader/NativeLibLoader && $mvnInstallCmd || installationEval
cd ../../GACalcAPI && $mvnInstallCmd || installationEval
cd ../JCasADi/JCasADi && $mvnInstallCmd || installationEval
cd ../../CGACasADi && $mvnInstallCmd || installationEval
fi

if [ $installErrors -eq 0 ]; then
 printf "\n${GREEN}All done!${NC} Now you can use DSL4GeometricAlgebra!\n"
 exit 0
else
 printf "\n${RED}$installErrors error(s) occured.${NC} Check the logs to see what went wrong.\n"
 exit 1
fi
