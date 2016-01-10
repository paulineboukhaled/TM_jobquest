package upload;


import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author paulineboukhaled
 *
 */
public class ResponseSpotlight {


	@SerializedName("@text")
	@Expose
	private String Text;
	@SerializedName("@confidence")
	@Expose
	private String Confidence;
	@SerializedName("@support")
	@Expose
	private String Support;
	@SerializedName("@types")
	@Expose
	private String Types;
	@SerializedName("@sparql")
	@Expose
	private String Sparql;
	@SerializedName("@policy")
	@Expose
	private String Policy;
	@SerializedName("Resources")
	@Expose
	private List<Resource> Resources = new ArrayList<Resource>();

	/**
	 * 
	 * @return
	 * The Text
	 */
	public String getText() {
		return Text;
	}

	/**
	 * 
	 * @param Text
	 * The @text
	 */
	public void setText(String Text) {
		this.Text = Text;
	}

	/**
	 * 
	 * @return
	 * The Confidence
	 */
	public String getConfidence() {
		return Confidence;
	}

	/**
	 * 
	 * @param Confidence
	 * The @confidence
	 */
	public void setConfidence(String Confidence) {
		this.Confidence = Confidence;
	}

	/**
	 * 
	 * @return
	 * The Support
	 */
	public String getSupport() {
		return Support;
	}

	/**
	 * 
	 * @param Support
	 * The @support
	 */
	public void setSupport(String Support) {
		this.Support = Support;
	}

	/**
	 * 
	 * @return
	 * The Types
	 */
	public String getTypes() {
		return Types;
	}

	/**
	 * 
	 * @param Types
	 * The @types
	 */
	public void setTypes(String Types) {
		this.Types = Types;
	}

	/**
	 * 
	 * @return
	 * The Sparql
	 */
	public String getSparql() {
		return Sparql;
	}

	/**
	 * 
	 * @param Sparql
	 * The @sparql
	 */
	public void setSparql(String Sparql) {
		this.Sparql = Sparql;
	}

	/**
	 * 
	 * @return
	 * The Policy
	 */
	public String getPolicy() {
		return Policy;
	}

	/**
	 * 
	 * @param Policy
	 * The @policy
	 */
	public void setPolicy(String Policy) {
		this.Policy = Policy;
	}

	/**
	 * 
	 * @return
	 * The Resources
	 */
	public List<Resource> getResources() {
		return Resources;
	}

	/**
	 * 
	 * @param Resources
	 * The Resources
	 */
	public void setResources(List<Resource> Resources) {
		this.Resources = Resources;
	}

	public class Resource {

		@SerializedName("@URI")
		@Expose
		private String URI;
		@SerializedName("@support")
		@Expose
		private String Support;
		@SerializedName("@types")
		@Expose
		private String Types;
		@SerializedName("@surfaceForm")
		@Expose
		private String SurfaceForm;
		@SerializedName("@offset")
		@Expose
		private String Offset;
		@SerializedName("@similarityScore")
		@Expose
		private String SimilarityScore;
		@SerializedName("@percentageOfSecondRank")
		@Expose
		private String PercentageOfSecondRank;

		/**
		 * 
		 * @return
		 * The URI
		 */
		public String getURI() {
			return URI;
		}

		/**
		 * 
		 * @param URI
		 * The @URI
		 */
		public void setURI(String URI) {
			this.URI = URI;
		}

		/**
		 * 
		 * @return
		 * The Support
		 */
		public String getSupport() {
			return Support;
		}

		/**
		 * 
		 * @param Support
		 * The @support
		 */
		public void setSupport(String Support) {
			this.Support = Support;
		}

		/**
		 * 
		 * @return
		 * The Types
		 */
		public String getTypes() {
			return Types;
		}

		/**
		 * 
		 * @param Types
		 * The @types
		 */
		public void setTypes(String Types) {
			this.Types = Types;
		}

		/**
		 * 
		 * @return
		 * The SurfaceForm
		 */
		public String getSurfaceForm() {
			return SurfaceForm;
		}

		/**
		 * 
		 * @param SurfaceForm
		 * The @surfaceForm
		 */
		public void setSurfaceForm(String SurfaceForm) {
			this.SurfaceForm = SurfaceForm;
		}

		/**
		 * 
		 * @return
		 * The Offset
		 */
		public String getOffset() {
			return Offset;
		}

		/**
		 * 
		 * @param Offset
		 * The @offset
		 */
		public void setOffset(String Offset) {
			this.Offset = Offset;
		}

		/**
		 * 
		 * @return
		 * The SimilarityScore
		 */
		public String getSimilarityScore() {
			return SimilarityScore;
		}

		/**
		 * 
		 * @param SimilarityScore
		 * The @similarityScore
		 */
		public void setSimilarityScore(String SimilarityScore) {
			this.SimilarityScore = SimilarityScore;
		}

		/**
		 * 
		 * @return
		 * The PercentageOfSecondRank
		 */
		public String getPercentageOfSecondRank() {
			return PercentageOfSecondRank;
		}

		/**
		 * 
		 * @param PercentageOfSecondRank
		 * The @percentageOfSecondRank
		 */
		public void setPercentageOfSecondRank(String PercentageOfSecondRank) {
			this.PercentageOfSecondRank = PercentageOfSecondRank;
		}
	}
}





	/*
	private String text;
	private double confidence;
	private long support;
	private String types;
	private String sparql;
	private String policy;
	private ArrayList<Ressources> listRessources;

	public ResponseSpotlight(String text, double confidence, long support, String types, String sparql, String policy, ArrayList<Ressources> listRessources){
		setText(text);
		setConfidence(confidence);
		setSupport(support);
		setTypes(types);
		setSparql(sparql);
		setPolicy(policy);
		setListRessources(listRessources);	
	}


	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public double getConfidence() {
		return confidence;
	}
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	public long getSupport() {
		return support;
	}
	public void setSupport(long support) {
		this.support = support;
	}
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}
	public String getSparql() {
		return sparql;
	}
	public void setSparql(String sparql) {
		this.sparql = sparql;
	}
	public String getPolicy() {
		return policy;
	}
	public void setPolicy(String policy) {
		this.policy = policy;
	}
	public ArrayList<Ressources> getListRessources() {
		return listRessources;
	}
	public void setListRessources(ArrayList<Ressources> listRessources) {
		this.listRessources = listRessources;
	}*/


