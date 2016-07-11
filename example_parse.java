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

// Example code to get parse trees for one sentence.
// To Compile and run 
// For Linux, Mac OS X:
// javac -cp "./stanford-parser.jar" example_parse.java
// java -cp "./stanford-parser.jar:./" example_parse
// On Windows:
// javac -cp "./stanford-parser.jar" example_parse.java
// java -cp "./stanford-parser.jar;./" example_parse
// Sample output:
// Loading parser from serialized file englishPCFG.ser.gz ...  done [0.4 sec].
// (ROOT (S (NP (PRP I)) (VP (VBP 'm) (NP (DT a) (NN test) (NN sentence))) (. .)))
// (ROOT (S (NP (PRP I)) (VP (VBP 'm) (NP (DT a) (NN test) (NN sentence))) (. .)))
// (ROOT (S (NP (PRP I)) (VP (VBP 'm) (NP (DT a) (JJ test) (NN sentence))) (. .)))
// (ROOT (S (NP (PRP I)) (VP (VBP 'm) (NP (DT a) (NN test) (NN sentence))) (. .)))
// (ROOT (S (NP (PRP I)) (VP (VBP 'm) (NP (NP (DT a) (NN test)) (NP (NN sentence)))) (. .)))

public class example_parse {

    public static void main(String[] args) {
        // Load the parser model and intialize parser objects
        LexicalizedParser lp = LexicalizedParser.loadModel("englishPCFG.ser.gz");
        LexicalizedParserQuery lpq = lp.lexicalizedParserQuery();
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();

        String sentence = "I'm a test sentence.";

        // Parse the sentence
        Tokenizer<? extends HasWord> toke = tlp.getTokenizerFactory().getTokenizer(new StringReader(sentence));
        List<? extends HasWord> sent = toke.tokenize();
        lpq.parse(sent);

        // Get the results. the kBestTrees are the top k best parse trees for the sentence.
        int numOfParses = 5; // Get the top 5 parse trees
        List<ScoredObject<Tree>> kbest = lpq.getKBestPCFGParses(numOfParses);
        // Convert it to an ArrayList of Tree
        ArrayList<Tree> kBestTrees = getkBestTrees(kbest);

        for (Tree t : kBestTrees) {
            System.out.println(t);
        }
    }

    // convert List<Scoredobject<Tree>> to ArrayList<Tree>
    public static ArrayList<Tree> getkBestTrees(List<ScoredObject<Tree>> kBest) {
        ArrayList<Tree> kBestTrees = new ArrayList<Tree>();
        for (ScoredObject<Tree> t : kBest) {
            kBestTrees.add(t.object());
        }
        return kBestTrees;
    }

}