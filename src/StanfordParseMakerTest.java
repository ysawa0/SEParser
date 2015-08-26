import java.io.StringReader;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.LexicalizedParserQuery;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.util.ScoredObject;

public class StanfordParseMakerTest implements Runnable{
	private List<Sentence> sentList;
	private LexicalizedParser lp;
	private int numOfParses;
	
	public StanfordParseMakerTest(List<Sentence> sentList, LexicalizedParser lp, int numOfParses) {
		this.sentList = sentList;
		this.lp = lp;
		this.numOfParses = numOfParses;
	}
	
	public void run() {
		LexicalizedParserQuery lpq = lp.lexicalizedParserQuery();
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		List<? extends HasWord> sent;
		Tokenizer<? extends HasWord> toke;
		Sentence gottenSentence;
		Sentence sentenceBefore = null; // Store the previous sentence so it can be looked at for anaphora resolution

		System.err.println("sentList.size() - " + sentList.size());
		
		for (int i = 0; i < sentList.size(); i++) {

			gottenSentence = sentList.get(i);

			toke = tlp.getTokenizerFactory().getTokenizer(new StringReader(gottenSentence.sent));
			sent = toke.tokenize();
			
//			// Ignore one word sentences
//			if (sent.size() == 2) {
//				sentList.remove(i);
//				continue;
//			}
			
//			 Print each sentence and how many words are in it
			
			StringBuilder sb = new StringBuilder();
			sb.append(i);
			sb.append(" - # of words: ");
			sb.append(sent.size());
			sb.append(" - ");
			sb.append(gottenSentence.getSentenceString());
			System.err.println(sb);
			

//			if (sent.size() >= 200)
//				continue;

			lpq.parse(sent);
			List<ScoredObject<Tree>> kbest = lpq.getKBestPCFGParses(numOfParses);
			gottenSentence.setKBest(kbest);
			gottenSentence.numParses = numOfParses;
			
			// Save the previous sentence in "sentenceBefore"
			// Do Anaphora resolution analysis by creating new AnaphoraParser object


		}
	}
}
