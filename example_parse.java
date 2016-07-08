import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.LexicalizedParserQuery;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.util.ScoredObject;

// Example code to get parse trees for one sentence.

public class Main {
    public static void main(String[] args) {
        // load the parser model and intialize parser objects
        lp = LexicalizedParser.loadModel("englishPCFG.ser.gz");
        LexicalizedParserQuery lpq = lp.lexicalizedParserQuery();
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();

        String sentence = "I'm a test sentence."

        // parse the sentence
        Tokenizer<? extends HasWord> toke = tlp.getTokenizerFactory().getTokenizer(new StringReader(sentence));;
        List<? extends HasWord> sent = toke.tokenize();;
        lpq.parse(sent);

        // get the results. the kBestTrees are the top k best parse trees for the sentence.
        List<ScoredObject<Tree>> kbest = lpq.getKBestPCFGParses(numOfParses);
        // convert it to an ArrayList of Tree
        ArrayList<Tree> kBestTrees = getkBestTrees(kbest);

        System.out.println(kBestTrees.get(0));
    }

    // convert List<Scoredobject<Tree>> to ArrayList<Tree>
    public static ArrayList<Tree> getkBestTrees(List<ScoredObject<Tree>> kBest) {
        kBestTrees = new ArrayList<Tree>();
        for (ScoredObject<Tree> t : kBest) {
            kBestTrees.add(t.object());
        }
        return kBestTrees;
    }

}