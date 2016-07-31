# odML-HDF5-Converter
This is a  library for conversion of any metadata from odML to HDF5 format,and vice versa; along with transfer of raw data to NIX data model. The data and its metadata are linked as HDF5 via the library, which provides a tool to create, store and share electrophysiological data in standardized format.  

Getting started:<br>

Install maven libraries not available in the central repository:<br>
mvn install:install-file -Dfile=lib/EEGLoader_2.2.jar -DgroupId=cz.zcu.kiv.eeg -DartifactId=EEGLoader -Dversion=2.2 -Dpackaging=jar <br>
mvn install:install-file -Dfile=lib/nix-linux-x86_64.jar -DgroupId=org.gnode -DartifactId=nix-linux-x86_64 -Dversion=1.0 -Dpackaging=jar <br>
mvn install:install-file -Dfile=lib/odml.jar -DgroupId=odml.core -DartifactId=odml -Dversion=1.0 -Dpackaging=jar <br>


The jar files required are : <br>
1. EEGLoader_2.2.jar <br>
2. javacpp-1.1.jar <br>
3. jdom-1.1.3.jar <br>
4. nix-linux-x86_64.jar <br>
5. odml-java-lib-1.1.7.jar <br>
6. slf4j-api-1.7.21.jar <br>
7. slf4j-simple-1.7.21.jar <br>
8. joda-time-2.4.jar


The total number of channels is taken as the default channel number. There is provision to change the default channel number, as shown in the MainClass.java (example usage).
