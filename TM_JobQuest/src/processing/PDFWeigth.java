package processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sparql.SPARQLRepository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import model.Pair;
import upload.ResponseOCR;
import upload.ResponseSpotlight;

public class PDFWeigth {
	
    private static HashMap<Integer, HashMap<String, Long>> cache = null;
    
    private static void writeCache() {
		System.out.println("write cache");
		PrintWriter pw;
		try {
			pw = new PrintWriter("./cachefile.json");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			pw.write(gson.toJson(cache));
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void readCache() {
		System.out.println("read cache " + new File("./cachefile.json").getAbsolutePath());
		try {
			BufferedReader br = new BufferedReader(new FileReader("./cachefile.json"));
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			
			cache = gson.fromJson(br, new TypeToken<HashMap<Integer, HashMap<String, Long>>>() {}.getType());
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			cache = new HashMap<>();
			writeCache();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	
	public static HashMap<String, Long> getHashMapAttachedFiles(File file) throws Exception {
		
		if(cache==null){
			readCache();
		}
		
		if(cache.containsKey(file.hashCode())){
			return cache.get(file.hashCode());
		}
		
		
		HashMap<String, Long> frequency= new HashMap<>();
		
		String cat2 = OCRRestAPI.pdfToTxt(file.getPath());

		Gson gson = new GsonBuilder().create();
		ResponseOCR ocr = gson.fromJson(cat2, ResponseOCR.class);
		System.out.println(ocr.getOCRText());
		int i = 0;
		ArrayList<String> keywords = new ArrayList<>();
		for (List<String> v : ocr.getOCRText()){
			for(String j : v){
				String x = j.replaceAll("[^A-Za-z0-9 ]","");
				x = x.replaceAll("\\s", "%20");
				String url = "http://spotlight.sztaki.hu:2222/rest/annotate?text="+x+"&confidence=0.2&support=20";
				HttpClient client = new DefaultHttpClient();
				HttpGet request2 = new HttpGet(url);
				request2.addHeader("accept", "application/json");
				HttpResponse response2 = client.execute(request2);
				int code = response2.getStatusLine().getStatusCode();
				if(code==200){
					BufferedReader rd = new BufferedReader (new InputStreamReader(response2.getEntity().getContent()));
					String line = "";
					String result="";
					while ((line = rd.readLine()) != null) {
						result+=line;
					}
					gson = new GsonBuilder().create();
					ResponseSpotlight resultSpotlight = gson.fromJson(result, ResponseSpotlight.class);
					for(ResponseSpotlight.Resource r : resultSpotlight.getResources()){
						
						
						StringBuilder qb = new StringBuilder();
						qb.append("PREFIX onto: <http://dbpedia.org/ontology/> \n");
						qb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
						qb.append("SELECT DISTINCT ?name \n");
						qb.append("WHERE { <"+r.getURI()+"> foaf:name ?name.  }  \n");
						
						System.out.println(qb.toString());
						
						Repository repo = new SPARQLRepository("http://dbpedia.org/sparql");
						repo.initialize();
						RepositoryConnection conn = repo.getConnection();
						TupleQueryResult result2 = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 
						result2 = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 

						if(result2.hasNext()){
							BindingSet bs = result2.next(); 
							String n = bs.getValue("name").toString();
							
							keywords.add(n);
							if(frequency.containsKey(n)){
								frequency.put(n, frequency.get(n)+1);
							}else{
								frequency.put(n, (long) 1);
							}
						}
						conn.close();
						repo.shutDown();
//						String uri = r.getURI();
//						uri = uri.replace("http://dbpedia.org/resource/", "");
//						uri = uri.replace("_", " ");
						
					}
				}
			}
		}
		
		cache.put(file.hashCode(), frequency);
		writeCache();
		return frequency;
	}
	


}
