

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.ScoredObject;

public class Sentence {

	private boolean hasThat = false; // includes a "that" in the sentence
	public String sent;
	public boolean isQuestion = false;
	public boolean isSoft = false;

	
	public List<ScoredObject<Tree>> kBest;
	private ArrayList<Tree> kBestTrees;
	
	public boolean detectAsQ = false;
	public boolean detectAsSoft = false;

	public String tags = "";
	public int detectionCount = 0;
	public int sentenceNumber = 0;
	
	public int numParses = 0;
	public boolean softCommand = false;
	public boolean hardCommand = false;
	
	public String softVerb = "";
	public String softNoun = "";
	public String hardVerb = "";
	public String hardNoun = "";
	
	public ArrayList<VerbNounPair> softWords;
	public ArrayList<VerbNounPair> vnpairs;
	
	public int detectedKBest = -1;
	
	public String anaphoraResolution;
	 
	public Sentence(String s, int n) {
		sent = s;
		sentenceNumber = n;
        if(s.contains("?"))
        {
            isQuestion = true;
        }
        else
        {
        	isQuestion = false;
        }
        if(s.contains("!"))
        {
        	isSoft = true;
        }
        else
        {
        	isSoft = false;
        }
        vnpairs = new ArrayList<VerbNounPair>();
	}
	
	public void setKBest(List<ScoredObject<Tree>> kBest) {
		this.kBest = kBest;
		// convert List<Scoredobject<Tree>> to ArrayList<Tree>
		kBestTrees = new ArrayList<Tree>();
		for (ScoredObject<Tree> t : kBest) {
			kBestTrees.add(t.object());
		}
	}
	
	public ArrayList<Tree> getkBestTrees() {
		return kBestTrees;
	}
	public void setHasThat() {
		hasThat = true;
	}
	
	public boolean getHasThat() {
		return hasThat;
	}
	
	public String getSentenceString() {		return sent;	}
	public void initKeywords() {
		vnpairs = new ArrayList<VerbNounPair>();
	}
	// Requires number of detections for a sentence to be detected
	
	public void newPair(String v) {
		vnpairs.add(new VerbNounPair(v));
	}
	
	public void addNoun(String n) {
		// adds a noun to the last added noun verb pair.
		vnpairs.get(vnpairs.size()-1).addNoun(n);
	}
	
	public boolean findResult1() {
		if (detectionCount >= 1) {
			detectAsQ = true;
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean findResult()
	{
		
		
		if(numParses == 1) {
			if (detectionCount == 1)
			{
				detectAsQ = true;
				return true;
			}
			else
			{
				detectedKBest = 0;
				return false;
			}
		}
		else if(numParses == 2) {
			if (detectionCount == 2)
			{
				detectAsQ = true;
				return true;
			}
			else
			{
				detectedKBest = 0;
				return false;
			}
		}
		else {
			if (detectionCount >= 3)
			{
				detectAsQ = true;
				return true;
			}
			else
			{
				detectedKBest = 0;
				return false;
			}
		}
	}
}
