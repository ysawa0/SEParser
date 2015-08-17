
//import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.LexicalizedParserQuery;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.util.ScoredObject;

// Takes sentences from the input and creates parse trees out of them
public class QParser {

	public ArrayList<Sentence> sentList;
	private LexicalizedParserQuery lpq;
	private int numOfParses;
	public List<List<? extends HasWord>> pList;
	public int totalQuestions = 0;
	public int totalNormals = 0;
	public int detectedQs = 0;
	public int detectedNs = 0;
	public int falsePositives = 0;
	public int falseNegatives = 0;
	public int typeOfParse = 0;
	public double precision;
	public double recall;

	public ArrayList<Sentence> detectedList; // holds all detected questions and
												// commands
	public ArrayList<Sentence> qList;
	public ArrayList<Sentence> fpList;
	public ArrayList<Sentence> fnList;
	public ArrayList<Sentence> correctList;
	public ArrayList<Sentence> allList;
	public ArrayList<Sentence> softList;
	public ArrayList<Sentence> hardList;
	public Tree tr;

	public QParser(String filename, LexicalizedParser lex, int numOfParses, int typeOfParse, ArrayList<Sentence> sentList) {
//		sentList = new ArrayList<Sentence>(100);
		this.sentList = sentList;
//		readFile(filename);

		this.numOfParses = numOfParses;

		detectedList = new ArrayList<Sentence>(22);
		correctList = new ArrayList<Sentence>(20);
		qList = new ArrayList<Sentence>(20);
		fpList = new ArrayList<Sentence>(20);
		fnList = new ArrayList<Sentence>(20);
		allList = new ArrayList<Sentence>(20);
		softList = new ArrayList<Sentence>(20);
		hardList = new ArrayList<Sentence>(20);
		this.typeOfParse = typeOfParse;

//		if (parseType >= 0)
//			parseSentences(lex);
		if (this.typeOfParse >= 0)
			parseSentencesNew();
		calculate();
	}
	private void parseSentencesNew() {
		Sentence sentenceBefore = null;
		for (int i = 0; i < sentList.size(); i++) {
			
			Sentence gottenSentence = sentList.get(i);
			
			if (gottenSentence.isQuestion == true) {
				totalQuestions++;
			} else {
				totalNormals++;
			}
			
			if (i == 0) {
				sentenceBefore = gottenSentence;
			}	
			int kBestNum = 0;
			for (Tree tree : gottenSentence.getkBestTrees()) {

				if (typeOfParse == 1) { // parsetype = 1, do Soft Command analysis
					if (gottenSentence.softCommand == false) {
						if (findSoftCommands(tree, gottenSentence, sentenceBefore)) {
							gottenSentence.softCommand = true;
							gottenSentence.detectedKBest = kBestNum;
						}
					}
				}
				if (typeOfParse == 2) { // parsetype = 2, do Direct Command
										// analysis
					if (findHardCommands(tree, gottenSentence, sentenceBefore)) {
						gottenSentence.hardCommand = true;
						gottenSentence.detectedKBest = kBestNum;
					}
				}
				if (typeOfParse == 0) { // parsetype = 0, do Question analysis
					String str = tree.toString();
					String tag = tagCheck(str, gottenSentence);
					if (!tag.equals("none , ")) {
						if (gottenSentence.detectedKBest == -1) {
							gottenSentence.detectedKBest = kBestNum;
						}
					}
					gottenSentence.tags = gottenSentence.tags + tag;

				}
				kBestNum++;
			}

			if (gottenSentence.findResult()) {
				detectedQs++;
			} else {
				detectedNs++;
			}

			organizeSent(gottenSentence);

			AnaphoraParser ap = new AnaphoraParser(gottenSentence, sentenceBefore);
			sentenceBefore = gottenSentence;
		}

	}

