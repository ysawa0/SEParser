

import java.util.ArrayList;

// Checks Verb Noun Pairs against the topic blacklist
public class PairChecker {

	Sentence sent;
	ArrayList<VerbNounPair> blacklist;
	public PairChecker(Sentence s) {
		sent = s;
		blacklist = new ArrayList<VerbNounPair>();
		
		blacklist.add(new VerbNounPair("download"));
		blacklist.get(blacklist.size()-1).addNoun("file");
		//blacklist.get(blacklist.size()-1).addNoun("it");
		
		blacklist.add(new VerbNounPair("open"));
		//blacklist.get(blacklist.size()-1).addNoun("that");
		blacklist.get(blacklist.size()-1).addNoun("Internet, Explorer");
		
		blacklist.add(new VerbNounPair("update"));
		blacklist.get(blacklist.size()-1).addNoun("records");
		
		blacklist.add(new VerbNounPair("give"));
		blacklist.get(blacklist.size()-1).addNoun("social");
		blacklist.get(blacklist.size()-1).addNoun("address");
		
		blacklist.add(new VerbNounPair("email"));
		blacklist.get(blacklist.size()-1).addNoun("cert");
		

		blacklist.add(new VerbNounPair("send"));
		blacklist.get(blacklist.size()-1).addNoun("certificate");
		
		blacklist.add(new VerbNounPair("turn"));
		blacklist.get(blacklist.size()-1).addNoun("firewall");
	}
	
	public void blacklistCheck() {
		for (VerbNounPair sentPair : sent.vnpairs) {
			
			for (VerbNounPair blackwordsPair : blacklist )
			{
				// Find a blacklist verb
				if (sentPair.verb.contains(blackwordsPair.verb)) {
					System.out.println("Blacklist Verb found: " + sentPair.verb);
					
					for (String noun : sentPair.nouns) {
						
						if (noun.contains("that") || noun.contains("[it]")) {
							if (sent.getHasThat()) {
								noun = sent.anaphoraResolution;
								System.out.println("Anaphora resolution detected - Replacing That or It");
							}
						}
						
						for (String blacknoun : blackwordsPair.nouns) {
							
//							if (noun.contains("that") || noun.contains("[it]")) {
//								if (sent.getHasThat()) {
//									noun = sent.anaphoraResolution;
//									System.out.println("Anaphora resolution detected - Replacing That or It");
//								}
//							}
							
//							System.out.println("blacknoun " + blacknoun + " noun " + noun);
							if (noun.contains(blacknoun)) {
								System.out.println("Blacklist Noun found: " + blacknoun);
								System.out.println("------ Sentence is MALICIOUS ------\n");
							}
						}
						
					}
				}
			}
			
		}
	}
	
}
