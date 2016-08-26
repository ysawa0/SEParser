package net.greenclay.SEParser;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

// Checks Verb Noun Pairs against the topic blacklist
// sets sent.isMalicious = true when it is detected as malicious

// TODO: Change txt file format to json. Allow multi word nouns like "Internet Explorer"
// This class will check VerbNounPairs against the Topic blacklist stored on MongoDB
public final class TopicBlacklist {

//	private Sentence sent;
	private static ArrayList<VerbNounPair> blacklist;
	private TopicBlacklist(String myFileName) {
		blacklist = new ArrayList<VerbNounPair>();
        blacklist.addAll(getTopicBlacklistFromMongo());
        // Populate topic blacklist by reading myFileName
        // OLD BLACKLIST THAT READS FROM TEXT FILE BELOW
//		blacklist.addAll(readTopicBlacklistFile(myFileName));
//
//		// blacklist.add(new VerbNounPair("download"));
//		// blacklist.get(blacklist.size()-1).addNoun("file");
//		// //blacklist.get(blacklist.size()-1).addNoun("it");
//		VerbNounPair p = new VerbNounPair("open", "");
//		p.setNoun("internet, explorer");
//		blacklist.add(p);
		// //blacklist.get(blacklist.size()-1).addNoun("that");
		// blacklist.get(blacklist.size()-1).addNoun("Internet, Explorer");
		
		// blacklist.add(new VerbNounPair("update"));
		// blacklist.get(blacklist.size()-1).addNoun("records");
		
		// blacklist.add(new VerbNounPair("give"));
		// blacklist.get(blacklist.size()-1).addNoun("social");
		// blacklist.get(blacklist.size()-1).addNoun("address");
		
		// blacklist.add(new VerbNounPair("email"));
		// blacklist.get(blacklist.size()-1).addNoun("cert");
		

		// blacklist.add(new VerbNounPair("send"));
		// blacklist.get(blacklist.size()-1).addNoun("certificate");
		
		// blacklist.add(new VerbNounPair("turn"));
		// blacklist.get(blacklist.size()-1).addNoun("firewall");
	}

	public static void populateTopicBlacklist() {
        blacklist = new ArrayList<VerbNounPair>();
        blacklist.addAll(getTopicBlacklistFromMongo());
    }

	public static ArrayList<VerbNounPair> getTopicBlacklistFromMongo() {
//		String mURI = "mongodb://guest:anteater1@ds145325.mlab.com:45325/separser";
//		MongoClientURI uri = new MongoClientURI(mURI);
//		MongoClient cl = new MongoClient(uri);
		MongoClientURI uri = new MongoClientURI("mongodb://guest:anteater1@ds145325.mlab.com:45325/separser");
		MongoClient client = new MongoClient(uri);
		MongoDatabase db = client.getDatabase("separser");
		FindIterable<Document> iterable = db.getCollection("blacklist").find();

        ArrayList<VerbNounPair> myblacklist = new ArrayList<VerbNounPair>();
		for (Document d : iterable) {
			VerbNounPair pair = new VerbNounPair(d.get("verb").toString(), d.get("noun").toString());
            myblacklist.add(pair);
//			System.out.println(pair.toString());
		}

		return myblacklist;

//		iterable.forEach(new Block<Document>() {
//			@Override
//			public void apply(final Document document) {
//				System.out.println(document);
//			}
//		});
//
// for (Document d : iterable) {
//			System.out.println(d);
//		}
	}

//	public void setSentence(Sentence s) {
//		this.sent = s;
//	}

	// Method deprecated for now. Dont need Anaphora detection
	// Detect "that" or "it" and when found, replace them with the previous noun
	// Find any Anaphora Resolution and replace the "that" or "it"
//	public void doAnaphoraDetection() {
//		for (VerbNounPair vnPair : sent.vxnpairs) {
//			for (int i=0; i < vnPair.nouns.size(); i++ ){
//				String noun = vnPair.nouns.get(i);
//				if (noun.contains("that") || noun.contains("[it]")) {
//					if (sent.getHasThat()) {
//						if(noun.contains("that")) {
//							OutputWriter.write("Anaphora resolution detected - Replacing \"that\" with: " + sent.anaphoraResolution);
//						}
//						else if(noun.contains("[it]")) {
//							OutputWriter.write("Anaphora resolution detected - Replacing \"it\" with: " + sent.anaphoraResolution);
//						}
//						vnPair.nouns.set(i, sent.anaphoraResolution);
//					}
//				}
//			}
//		}
//	}

	public static void blacklistCheck(Sentence sent) {
//		doAnaphoraDetection();
		for (VerbNounPair sentencePair : sent.vnpairs) {
			for (VerbNounPair blacklistPair : blacklist )
			{
				// Find a blacklist verb
				if (sentencePair.verb.contains(blacklistPair.verb)) {
					if (sentencePair.noun.contains(blacklistPair.noun)) {
                        OutputWriter.write("Verb found on Topic Blacklist: " + sentencePair.verb);
                        OutputWriter.write("Noun found on Topic Blacklist: " + blacklistPair.noun);
						OutputWriter.write("---- Sentence is MALICIOUS ------");
						sent.setIsMalicious(true);
                        sentencePair.malicious = true;
					}
//					for (String noun : sentPair.nouns) {
//						for (String blacknoun : blackwordsPair.nouns) {
//							if (noun.toLowerCase().contains(blacknoun)) {
//								OutputWriter.write("Blacklist Noun found: " + blacknoun);
//								OutputWriter.write("------ Sentence is MALICIOUS ------\n");
//								sent.setIsMalicious(true);
//							}
//						}
//					}
				}
			}
		}
	}

	// Read topic blacklist from txt file.
	// Format should be: 
	// Verb Noun Noun Noun ...
	// ie: give social address password 
	// Verb and nouns should be all lowercased
	private static ArrayList<VerbNounPair> readTopicBlacklistFile(String myFileName) {
		ArrayList<VerbNounPair> blacklist = new ArrayList<VerbNounPair>();
		try (BufferedReader br = new BufferedReader(new FileReader(myFileName))) {
			String line = br.readLine();
			while (line != null) {
				if (!line.equals("")) {
					String[] lineSplit = line.split("\\s");
					VerbNounPair pair = new VerbNounPair(lineSplit[0], lineSplit[1]);
//					pair.addNoun(lineSplit[1]);
					blacklist.add(pair);
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<VerbNounPair>();
		}
		return blacklist;
	}
}
