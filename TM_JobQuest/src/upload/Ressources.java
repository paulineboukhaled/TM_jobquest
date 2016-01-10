package upload;

public class Ressources {
	private String URI;
	private double support;
	private String types;
	private String surfaceForm;
	private long offset;
	private double similarityScore;
	private double percentageOfSecondRank;
	
	public Ressources(String URI, double support, String types, String surfaceForm, long offset, double similarityScore, double percentageOfSecondRank){
		setURI(URI);
		setSupport(support);
		setTypes(types);
		setSurfaceForm(surfaceForm);
		setOffset(offset);
		setSimilarityScore(similarityScore);
		setPercentageOfSecondRank(percentageOfSecondRank);	
	}
	
	public String getURI() {
		return URI;
	}
	public void setURI(String uRI) {
		URI = uRI;
	}
	public double getSupport() {
		return support;
	}
	public void setSupport(double support) {
		this.support = support;
	}
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}
	public String getSurfaceForm() {
		return surfaceForm;
	}
	public void setSurfaceForm(String surfaceForm) {
		this.surfaceForm = surfaceForm;
	}
	public long getOffset() {
		return offset;
	}
	public void setOffset(long offset) {
		this.offset = offset;
	}
	public double getSimilarityScore() {
		return similarityScore;
	}
	public void setSimilarityScore(double similarityScore) {
		this.similarityScore = similarityScore;
	}
	public double getPercentageOfSecondRank() {
		return percentageOfSecondRank;
	}
	public void setPercentageOfSecondRank(double percentageOfSecondRank) {
		this.percentageOfSecondRank = percentageOfSecondRank;
	}
}
