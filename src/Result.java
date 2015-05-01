

public class Result {
	public int falseNegatives = 0;
	public int falsePositives = 0;
	public int i = 1;
	public float precision = 0;
	public float recall = 0;
	public int detectedQs = 0;
	public int detectedNs = 0;
	public int totalQs = 0;
	public int totalNs = 0;
	
	public int numParses = 0;
	
	public Result(int num) {
		numParses = num;
	}
	
	public void calculate() {

		precision = ((float) detectedQs) / (detectedQs + falsePositives);
		recall = ((float) detectedQs) / (detectedQs + falseNegatives);
	}
}
