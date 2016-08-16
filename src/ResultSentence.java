package net.greenclay.SEParser;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.ScoredObject;

 
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.io.StringWriter;
 
// Each sentence/line in the input text file is turned into a Sentence object which holds 
// the parse trees, verb/noun info and if it has been detected as a certain type of sentence
public class ResultSentence {
	
	protected boolean question = false;
	protected boolean softCommand = false;
	protected boolean hardCommand = false;
	protected boolean malicious = false;
	protected String sentence = "";
	protected ArrayList<Tree> kBestTrees;
	protected int numParses;
	protected ArrayList<VerbNounPair> vnpairs;
	protected String vnpairStr;

	public ResultSentence(String sentence, ArrayList<Tree> kBestTrees, int numParses, ArrayList<VerbNounPair> vnpairs) {
		// kBestTrees = convertScoredObjectToArrayList(kBest);
		this.kBestTrees = kBestTrees;
		this.sentence = sentence;
		this.numParses = numParses;
		this.vnpairs = vnpairs;
		this.vnpairStr = toString(vnpairs);
	}
	
	private String toString(ArrayList<VerbNounPair> vnpairs) {
		String results = "";
		for (VerbNounPair vnpair : vnpairs) {
			String nouns = vnpair.nounToString();
			// vnpair.nouns.forEach((str) ->  nouns + " " + str);
			String s = vnpair.verb + " - " + nouns;
			results += s;
			// JsonObject model = Json.createObjectBuilder()
			// .add("verb", vnpair.verb)
			// .add("nouns", nouns)
			// .build();
			// StringWriter stWriter = new StringWriter();
			// JsonWriter jsonWriter = Json.createWriter(stWriter);
			// jsonWriter.writeObject(model);
			// jsonWriter.close();

			// String jsonData = stWriter.toString();
			// return jsonData;
		}
		return results;
	}
	public ArrayList<Tree> convertScoredObjectToArrayList(List<ScoredObject<Tree>> kBest) {
		ArrayList<Tree> list = new ArrayList<Tree>();
		for (ScoredObject<Tree> t : kBest) {
			list.add(t.object());
		}
		return list;
	}

	public String makeJson() {
		JsonObject model = Json.createObjectBuilder()
		.add("malicious", malicious)
		.add("softCommand", softCommand)
		.add("hardCommand", hardCommand)
		.add("question", question)
		.add("sentence", sentence)
		.add("parseTree", kBestTrees.get(0).toString())
		.add("numParses", numParses)
		.add("verbNounPairs", vnpairStr)
		.build();

		StringWriter stWriter = new StringWriter();
		JsonWriter jsonWriter = Json.createWriter(stWriter);
		jsonWriter.writeObject(model);
		jsonWriter.close();

		String jsonData = stWriter.toString();
		return jsonData;
	}
}

