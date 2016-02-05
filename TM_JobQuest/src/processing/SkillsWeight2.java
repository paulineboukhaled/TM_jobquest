package processing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import ch.qos.logback.classic.LoggerContext;
import model.Pair;
import model.Skill;
import model.SkillCompute;

public class SkillsWeight2 {

	
	private static HashMap<String, ArrayList<Pair>> cache = null; // String = URI -> valeurs associées

	private static void writeCache() {
//		System.out.println("write cache");
		PrintWriter pw;
		try {
			pw = new PrintWriter("./cache.json");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			pw.write(gson.toJson(cache));
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void readCache() {
//		System.out.println("read cache");
		try {
			BufferedReader br = new BufferedReader(new FileReader("./cache.json"));
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			
			cache = gson.fromJson(br, new TypeToken<HashMap<String, ArrayList<Pair>>>() {}.getType());
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			cache = new HashMap<>();
			writeCache();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}



	
	
	
	public static HashMap<String, SkillCompute> influencedBy(String competenceName, double weight, int depth) {
		HashMap<String, SkillCompute> data = new HashMap<>();
		String compentenceUri = "http://dbpedia.org/resource/"+competenceName;
		data.put(compentenceUri, new SkillCompute(compentenceUri, 1, 1, 1, null));
		influencedBy(compentenceUri, weight, data, depth - 1);
		
		return data;
	}
	
	public static void influencedBy(String competenceUri, double weight, HashMap<String, SkillCompute> data, int depth) {
		if(cache == null) {
			readCache();
		}
		
		if(depth== 0)
			return;
		
		///
	
		String cache_key = competenceUri + "_" + "influencedBy";
		
		Repository repo = new SPARQLRepository("http://dbpedia.org/sparql");
		repo.initialize();
		RepositoryConnection conn = repo.getConnection();
		
		
		try {

			ArrayList<SkillCompute> listTemp = new ArrayList<>();

			if(!cache.containsKey(cache_key)) {
				StringBuilder qb = new StringBuilder();
				qb.append("PREFIX onto: <http://dbpedia.org/ontology/> \n");
				qb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
				qb.append("SELECT DISTINCT ?name ?languages \n");
				qb.append("WHERE { ?languages rdf:type onto:ProgrammingLanguage. ?languages onto:influencedBy  <"+competenceUri+">.  \n");
				qb.append("?languages foaf:name ?name.  }  \n");
				TupleQueryResult result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 
				result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 
				
				ArrayList<Pair> pair = new ArrayList<>();
				while(result.hasNext()) {
					BindingSet bs = result.next(); 
					Value uri = bs.getValue("languages");
					Value name = bs.getValue("name");
					SkillCompute skill = new SkillCompute(name.stringValue(), 3, 2, 0, uri.stringValue());
					listTemp.add(skill);
					pair.add(new Pair(uri.toString(), name.toString()));

				}
				cache.put(cache_key, pair);
			} else {
				for(Pair p : cache.get(cache_key)) {
					SkillCompute skill = new SkillCompute(p.name, 3, 2, 0, p.uri);
					listTemp.add(skill);
				}
			}
			
			for (SkillCompute skillCompute : listTemp) {
				if(!data.containsKey(skillCompute.getName())) {
					skillCompute.setWeight(weight / 2);
					data.put(skillCompute.getName(), skillCompute);
					influencedBy(skillCompute.getURI(), weight / 4, data, depth-1);
				}
			}
	
		}finally {
			conn.close();
		}
		
		writeCache();
	}
	
	public static HashMap<String, SkillCompute> influenced(String competenceName, double weight, int depth) {
		HashMap<String, SkillCompute> data = new HashMap<>();
		String compentenceUri = "http://dbpedia.org/resource/"+competenceName;
		data.put(compentenceUri, new SkillCompute(compentenceUri, 1, 1, 1, null));
		influenced(compentenceUri, weight, data, depth - 1);
		
		return data;
	}
	
	public static void influenced(String competenceUri, double weight, HashMap<String, SkillCompute> data, int depth) {
		if(cache == null) {
			readCache();
		}
		
		if(depth== 0)
			return;
		
		///
	
		String cache_key = competenceUri + "_" + "influencedBy";
		
		Repository repo = new SPARQLRepository("http://dbpedia.org/sparql");
		repo.initialize();
		RepositoryConnection conn = repo.getConnection();
		
		
		try {

			ArrayList<SkillCompute> listTemp = new ArrayList<>();

			if(!cache.containsKey(cache_key)) {
				StringBuilder qb = new StringBuilder();
				qb.append("PREFIX onto: <http://dbpedia.org/ontology/> \n");
				qb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
				qb.append("SELECT DISTINCT ?name ?languages \n");
				qb.append("WHERE { ?languages rdf:type onto:ProgrammingLanguage. ?languages onto:influenced  <"+competenceUri+">.  \n");
				qb.append("?languages foaf:name ?name.  }  \n");
				TupleQueryResult result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 
				result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 
				
				ArrayList<Pair> pair = new ArrayList<>();
				while(result.hasNext()) {
					BindingSet bs = result.next(); 
					Value uri = bs.getValue("languages");
					Value name = bs.getValue("name");
					SkillCompute skill = new SkillCompute(name.stringValue(), 3, 2, 0, uri.stringValue());
					listTemp.add(skill);
					pair.add(new Pair(uri.toString(), name.toString()));

				}
				cache.put(cache_key, pair);
			} else {
				for(Pair p : cache.get(cache_key)) {
					SkillCompute skill = new SkillCompute(p.name, 3, 2, 0, p.uri);
					listTemp.add(skill);
				}
			}
			
			for (SkillCompute skillCompute : listTemp) {
				if(!data.containsKey(skillCompute.getName())) {
					skillCompute.setWeight(weight / 2);
					data.put(skillCompute.getName(), skillCompute);
					influenced(skillCompute.getURI(), weight / 4, data, depth-1);
				}
			}
	
		}finally {
			conn.close();
		}
		
		writeCache();
	}
	
	
	public static void main(String[] args) {

		
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.stop();
		
		
		HashMap<String, SkillCompute> data = SkillsWeight2.influenced("Java_(programming_language)", 1, 4);
		for (String key : data.keySet()) {
			SkillCompute skillCompute = data.get(key);
			
			System.out.println("Skill : " + skillCompute.getName() + " (" + skillCompute.getWeight() + ")");
		}
		
		
	}

	public static HashMap<String, Double> getAllSkillsWeight(List<Skill> skills) {
		
		HashMap<String, Double> obj = new HashMap<>();

		for(Skill skill:skills){
			int depth = 6;
			double weight = 1.0+Math.log(Math.pow(Double.parseDouble(skill.getLevel()),Double.parseDouble(skill.getYearOfExperience())));
			HashMap<String, SkillCompute> data1 = SkillsWeight2.influenced(skill.getName(), weight, depth);
			for (String key : data1.keySet()) {
				SkillCompute skillCompute = data1.get(key);
				if(obj.containsKey(key)) {
					obj.put(key, obj.get(key) + skillCompute.getWeight());
				}else {
					obj.put(skillCompute.getName(), skillCompute.getWeight());
				}
			}		

			HashMap<String, SkillCompute> data2 = SkillsWeight2.influencedBy(skill.getName(), weight, depth);
			for (String key : data2.keySet()) {
				SkillCompute skillCompute = data2.get(key);
				if(obj.containsKey(key)) {
					obj.put(key, obj.get(key) + skillCompute.getWeight());
				}else {
					obj.put(skillCompute.getName(), skillCompute.getWeight());
				}
				
			}		
		}
		
		System.out.println("MAP: "+obj);
		return obj;
	}
	
}
