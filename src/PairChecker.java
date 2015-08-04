

import java.util.ArrayList;

// Checks Verb Noun Pairs against the topic blacklist
public class PairChecker {

	private Sentence sent;
	private ArrayList<VerbNounPair> blacklist;
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
	
	// Detect "that" or "it" and when found, replace them with the previous noun
	// Find any Anaphora Resolution and replace the "that" or "it"
	public void doAnaphoraDetection() {
		
		System.out.println("Anaphorse Detection");
		
		for (VerbNounPair vnPair : sent.vnpairs) {
			System.out.println("VNP verb: " + vnPair.verb);
			System.out.println("VNP nouns: " + vnPair.nouns);
			
			for (int i=0; i < vnPair.nouns.size(); i++ ){
				String noun = vnPair.nouns.get(i);
				if (noun.contains("that") || noun.contains("[it]")) {
					if (sent.getHasThat()) {
						vnPair.nouns.set(i, sent.anaphoraResolution);
						System.out.println("Anaphora resolution detected - Replacing That or It");
					}
				}
			}
		}
	}

	public void blacklistCheck2() {
		doAnaphoraDetection();
		for (VerbNounPair sentPair : sent.vnpairs) {
			
			for (VerbNounPair blackwordsPair : blacklist )
			{
				// Find a blacklist verb
				if (sentPair.verb.contains(blackwordsPair.verb)) {
					System.out.println("Blacklist Verb found: " + sentPair.verb);
					
					for (String noun : sentPair.nouns) {
						
//						if (noun.contains("that") || noun.contains("[it]")) {
//							if (sent.getHasThat()) {
//								noun = sent.anaphoraResolution;
//								System.out.println("Anaphora resolution detected - Replacing That or It");
//							}
//						}
						
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
	
	public void blacklistCheck() {
		for (VerbNounPair vnPair : sent.vnpairs) {
			
			for (VerbNounPair blackwordsPair : blacklist )
			{
				// Find a blacklist verb
				if (vnPair.verb.contains(blackwordsPair.verb)) {
					System.out.println("Blacklist Verb found: " + vnPair.verb);
					
					for (String noun : vnPair.nouns) {
						
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
