

import java.util.ArrayList;

// Checks Verb Noun Pairs against the topic blacklist
// sets sent.isMalicious = true when it is detected as malicious

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
		
		
		for (VerbNounPair vnPair : sent.vnpairs) {
//			OutputWriter.write("VNP verb: " + vnPair.verb);
//			OutputWriter.write("VNP nouns: " + vnPair.nouns);
			
			for (int i=0; i < vnPair.nouns.size(); i++ ){
				String noun = vnPair.nouns.get(i);
				if (noun.contains("that") || noun.contains("[it]")) {
					if (sent.getHasThat()) {
						if(noun.contains("that")) {
							OutputWriter.write("Anaphora resolution detected - Replacing \"that\" with: " + sent.anaphoraResolution);
						}
						else if(noun.contains("[it]")) {
							OutputWriter.write("Anaphora resolution detected - Replacing \"it\" with: " + sent.anaphoraResolution);
						}
						vnPair.nouns.set(i, sent.anaphoraResolution);
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
					OutputWriter.write("Blacklist Verb found: " + sentPair.verb);
					
					for (String noun : sentPair.nouns) {
						
						for (String blacknoun : blackwordsPair.nouns) {
							
							if (noun.contains(blacknoun)) {
								OutputWriter.write("Blacklist Noun found: " + blacknoun);
								OutputWriter.write("------ Sentence is MALICIOUS ------\n");
								sent.setIsMalicious(true);
							}
						}
						
					}
				}
			}
			
		}
	}
}
