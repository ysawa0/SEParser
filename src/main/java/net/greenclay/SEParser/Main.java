package net.greenclay.SEParser;

import java.util.ArrayList;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class Main {
	private static ArrayList<String> overallResults = new ArrayList<String>();
	private static String topicBlacklistFileName; 
	private static LexicalizedParser lp;
	
	// Main method 
	// input - the input text file. Each sentence should be on its own line and end with a period
	// lp - the Parser Model

//	public static void main(String[] args) {
//		TopicBlacklist.getTopicBlacklistFromMongo();
//	}

	public static void main(String[] args) {
		String dir = System.getProperty("user.dir");
		if (dir.contains("/py-SEParser/SEParser")) {
			topicBlacklistFileName = dir + "/topic_blacklist.txt";
			dir = dir + "/englishPCFG.ser.gz";
		} else {
			topicBlacklistFileName = dir + "/SEParser/topic_blacklist.txt";
			dir = dir + "/SEParser/englishPCFG.ser.gz";
		}
		// System.out.println("dir:   " + dir);
		topicBlacklistFileName = "topic_blacklist.txt";
		lp = LexicalizedParser.loadModel("englishPCFG.ser.gz");
		String input = args[0]; // Read sentence from args
		int numParses = 3;
		ArrayList<Sentence> sentList = ParseTreeMaker.makeOneParseTree(input, lp, numParses);
		OutputWriter.write(sentList.get(0).getkBestTrees().get(0));
		getJSONResult(input, numParses, sentList);
		// questionParse(input, numParses, sentList);
		// softCommandParse(input, numParses, sentList);
		// hardCommandParse(input, numParses, sentList);
		OutputWriter.printAll();
	}

	public static void getTopicBlacklist() {

    }

	public static void getJSONResult(String sentence, int numParses, ArrayList<Sentence> sentList) {
		questionParse(sentence, numParses, sentList).get(0);
		softCommandParse(sentence, numParses, sentList).get(0);
		hardCommandParse(sentence, numParses, sentList).get(0);

		Sentence sentObj = sentList.get(0);

		ArrayList<VerbNounPair> vnpairs = new ArrayList<VerbNounPair>();
		if (sentList.get(0).isMalicious) vnpairs.addAll(sentObj.vnpairs);
		ResultSentence rs = new ResultSentence(sentObj.sent, sentObj.kBestTrees, numParses, vnpairs);
		if (sentObj.detectAsQ) rs.question = true;
		if (sentObj.softCommand) rs.softCommand = true;
		if (sentObj.hardCommand) rs.hardCommand = true;
		if (sentObj.isMalicious) rs.malicious = true;
		System.out.println(rs.makeJson());
	}

	public static void oldmain(String[] args) {
		long startTime = System.currentTimeMillis();
		String input = "input.txt"; // Example input for DEFCON presentation
		lp = LexicalizedParser.loadModel("/Users/ysawa/b/academic/SEParser/englishPCFG.ser.gz");

		int numOfParses = 3;

		OutputWriter.writeOverallResults("\n# of parses used: " + numOfParses);
		ArrayList<Sentence> sentList = ParseTreeMaker.makeParseTrees(input, lp, numOfParses);
		OutputWriter.writeOverallResults("# of sentences total: " + sentList.size());
		questionParse(input, 1, sentList);
		softCommandParse(input, 1, sentList);
		hardCommandParse(input, 1, sentList);

		long endTime = System.currentTimeMillis();
		double totalTime = endTime - startTime;
		totalTime = totalTime/1000.;
		OutputWriter.writeOverallResults("Total execution time: " + Double.toString(totalTime) + " seconds");

		OutputWriter.printAll();
	}
	
	// Analyzes the input for questions and see if any of them are malicious
	public static ArrayList<Sentence> questionParse(String filename, int numParses, ArrayList<Sentence> sentList) {
		QParser qp = new QParser(filename,lp, numParses, 0, sentList);
		TParser tp;
		OutputWriter.write("\n---Question parse start--- " + numParses + " parses \n");
		OutputWriter.write("Questions detected: " + qp.qList.size());
		TopicBlacklist pairCheck = new TopicBlacklist(topicBlacklistFileName);

		for(Sentence s :qp.detectedList) {
			tp = new TParser(s);
			tp.SQParse();
			printSentences(s);
			pairCheck.setSentence(s);
			pairCheck.blacklistCheck();
		}

		int numMalicious = 0;
		for (Sentence sent : qp.sentList) {
			if (sent.isMalicious()) numMalicious++;
		}

		OutputWriter.writeOverallResults("# questions: " + qp.qList.size());
		OutputWriter.writeOverallResults("# of malicious questions: " + numMalicious);
		return qp.sentList;
	}

	// Analyzes the input for Soft Commands and see if any of them are malicious
	public static ArrayList<Sentence> softCommandParse(String filename, int numParses, ArrayList<Sentence> sentList) {
		OutputWriter.write("\n---Soft Commands parse start--- " + numParses + " parses \n");

		QParser qp = new QParser(filename,lp, numParses, 1, sentList);
		OutputWriter.write("Soft commands detected: " + qp.softList.size());
		TopicBlacklist pairCheck = new TopicBlacklist(topicBlacklistFileName);

		for(Sentence s : qp.softList) {
			TParser tp = new TParser(s);
			tp.CommandParse();
			printSentences(s);
			pairCheck.setSentence(s);
			pairCheck.blacklistCheck();

		}
		int numMalicious = 0;
		for (Sentence sent : qp.sentList) {
			if (sent.isMalicious()) numMalicious++;
		}

		OutputWriter.writeOverallResults("# soft commands: " + qp.softList.size());
		OutputWriter.writeOverallResults("# of malicious soft commands: " + numMalicious);
		return qp.sentList;
	}

	// Analyzes the input for Hard Commands and see if any of them are malicious
	public static ArrayList<Sentence> hardCommandParse(String filename, int numParses, ArrayList<Sentence> sentList) {
		OutputWriter.write("\n---Direct Commands parse start--- " + numParses + " parses \n");

		QParser qp = new QParser(filename,lp, numParses,2, sentList);
		OutputWriter.write("Direct commands detected: " + qp.hardList.size());
		TopicBlacklist pairCheck = new TopicBlacklist(topicBlacklistFileName);

		for(Sentence s : qp.hardList) {
			TParser tp = new TParser(s);
			tp.CommandParse();

			printSentences(s);
			pairCheck.setSentence(s);
			pairCheck.blacklistCheck();
		}

		int numMalicious = 0;
		for (Sentence sent : qp.sentList) {
			if (sent.isMalicious()) numMalicious++;
		}

		OutputWriter.writeOverallResults("# direct commands: " + qp.hardList.size());
		OutputWriter.writeOverallResults("# of malicious direct commands: " + numMalicious);
		return qp.sentList;
	}

	public static void printSentences(Sentence s) {
		OutputWriter.write("\nSentence: " + s.sent);
		if (s.sent.equals("Click run.")) {
//			OutputWriter.write(s.kBest.get(1).object().skipRoot());
		}
		else {
//			OutputWriter.write(s.kBest.get(0).object().skipRoot());
		}
	}
		


}
