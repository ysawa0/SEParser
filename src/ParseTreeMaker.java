package net.greenclay.SEParser;

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
import edu.stanford.nlp.util.ScoredObject;

// parseSentences - Given an ArrayList<Sentence> takes each one and gets the parse tree using Stanford Parser.
// readFile - Given a text file with sentences on each line, create a Sentence object out of each and store it in an ArrayList<Sentence>
public class ParseTreeMaker {

	public static ArrayList<Sentence> makeOneParseTree(String str, LexicalizedParser lp, int numOfParses) {
		ArrayList<Sentence> sentList = readString(str);
		return parseSentences(sentList, lp, numOfParses);
	}

	public static ArrayList<Sentence> makeParseTrees(String filename, LexicalizedParser lp, int numOfParses) {
		ArrayList<Sentence> sentList = readFile(filename);
		sentList = parseSentences(sentList, lp, numOfParses);

		return sentList;
	}

	// Given an ArrayList<Sentence> returned from readFile() use the PCFG Stanford Parser Model specified in LexicalizedParser lp
	// to find numOfParses many parse trees. These are called the kBest parse trees. Where k = numOfParses
	// After finding the kBest parses, store them in the Sentence object as Sentence.kBest
	private static ArrayList<Sentence> parseSentences(ArrayList<Sentence> sentList, LexicalizedParser lp, int numOfParses) {
		LexicalizedParserQuery lpq = lp.lexicalizedParserQuery();
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		List<? extends HasWord> sent;
		Tokenizer<? extends HasWord> toke;
		Sentence gottenSentence;
		Sentence sentenceBefore = null; // Store the previous sentence so it can be looked at for anaphora resolution

		for (int i = 0; i < sentList.size(); i++) {

			gottenSentence = sentList.get(i);

			toke = tlp.getTokenizerFactory().getTokenizer(new StringReader(gottenSentence.sent));
			sent = toke.tokenize();

//			// Ignore one word sentences
//			if (sent.size() == 2) {
//				sentList.remove(i);
//				continue;
//			}

			// Print each sentence and how many words are in it
//			StringBuilder sb = new StringBuilder();
//			sb.append(i);
//			sb.append(" - # of words: ");
//			sb.append(sent.size());
//			sb.append(" - ");
//			sb.append(gottenSentence.getSentenceString());
//			System.err.println(sb);


//			if (sent.size() >= 200)
//				continue;

			lpq.parse(sent);
			List<ScoredObject<Tree>> kbest = lpq.getKBestPCFGParses(numOfParses);
			gottenSentence.setKBest(kbest);
			gottenSentence.numParses = numOfParses;

			// Save the previous sentence in "sentenceBefore"
			// Do Anaphora resolution analysis by creating new AnaphoraParser object


		}

		return sentList;
	}

	private static ArrayList<Sentence> readString(String str) {
		ArrayList<Sentence> sentList = new ArrayList<Sentence>();
		sentList.add(new Sentence(str, 1));
		return sentList;
	}

	// Given a filename of the input text file, read it in line by line.
	// For each line a Sentence object is created and saved in an ArrayList<Sentence>
	// return the ArrayList at the end.
	private static ArrayList<Sentence> readFile(String filename) {
		ArrayList<Sentence> sentList = new ArrayList<Sentence>();
		int n = 1;
		
		//
		int numberOfLinesToParse = 10;
		//
		
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line = br.readLine();
			while (line != null) {
				if ( n > numberOfLinesToParse) break;
				if (!"".equals(line)) {
					line = preprocessLine(line);

					sentList.add(new Sentence(line, n));
					n++;
				}

				line = br.readLine();
			}
		} catch (IOException e) {
			System.err.println("Error in readFile()");
			e.printStackTrace();
		}
		System.out.println(sentList.size());
		return sentList;
	}
	
	// preprocess certain sentences/lines to make them parser friendly and easier to analyze
	private static String preprocessLine(final String line) {
		String preprocessedLine = line;
		if ("Uh huh.".equals(line)) {
			preprocessedLine = "Yes.";
		}
		if ("Thank you.".equals(line)) {
			preprocessedLine = "Thank.";
		}
		
		return preprocessedLine;
	}
}
