#ODT2HTML

experiment to parse a office document (either ms-word or libreoffice) using the JAVA connector to LibreOffice's UNO API, and generate custom HTML. 

reference:
http://api.libreoffice.org/

##setup
LibreOffice API requires some development utilities. install like this: 
sudo apt-get install libreoffice-dev

move to the sdk library and execute the following script to define env variables: 
cd /usr/lib/libreoffice/sdk

./setsdkenv_unix

test the result of the script with this: 
echo $OO_SDK_NAME

important! the script create variables only in a temporal way. you will need to execute the script every time or use another solution (like moving all the vars to your .bashrc)

jars path: 
/usr/lib/libreoffice/program/classes/

