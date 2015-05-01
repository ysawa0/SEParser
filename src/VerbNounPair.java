

import java.util.ArrayList;

public class VerbNounPair {
	
	public String verb;
	public ArrayList<String> nouns;
	
	public VerbNounPair(String v) {
		verb = v.toLowerCase();
		nouns = new ArrayList<String>();
	}
	
	public void addNoun(String n) {
		nouns.add(n.toLowerCase());
	}
	
}
