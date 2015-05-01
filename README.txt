Social Engineering Parser
1. 
Place the text input to anaylze into input.txt

2. 
Make sure englishPCFG.ser.gz the Stanford Parser model file is in the main folder.
It should be included in the zip file.
It can also be downloaded from: http://nlp.stanford.edu/software/lex-parser.shtml#Download

3. 
Make sure stanford-parser.jar is in the main folder. It should be included in the zip file.
It can also be downloaded from: http://nlp.stanford.edu/software/lex-parser.shtml#Download

4.
Compile: javac -classpath "./stanford-parser.jar" ./src/*.java
Run: java -classpath "./stanford-parser.jar;./src" Main