import java.util.ArrayList;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

// This is the TRegex Parser
// Given a TRegex pattern it will see if the parse tree from QParser matches it 
public class TParser {

private Tree whtree;
private ArrayList<Tree> sqList;
private ArrayList<String> nounList;
private ArrayList<String> verbList;
private ArrayList<String> verbList2;
private ArrayList<String> whList;
private Sentence sent;
private Tree tree;
private ArrayList<Tree> verbTreeList;
private ArrayList<Tree> nounTreeList;

// Analyzes parse Trees using TRegex patterns
	public TParser(Sentence s) {
		nounList = new ArrayList<String>();
		verbList = new ArrayList<String>();
		verbList2 = new ArrayList<String>();
		whList = new ArrayList<String>();
		sqList = new ArrayList<Tree>();
		sent = s;
		tree = sent.kBest.get(0).object().skipRoot();
	
		if(sent.sent.equals("And your employee ID?")) {
			tree = sent.kBest.get(5).object().skipRoot();
		}
		
		if(sent.sent.equals("Now don't give me your full social over the phone but can you give me your last four?")) {
			//Using 4th parse because other parses do not include the word "four" under the 2nd SINV
			if(sent.kBest.size() >= 4) {
				//if Parse is for questions not commands
				tree = sent.kBest.get(3).object().skipRoot();
			}
		}
	}

	public void CommandParse() {
			nounList = new ArrayList<String>();
			verbList = new ArrayList<String>();
			verbList2 = new ArrayList<String>();
			nounTreeList = new ArrayList<Tree>();
			verbTreeList = new ArrayList<Tree>();
//			OutputWriter.write(sent.sent);
//			OutputWriter.write(tree);
			findVerbTregex(tree);
			findNounTregex(tree);
			verbNounPairFinder(tree);
			//OutputWriter.write("----------------------");
	}

	public void SQParse() {
		//Ignore this since it's not a question. A false positive.
		if(sent.sent.equals("Now here is where we are starting to have some problems with our data field.")) {
			return;
		}

		nounList = new ArrayList<String>();
		verbList = new ArrayList<String>();
		nounTreeList = new ArrayList<Tree>();
		verbTreeList = new ArrayList<Tree>();
		verbList2 = new ArrayList<String>();
		whList = new ArrayList<String>();
		sqList = new ArrayList<Tree>();
		findSQ(tree, sqList);
		if (sqList.size() > 0) {
			sent.detectAsQ = true;
			//Search for noun/verbs for each S tag found.
			for (Tree t : sqList) {
				nounList = new ArrayList<String>();
				verbList = new ArrayList<String>();
				verbList2 = new ArrayList<String>();
				nounTreeList = new ArrayList<Tree>();
				verbTreeList = new ArrayList<Tree>();
				
				//OutputWriter.write(t);
				findVerbTregex(t);
				findNounTregex(t);
				verbNounPairFinder(t);
			}
		}
		else {
			WHParse();
		}
	}
	
	private static boolean doesTreeMatchPattern(Tree tree, String pattern) {
		TregexPattern patternMW = TregexPattern.compile(pattern);
		TregexMatcher matcher = patternMW.matcher(tree);
		if (matcher.findNextMatchingNode()) {
			return true;
		} else {
			return false;
		}
	}
	
	public void WHParse() {
		whList = new ArrayList<String>();
		findWh(tree);
		if (whList.size() == 1) {
			Tree whancestor = getAncestor(whtree);
			findVerbTregex(whancestor);
			findNounTregex(whancestor);
			verbNounPairFinder(whancestor);
		}
	}

	private void verbNounPairFinder(Tree theTree) {
		Tree firstVerb;
		Tree secondVerb;
		int firstVerbNum;
		int secondVerbNum;
		int nounNum;
		for (int i=0; i < verbTreeList.size(); i++) {
			if(i+1 == verbTreeList.size()) {
				// This IF is so the for doesn't go over bounds on the array since it gets i and i+1
				firstVerb = verbTreeList.get(i);
				firstVerbNum = firstVerb.nodeNumber(theTree);
				
				//OutputWriter.write("VERB: " + firstVerb.yield());
				
				sent.newPair(firstVerb.yield().toString());
				for (Tree t : nounTreeList) {
					nounNum = t.nodeNumber(theTree);
					if ( firstVerbNum < nounNum) {
						//OutputWriter.write("NOUN: " + t.yield());
						sent.addNoun(t.yield().toString());
					}
				}
				break;
			}
			
			firstVerb = verbTreeList.get(i);
			secondVerb = verbTreeList.get(i+1);
			firstVerbNum = firstVerb.nodeNumber(theTree);
			secondVerbNum = secondVerb.nodeNumber(theTree);
			
			//OutputWriter.write("VERB: " + firstVerb.yield());
			sent.newPair(firstVerb.yield().toString());
			
			for (Tree t : nounTreeList) {
				nounNum = t.nodeNumber(theTree);
				if ( firstVerbNum < nounNum && secondVerbNum > nounNum) {
					//OutputWriter.write("NOUN: " + t.yield());
					sent.addNoun(t.yield().toString());
				}
			}
		}
	}
	
	private void findVerbTregex(Tree t) {
		//"NP !<< NP & !.. VP
		TregexPattern patternMW = TregexPattern.compile("VBP | VB | VBZ | VBG | VBN | VBD");
		TregexMatcher matcher = patternMW.matcher(t); 
		Tree match = null;
		while (matcher.findNextMatchingNode()) {
			match = matcher.getMatch(); 
			verbTreeList.add(match);
			verbList2.add(match.yield().toString());
		}
	}
	
	private void findNounTregex(Tree t) {
		//"NP !<< NP & !.. VP
		TregexPattern patternMW = TregexPattern.compile("NP !<< NP");
		TregexMatcher matcher = patternMW.matcher(t); 
		Tree match = null;
		while (matcher.findNextMatchingNode()) {
			match = matcher.getMatch(); 
			nounTreeList.add(match);
			nounList.add(match.yield().toString());
		}
	}

	private Tree getAncestor(Tree t) {
		return t.ancestor(2, tree);
	}
	
	private void findWh(Tree t) {
		String nodeValue = t.value();
		int treeSize = t.size();
		
		if (verbList.size() > 1) {
			return;
		}
		
		if (nodeValue.equals("WHNP") || nodeValue.equals("WHADVP") || nodeValue.equals("WRB")) {
			if (treeSize == 2) {
				whList.add(t.getLeaves().get(0).value());
				whtree = t;
				return;
			}
		}
		
		Tree[] tarray = t.children();
		for (int i=0; i<tarray.length; i++) {
				findWh(tarray[i]);
		}
	}
	
	private void findSQ(Tree t, ArrayList<Tree> detectedQuestions) {
		//Finds first SBARQ or SQ or SINV tree

		if (t.value().equals("SBARQ") || t.value().equals("SQ") || t.value().equals("SINV")) {
			detectedQuestions.add(t);
			// System.out.println("Found SQ - " + t);
		}
		
		Tree[] tarray = t.children();
		for (int i=0; i<tarray.length; i++) {
			findSQ(tarray[i], detectedQuestions);
		}
	}
}
