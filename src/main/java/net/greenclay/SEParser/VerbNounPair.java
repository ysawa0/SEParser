package net.greenclay.SEParser;

import java.util.ArrayList;

// Holds a Verb Noun Pair which is a verb and all nouns that follow it and is before the next verb.
// A new VerbNounPair is created when the next verb is found.
// For example, if the sentence is "Give me your password then send me the file certificate"
// The first VerbNounPair would be "Give" and "me", "your", "password"
// The second VerbNounPair would be "send" and "file", "certificate"

public class VerbNounPair {
	
	public String verb;
//	public ArrayList<String> noun;
	public String noun;
    protected boolean malicious = false;

	public VerbNounPair(String verb, String noun) {
		this.verb = verb.toLowerCase();
        this.noun = noun.toLowerCase();
//		this.noun = new ArrayList<String>();
	}

	public void setNoun(String noun) { this.noun = noun; }

//	public void addNoun(String n) {
//		noun.add(n.toLowerCase());
//	}

	public String toString() {
		return verb + " - " + noun;
	}
}
