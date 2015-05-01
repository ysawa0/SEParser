

import java.util.ArrayList;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

// Resolves words like "that" or "it" to words before it in a sentence.
// aka Anaphora resolution
public class AnaphoraParser {
	
	Sentence currentSent;
	Sentence beforeSent;
	public AnaphoraParser(Sentence cs, Sentence bs) {
		currentSent = cs;
		beforeSent = bs;
		ArrayList<Tree> kBestTrees = currentSent.getkBestTrees();
		for (Tree t : kBestTrees) {
			detectThat(t);
		}
		checkThat(currentSent,findAllNP(cs),findAllNP(bs));
	}
	
	private void detectThat(Tree tree) {
		if(doesTreeMatchPattern(tree, "NP << (DT << that)")) {
			currentSent.setHasThat();
		}
	}
	
	private static boolean doesTreeMatchPattern(Tree tree, String pattern) {
		TregexPattern patternMW = TregexPattern.compile(pattern);
		TregexMatcher matcher = patternMW.matcher(tree);
		Tree match = null;
		if (matcher.findNextMatchingNode()) {
			match = matcher.getMatch();
			return true;
		} else {
			return false;
		}
	}
	
	private void checkThat(Sentence sent, ArrayList<String> a, ArrayList<String> b) {
		if(sent.getHasThat() == true) {
			String thatResolution = null;
			//System.out.println("Sentence Before " + beforeSent.sent);
			
			for (String str : b) {
				//System.out.println(str);
				if (!str.contains("you") && !str.contains("that") && !str.contains("PRP it")) {
					thatResolution = str;
				}
			}
			
			for (String str : a) {
				//System.out.println(str);
				if(str.contains("that")) {
					break;
				}
				if (!str.contains("you") && !str.contains("that") && !str.contains("[it]")) {
					thatResolution = str;
				}
				
			}
			//System.out.println("THAT resolves to: " + thatResolution);
			currentSent.anaphoraResolution = thatResolution;

		}
	}
	
	private ArrayList<String> findAllNP(Sentence s) {
		if (s.detectedKBest == -1) {
			return null;
		}
		
		ArrayList<String> NPs = new ArrayList<String>();
		TregexPattern patternMW = TregexPattern.compile("NP"); 
		TregexMatcher matcher = patternMW.matcher(s.kBest.get(s.detectedKBest).object());
		
		Tree match;
		
		while (matcher.findNextMatchingNode()) {
			match = matcher.getMatch();
			NPs.add(match.yield().toString());
		}
		
		return NPs;
	}
}