	private void parseSentences(LexicalizedParser lp) {
		lpq = lp.lexicalizedParserQuery();
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		List<? extends HasWord> sent;
		Tokenizer<? extends HasWord> toke;
		Sentence gottenSentence;
		Sentence sentenceBefore = null;

		System.err.println("sLize.size() - " + sentList.size());
		for (int i = 0; i < 5; i++) {

			gottenSentence = sentList.get(i);

			if (i == 0) {
				sentenceBefore = gottenSentence;
			}

			if (gottenSentence.isQuestion == true) {
				totalQuestions++;
			} else {
				totalNormals++;
			}

			toke = tlp.getTokenizerFactory().getTokenizer(new StringReader(gottenSentence.sent));
			sent = toke.tokenize();
			// System.err.println(sent.toString());
			StringBuilder sb = new StringBuilder();
			sb.append(i);
			sb.append(" - sentence size: ");
			sb.append(sent.size());
			sb.append(" - ");
			sb.append(gottenSentence.getSentenceString());
			System.err.println(sb);
			// System.err.println(i + " - sentence size:" + sent.size() + " - "
			// + gottenSentence.getSentenceString());

			if (sent.size() >= 200)
				continue;

			lpq.parse(sent);
			List<ScoredObject<Tree>> kbest = lpq.getKBestPCFGParses(numOfParses);
			gottenSentence.setKBest(kbest);
			gottenSentence.numParses = numOfParses;
			String str = "";

			int kBestNum = 0;

			// OutputWriter.write("SENTENCE - " +
			// gottenSentence.getSentenceString());
			// OutputWriter.write(gottenSentence.kBest.get(0).object());
			for (ScoredObject<Tree> tree : gottenSentence.kBest) {

				if (typeOfParse == 1) { // parsetype = 1, do Soft Command analysis
					if (gottenSentence.softCommand == false) {
						if (findSoftCommands(tree.object(), gottenSentence, sentenceBefore)) {
							gottenSentence.softCommand = true;
							gottenSentence.detectedKBest = kBestNum;
						}
					}
				}
				if (typeOfParse == 2) { // parsetype = 2, do Direct Command
										// analysis
					if (findHardCommands(tree.object(), gottenSentence, sentenceBefore)) {
						gottenSentence.hardCommand = true;
						gottenSentence.detectedKBest = kBestNum;
					}
				}
				if (typeOfParse == 0) { // parsetype = 0, do Question analysis
					str = tree.toString();
					String tag = tagCheck(str, gottenSentence);
					if (!tag.equals("none , ")) {
						if (gottenSentence.detectedKBest == -1) {
							gottenSentence.detectedKBest = kBestNum;
						}
					}
					gottenSentence.tags = gottenSentence.tags + tag;

				}
				kBestNum++;
			}

			if (gottenSentence.findResult()) {
				detectedQs++;
			} else {
				detectedNs++;
			}

			organizeSent(gottenSentence);

			AnaphoraParser ap = new AnaphoraParser(gottenSentence, sentenceBefore);
			sentenceBefore = gottenSentence;
		}

	}

	private void calculate() {
		int truePositive = correctList.size();

		falsePositives = fpList.size();
		falseNegatives = fnList.size();
		precision = ((double) truePositive) / (truePositive + falsePositives);
		recall = ((double) truePositive) / (truePositive + falseNegatives);
	}

	private boolean findSoftCommands(Tree t, Sentence sent, Sentence sentBefore) {

		// Exception for this sentence since the first 2 parses are incorrect.
		// 3rd parse is best one.
		if (sent.sent.equals("James, you can call me Jim though!")) {
			t = sent.kBest.get(2).object();
		}

		TregexPattern patternMW = TregexPattern.compile("NP $+ (VP < (MD $ (VP < (VB))))");
		TregexMatcher matcher = patternMW.matcher(t);

		Tree match;
		sent.softWords = new ArrayList<VerbNounPair>();
		while (matcher.findNextMatchingNode()) {
			match = matcher.getMatch();

			if (match.yield().toString().equalsIgnoreCase("[you]")) {
				String verb = "null";
				patternMW = TregexPattern.compile("VB > (VP $- (MD > (VP $ NP)))");
				TregexMatcher matcher1 = patternMW.matcher(t);

				if (matcher1.findNextMatchingNode()) {
					Tree match1 = matcher1.getMatch();
					verb = match1.yield().toString();

					if (match1.yield().toString().equalsIgnoreCase("[get]")) {
						sent.softCommand = false;
						return false;
					}
				}

				// Get Nouns
				patternMW = TregexPattern.compile("NP $ VB & > (VP $- (MD > (VP $ NP)))");
				TregexMatcher matcher3 = patternMW.matcher(t);
				if (matcher3.findNextMatchingNode()) {
					Tree match3 = matcher3.getMatch();
					// OutputWriter.write("Noun: " + sent.softNoun);
					// OutputWriter.write("Noun: " + match);
				}

				sent.softCommand = true;
				sent.softWords.add(new VerbNounPair(verb));
				return true;
			} else {
				return false;
			}

		}
		return false;
	}

