import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

import edu.stanford.nlp.parser.lexparser.LexicalizedParserQuery;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.util.ScoredObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

/*
Example code to get parse trees for one sentence.
To Compile and run 

For Linux, Mac OS X:
javac -cp "./stanford-parser.jar" example_parse.java
java -cp "./stanford-parser.jar:./" example_parse

On Windows:
javac -cp "./stanford-parser.jar" example_parse.java
java -cp "./stanford-parser.jar;./" example_parse

Sample output:
Loading parser from serialized file englishPCFG.ser.gz ...  done [0.4 sec].
(ROOT (SQ (MD Can) (NP (PRP you)) (VP (VB give) (NP (PRP me)) (NP (PRP$ your) (JJ social))) (. ?)))
Found some noun phrases:
(NP (PRP you))
(NP (PRP me))
(NP (PRP$ your) (JJ social))

(ROOT (SQ (MD Can) (NP (PRP you)) (VP (VB give) (NP (PRP me)) (NP (PRP$ your) (NN social))) (. ?)))
Found some noun phrases:
(NP (PRP you))
(NP (PRP me))
(NP (PRP$ your) (NN social))

Is the sentence a question: true
*/

public class example_parse {

    public static void main(String[] args) {
        // Load the parser model and intialize parser objects
        LexicalizedParser lp = LexicalizedParser.loadModel("englishPCFG.ser.gz");
        LexicalizedParserQuery lpq = lp.lexicalizedParserQuery();
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();

        // String sentence = "I'm a test sentence.";
        String sentence = "Can you give me your social?";
        // String sentence = "If you could send me the certificate I would appreciate it.";

        // Parse the sentence
        Tokenizer<? extends HasWord> toke = tlp.getTokenizerFactory().getTokenizer(new StringReader(sentence));
        List<? extends HasWord> sent = toke.tokenize();
        lpq.parse(sent);

        // Get the results. the kBestTrees are the top k best parse trees for the sentence.
        int numOfParses = 2; // Get the top 2 parse trees
        List<ScoredObject<Tree>> kbest = lpq.getKBestPCFGParses(numOfParses);

        // Convert it to an ArrayList of Tree
        ArrayList<Tree> kBestTrees = getkBestTrees(kbest);

        for (Tree t : kBestTrees) {
            System.out.println(t);

            // Find Nouns (NPs) in the parse tree
            ArrayList<Tree> nounList = findNounPhrases(t);
            
            System.out.println("Found some noun phrases:");
            for (Tree t2 : nounList) {
                System.out.println(t2);
            }
            System.out.println();
        }

        //EXAMPLE: Identifing Questions
        boolean x = isQuestion(kBestTrees.get(0));
        System.out.println("Is the sentence a question: " + x);
    }

    // convert List<Scoredobject<Tree>> to ArrayList<Tree>
    public static ArrayList<Tree> getkBestTrees(List<ScoredObject<Tree>> kBest) {
        ArrayList<Tree> kBestTrees = new ArrayList<Tree>();
        for (ScoredObject<Tree> t : kBest) {
            kBestTrees.add(t.object());
        }
        return kBestTrees;
    }

    // Parse each Tree using Tregex and find all NPs
    // NP is a Noun Phrase
    public static ArrayList<Tree> findNounPhrases(Tree t) {
        // Intialize TregexPattern, pass it the pattern and parse the tree
        TregexPattern pattern = TregexPattern.compile("NP");
        TregexMatcher matcher = pattern.matcher(t);
        ArrayList<Tree> list = new ArrayList<Tree>();

        while (matcher.findNextMatchingNode()) {
            Tree match = matcher.getMatch();
            list.add(match);
        }
        return list;
    }


    // Check if the sentence is a question. Questions (usually) involve a SBARQ, SQ, or SINV in their parse tree.
    private static boolean isQuestion(Tree t) {
        //Check if top node is SBARQ, SQ, or SINV
        if (t.value().equals("SBARQ") || t.value().equals("SQ") || t.value().equals("SINV")) {
            return true;
        }
        
        Tree[] tarray = t.children();
        // Recurse into children nodes
        for (int i=0; i < tarray.length; i++) {
            if (isQuestion(tarray[i]) == true) {
                return true;
            }
        }
        return false;
    }
}