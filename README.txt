SEParser -- Social Engineering Parser using Natural Language Processing

Check it out online! 
http://separser.greenclay.net/



Stanford Parser website:
http://nlp.stanford.edu/software/lex-parser.shtml#Download

Project Dependencies:
Stanford Parser ver 3.5.2
http://nlp.stanford.edu/software/stanford-parser-full-2015-04-20.zip

englishPCFG.ser.gz
http://nlp.stanford.edu/software/stanford-english-corenlp-2016-01-10-models.jar

I have included examples showing how to use the Stanford Parser API in example_parse.java

1. 
Download both files above
Place the text input to anaylze into input.txt

2. 
Extract the file stanford-english-corenlp-2016-01-10-models.jar 
Look in the "edu/stanford/nlp/models/lexparser/" directory
Copy englishPCFG.ser.gz
Make sure englishPCFG.ser.gz the Stanford Parser model file is in the base project directory

3. 
Make sure stanford-parser.jar is in the base directory. 

4. Compile and run
Windows:
javac -classpath "./stanford-parser.jar" ./src/*.java
java -classpath "./stanford-parser.jar;./src" Main

Linux/OSX:
javac -classpath "./stanford-parser.jar" ./src/*.java
java -classpath "./stanford-parser.jar:./src" Main
