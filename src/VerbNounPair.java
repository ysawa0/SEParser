

import java.util.ArrayList;

// Holds a Verb Noun Pair which is a verb and all nouns that follow it and is before the next verb.
// A new VerbNounPair is created when the next verb is found.
// For example, if the sentence is "Give me your password then send me the file certificate"
// The first VerbNounPair would be "Give" and "me", "your", "password"
// The second VerbNounPair would be "send" and "file", "certificate"

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
