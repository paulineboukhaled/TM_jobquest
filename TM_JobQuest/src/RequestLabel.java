import java.util.ArrayList;

public class RequestLabel {
	private String query;
	private ArrayList<Label> results;
	
	public RequestLabel(String query, ArrayList<Label> results){
		this.query=query;
		this.results=results;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public ArrayList<Label> getResults() {
		return results;
	}

	public void setResults(ArrayList<Label> results) {
		this.results = results;
	}
	
	

}