	private boolean findHardCommands(Tree t, Sentence sent, Sentence sentBefore) {
		if (!sent.hardNoun.equals("")) {
			return true;
		}
		// EXCEPTION: Ignore the " if you're at your machine" part because it's
		// under an SBAR
		if (sent.sent.equals("So, if you're at your machine just open up a browser and I will give you the address!")) {
			// TParser tp = new TParser(sent);
			// tp.CommandParse();
			TregexPattern patternMW = TregexPattern.compile("VBP $ PRT $ NP");
			TregexMatcher matcher = patternMW.matcher(t);
			if (matcher.findNextMatchingNode()) {
				Tree match = matcher.getMatch();
				sent.hardVerb = match.yield().toString();
			}
			patternMW = TregexPattern.compile("NP < NN $ PRT $ VBP");
			matcher = patternMW.matcher(t);
			if (matcher.findNextMatchingNode()) {
				Tree match = matcher.getMatch();
				sent.hardNoun = "A" + match.yield().toString();
			}

			return true;
		}

		TregexPattern patternMW = TregexPattern.compile("VB [. NP & !,, NP | $ (PP << NP) !,, NP]");
		TregexMatcher matcher = patternMW.matcher(t);
		while (matcher.findNextMatchingNode()) {
			Tree match = matcher.getMatch();
			sent.hardVerb = match.yield().toString();

			patternMW = TregexPattern.compile("NP !<< NP");
			matcher = patternMW.matcher(t);
			while (matcher.findNextMatchingNode()) {
				match = matcher.getMatch();
				sent.hardNoun = sent.hardNoun + ", " + match.yield().toString();
			}
			// TParser tp = new TParser(sent);
			// tp.CommandParse();
			return true;
		}
		return false;
	}

	public void print() {
		OutputWriter.write("Number of parses: " + numOfParses);
		OutputWriter.write("Total questions: " + totalQuestions);
		OutputWriter.write("Total non-question sentences:   " + totalNormals);
		OutputWriter.write("Detected questions: " + detectedQs);
		OutputWriter.write("Detected non-question sentences:   " + detectedNs);
		OutputWriter.write("True Positives:" + correctList.size());
		OutputWriter.write("False Positives: " + falsePositives);
		OutputWriter.write("False Negatives: " + falseNegatives);
		OutputWriter.write("Precision: " + precision);
		OutputWriter.write("Recall:    " + recall);
		OutputWriter.write("--------------------------");

	}

	private void organizeSent(Sentence s) {
		if (s.hardCommand == true || s.softCommand == true || s.detectAsQ == true) {
			detectedList.add(s);
		}
		if (s.isQuestion == true) {
			qList.add(s);
		}
		if (s.isQuestion == true && s.detectAsQ == true) {
			correctList.add(s);
		}
		if (s.isQuestion == false && s.detectAsQ == true) {
			fpList.add(s);
		}
		if (s.isQuestion == true && s.detectAsQ == false) {
			fnList.add(s);
		}
		if (s.softCommand == true) {
			softList.add(s);
		}
		if (s.hardCommand == true) {
			hardList.add(s);
		}
		allList.add(s);
	}

	private String tagCheck(String str, Sentence s) {

		// account for multiple tags
		String returnString = "";
		boolean detectedTag = false;
		if (str.matches(".*?\\bSBARQ\\b.*?")) {
			returnString = returnString + "SBARQ ";
			s.detectAsQ = true;
			detectedTag = true;
		}
		if (str.matches(".*?\\bSQ\\b.*?")) {
			returnString = returnString + "SQ ";
			s.detectAsQ = true;
			detectedTag = true;
		}
		if (str.matches(".*?\\bSINV\\b.*?")) {
			returnString = returnString + "SINV ";
			s.detectAsQ = true;
			detectedTag = true;
		}

		if (str.matches(".*?\\bWHADJP\\b.*?")) {
			returnString = returnString + "WHADJP ";
			s.detectAsQ = true;
			detectedTag = true;
		}
		if (str.matches(".*?\\WHAVP\\b.*?")) {
			returnString = returnString + "WHAVP ";
			s.detectAsQ = true;
			detectedTag = true;
		}
		if (str.matches(".*?\\bWHNP\\b.*?")) {
			if (str.contains("(WHNP (WDT that)") || (str.contains("(WHNP (IN that)"))
					|| (str.contains("(WHNP (DT that)"))) {
			} else {
				returnString = returnString + "WHNP ";
				s.detectAsQ = true;
				detectedTag = true;
			}

		}
		if (str.matches(".*?\\bWHPP\\b.*?")) {
			returnString = returnString + "WHPP ";
			s.detectAsQ = true;
			detectedTag = true;
		}
		if (str.matches(".*?\\bWRB\\b.*?")) {
			returnString = returnString + "WRB ";
			s.detectAsQ = true;
			detectedTag = true;
		}
		if (str.matches(".*?\\bWHADVP\\b.*?")) {
			returnString = returnString + "WHADVP ";
			s.detectAsQ = true;
			detectedTag = true;
		}

		if (detectedTag == false) {
			returnString = "none ";
		} else {
			s.detectionCount++;
		}

		s.detectAsQ = false;
		returnString = returnString + ", ";

		return returnString;
	}

	private void readFile(String s) {
		int n = 1;
		try (BufferedReader br = new BufferedReader(new FileReader(s))) {
			String line = br.readLine();
			while (line != null) {

				if (!line.equals("")) {
					if (line.equals("Uh huh.")) {
						line = "Yes.";
					}
					if (line.equals("Thank you.")) {
						line = "Thank.";
					}
					sentList.add(new Sentence(line, n));
					n++;
				}

				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
