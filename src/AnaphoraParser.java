

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
		checkForThat(currentSent,findAllNP(cs),findAllNP(bs));
	}
	
	private void detectThat(Tree tree) {
		if(doesTreeMatchPattern(tree, "NP << (DT << that)")) {
			currentSent.setHasThat();
		}
	}
	
	// If tree matches the TRegex pattern return true
	// pattern - Tregex pattern
	// tree - Parse tree to match the pattern against
	private static boolean doesTreeMatchPattern(Tree tree, String pattern) {
		TregexPattern patternMW = TregexPattern.compile(pattern);
		TregexMatcher matcher = patternMW.matcher(tree);
		if (matcher.findNextMatchingNode()) {
			return true;
		} else {
			return false;
		}
	}
	
	// Look for "that" "it" or "you" in a sentence and if they appear store the NP right before them
	private void checkForThat(Sentence sent, ArrayList<String> a, ArrayList<String> b) {
		if(sent.getHasThat() == true) {
			String thatResolution = null;
			
			for (String str : b) {
				if (!str.contains("you") && !str.contains("that") && !str.contains("PRP it")) {
					thatResolution = str;
				}
			}
			
			for (String str : a) {
				if(str.contains("that")) {
					break;
				}
				if (!str.contains("you") && !str.contains("that") && !str.contains("[it]")) {
					thatResolution = str;
				}
				
			}
			//OutputWriter.write("THAT resolves to: " + thatResolution);
			currentSent.anaphoraResolution = thatResolution;
		}
	}
	
	// Find all NPs in a sentence and return them in an ArrayList
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
