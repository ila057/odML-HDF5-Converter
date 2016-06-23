# odML-HDF5-converter
This is a  library for conversion of any metadata from odML to HDF5 format,and vice versa; along with transfer of raw data to NIX data model. The data and its metadata are linked as HDF5 via the library, which provides a tool to create, store and share electrophysiological data in standardized format.  

The jar files required are :
EEGLoader_2.2.jar
javacpp-1.1.jar
jdom-2.0.6.jar
nix-linux-x86_64.jar


In case odML java library is added for use (is not currently used), the following jar files will also be required:
odml.jar
slf4j-api-1.7.21.jar
slf4j-simple-1.7.21.jar

The total number of channels is taken as the default channel number.
