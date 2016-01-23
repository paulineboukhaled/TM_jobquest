package processing;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import ch.qos.logback.core.net.SyslogOutputStream;
import model.Pair;
import model.Person;
import model.Skill;
import model.SkillCompute;
import upload.UploadPDF;
import upload.*;

public class SkillsWeight {


	private static HashMap<String, ArrayList<Pair>> cache = null; // String = URI -> valeurs associées

	private static void writeCache() {
		System.out.println("write cache");
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
		System.out.println("read cache");
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

	public static HashMap<String, Double> map = new HashMap<String, Double>();
	static HashMap<String, Double> map2 = new HashMap<String, Double>();

	static ArrayList<SkillCompute> listOfSkill = new ArrayList<>();
	static final int MAX_RESULT = 100;
	public static final int LEVEL = 2;

	static int count = 0;

	public static HashMap<String, Double> getAllSkillsWeight(List<Skill> listOfSkills){
		
		if(cache == null) {
			readCache();
		}
		
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.stop();		
		for(Skill s : listOfSkills){
			System.out.println(s.getName());
			double weight =  Double.parseDouble(s.getLevel()) *Double.parseDouble(s.getYearOfExperience());
			map.put(s.getName(), weight);
			getInfluencedBy("http://dbpedia.org/resource/"+s.getName(), "0", LEVEL, weight);
			getInfluenced("http://dbpedia.org/resource/"+s.getName(), "0", LEVEL, weight);
			getParadigm("http://dbpedia.org/resource/"+s.getName(), "0", LEVEL, weight);	
		}
		
		writeCache();
		
		return map;			
	}


	public static ArrayList<SkillCompute> getInfluencedBy(String URI, String URIParent, int level, double value){
		String cache_key = URI + "_" + "influencedBy";
		
		count = 0;
		ArrayList<SkillCompute> listTemp = new ArrayList<>();

		Repository repo = new SPARQLRepository("http://dbpedia.org/sparql");
		repo.initialize();
		RepositoryConnection conn = repo.getConnection();
		boolean hasResult=true;
		try {

			if(!cache.containsKey(cache_key)) {
				StringBuilder qb = new StringBuilder();
				qb.append("PREFIX onto: <http://dbpedia.org/ontology/> \n");
				qb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
				qb.append("SELECT DISTINCT ?name ?languages \n");
				qb.append("WHERE { ?languages rdf:type onto:ProgrammingLanguage. ?languages onto:influencedBy  <"+URI+">.  \n");
				qb.append("?languages foaf:name ?name.  }  \n");
				TupleQueryResult result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 
				result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 
				if(!result.hasNext()){
					hasResult=false;
				}
				ArrayList<Pair> pair = new ArrayList<>();
				while(result.hasNext() && count<MAX_RESULT) {
					count++;
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
			if(!listTemp.isEmpty() && hasResult){
				for(int i=0; i<listTemp.size(); i++){
					listTemp.get(i).setWeight((1.0/listTemp.size())*value);
					if(map.containsKey(listTemp.get(i).getName())){
						//System.out.println("Le poids de :"+listTemp.get(i).getName()+" est "+map.get(listTemp.get(i).getName())+(listTemp.get(i).getWeight()));
						map.put(listTemp.get(i).getName(), map.get(listTemp.get(i).getName())+(listTemp.get(i).getWeight()));
					}else{
						map.put(listTemp.get(i).getName(), listTemp.get(i).getWeight());
					}	
				}
			}
		}finally {
			conn.close();
		}


		if (level!=0 && hasResult){
			for(int i=0; i<listTemp.size(); i++){
				double value2 = 1.0/listTemp.size();
				ArrayList<SkillCompute> Temp1 = getInfluencedBy(listTemp.get(i).getURI(), URI, level-1, value2);
				ArrayList<SkillCompute> Temp2 = getInfluenced(listTemp.get(i).getURI(), URI, level-1, value2);
				ArrayList<SkillCompute> Temp3 = getParadigm(listTemp.get(i).getURI(), URI, level-1, value2);
				if(!Temp1.isEmpty()){
					listOfSkill.addAll(Temp1);
				}
				if(!Temp2.isEmpty()){
					listOfSkill.addAll(Temp2);
				}
				if(!Temp3.isEmpty()){
					listOfSkill.addAll(Temp3);
				}
			}
		}

		return listOfSkill;
	}


	public static ArrayList<SkillCompute> getInfluenced(String URI, String URIParent, int level, double value){
		String cache_key = URI + "_" + "influenced";
		
		count = 0;
		ArrayList<SkillCompute> listTemp = new ArrayList<>();
		Repository repo = new SPARQLRepository("http://dbpedia.org/sparql");
		repo.initialize();
		RepositoryConnection conn = repo.getConnection();
		boolean hasResult=true;
		try {
			if(!cache.containsKey(cache_key)) {

				StringBuilder qb = new StringBuilder();
				qb.append("PREFIX onto: <http://dbpedia.org/ontology/> \n");
				qb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
				qb.append("SELECT DISTINCT ?name ?languages \n");
				qb.append("WHERE { ?languages rdf:type onto:ProgrammingLanguage. ?languages onto:influenced  <"+URI+">.  \n");
				qb.append("?languages foaf:name ?name.  }  \n");
				TupleQueryResult result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 
				result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 
				if(!result.hasNext()){
					hasResult=false;
				}

				ArrayList<Pair> pair = new ArrayList<>();
				while(result.hasNext() && count<MAX_RESULT) {
					count++;
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
			if(!listTemp.isEmpty() && hasResult){
				for(int i=0; i<listTemp.size(); i++){
					listTemp.get(i).setWeight((1.0/listTemp.size())*value);
					if(map.containsKey(listTemp.get(i).getName())){
						System.out.println("Le poids de :"+listTemp.get(i).getName()+" est "+map.get(listTemp.get(i).getName())+(listTemp.get(i).getWeight()));
						map.put(listTemp.get(i).getName(), map.get(listTemp.get(i).getName())+(listTemp.get(i).getWeight()));
					}else{
						map.put(listTemp.get(i).getName(), listTemp.get(i).getWeight());
					}	
				}
			}
		}finally {
			conn.close();
		}
		if (level!=0 && hasResult){
			for(int i=0; i<listTemp.size(); i++){
				double value2 = 1.0/listTemp.size();
				ArrayList<SkillCompute> Temp1 = getInfluencedBy(listTemp.get(i).getURI(), URI, level-1, value2);
				ArrayList<SkillCompute> Temp2 = getInfluenced(listTemp.get(i).getURI(), URI, level-1, value2);
				ArrayList<SkillCompute> Temp3 = getParadigm(listTemp.get(i).getURI(), URI, level-1, value2);
				if(!Temp1.isEmpty()){
					listOfSkill.addAll(Temp1);
				}
				if(!Temp2.isEmpty()){
					listOfSkill.addAll(Temp2);
				}
				if(!Temp3.isEmpty()){
					listOfSkill.addAll(Temp3);
				}
			}
		}
		return listOfSkill;
	}

	public static ArrayList<SkillCompute> getParadigm(String URI, String URIParent, int level, double value){
		String cache_key = URI + "_" + "paradigm";
		count = 0;
		ArrayList<SkillCompute> listTemp = new ArrayList<>();
		Repository repo = new SPARQLRepository("http://dbpedia.org/sparql");
		repo.initialize();
		RepositoryConnection conn = repo.getConnection();
		boolean hasResult=true;
		try {
			if(!cache.containsKey(cache_key)) {

				StringBuilder qb = new StringBuilder();
				qb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
				qb.append("PREFIX dbp: <http://dbpedia.org/property/> \n");
				qb.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n");
				qb.append("SELECT  ?name ?paradigm \n");
				qb.append("WHERE { ?paradigm rdf:type <http://dbpedia.org/class/yago/ProgrammingParadigms>. <"+URI+"> dbp:paradigm ?paradigm.  \n");
				qb.append("?paradigm rdfs:label ?name. FILTER(lang(?name) = \"en\") }  \n");
				TupleQueryResult result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 
				result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 
				if(!result.hasNext()){
					hasResult=false;
				}

				ArrayList<Pair> pair = new ArrayList<>();
				while(result.hasNext() && count<MAX_RESULT) {
					count++;
					BindingSet bs = result.next();
					Value uri = bs.getValue("paradigm");
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
			if(!listTemp.isEmpty() && hasResult){
				for(int i=0; i<listTemp.size(); i++){
					listTemp.get(i).setWeight((1.0/listTemp.size())*value);
					if(map.containsKey(listTemp.get(i).getName())){
						System.out.println("Le poids de :"+listTemp.get(i).getName()+" est "+map.get(listTemp.get(i).getName())+(listTemp.get(i).getWeight()));
						map.put(listTemp.get(i).getName(), map.get(listTemp.get(i).getName())+(listTemp.get(i).getWeight()));
					}else{
						map.put(listTemp.get(i).getName(), listTemp.get(i).getWeight());
					}	
				}
			}
		}finally {
			conn.close();
		}
		if (level!=0 && hasResult){
			for(int i=0; i<listTemp.size(); i++){
				double value2 = 1.0/listTemp.size();
				ArrayList<SkillCompute> Temp1 = getInfluencedBy(listTemp.get(i).getURI(), URI, level-1, value2);
				ArrayList<SkillCompute> Temp2 = getInfluenced(listTemp.get(i).getURI(), URI, level-1, value2);
				ArrayList<SkillCompute> Temp3 = getParadigm(listTemp.get(i).getURI(), URI, level-1, value2);
				if(!Temp1.isEmpty()){
					listOfSkill.addAll(Temp1);
				}
				if(!Temp2.isEmpty()){
					listOfSkill.addAll(Temp2);
				}
				if(!Temp3.isEmpty()){
					listOfSkill.addAll(Temp3);
				}
			}
		}
		return listOfSkill;
	}

	public static class ValueComparator implements Comparator<String> {
		Map<String, Double> base;

		public ValueComparator(Map<String, Double> base) {
			this.base = base;
		}

		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} 
		}
	}

	static class ValueComparator2 implements Comparator<String> {
		Map<String, Long> base;

		public ValueComparator2(Map<String, Long> base) {
			this.base = base;
		}

		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} 
		}
	}

	public static HashMap<String, Double> mergeSkillAndPDF(HashMap<String, Double> skill, HashMap<String, Long> tempMap){
		
		HashMap<String, Double> results = new HashMap<>();
		
		for(String key : skill.keySet()){
			if(!results.containsKey(key))
				results.put(key, 0.0);
			
			results.put(key, results.get(key)+skill.get(key));
			
		}
		
		for(String key : tempMap.keySet()){
			if(!results.containsKey(key))
				results.put(key, 0.0);

			results.put(key, results.get(key)+tempMap.get(key));
		}
		
		return results;		
	}

	//
	//	public static double getSimBetween(Person personA, Person personB) throws Exception{
	//		String path = "/Users/paulineboukhaled/git/TM_JobQuest/";
	//
	//		File fPersonA = new File(path+personA.getFirstname()+"_"+personA.getName()+".json");
	//		File fPersonB = new File(path+personB.getFirstname()+"_"+personB.getName()+".json");
	//		HashMap<String, Double> temp1 = new HashMap<>();
	//		HashMap<String, Double> temp22 = new HashMap<>();
	//
	//
	//
	//		//-----------------------------------CANDIDAT NUMBER 1
	//		if(!fPersonA.exists()){
	//			File cv = new File("cv.pdf");
	//			HashMap<String, Long> tempMap = UploadPDF.getHashMapAttachedFiles(cv);
	//			ArrayList<Skill> temp = personA.getListOfSkill();
	//			temp1.putAll(getAllSkillsWeight(temp));
	//			// Merge hashmap skill and hashmap attached files
	//			temp1.putAll(mergeSkillAndPDF(temp1, tempMap));
	//
	//			// CREATION FILES FOR WEIGTHS
	//			PrintWriter pw = null;
	//			Gson gson = new GsonBuilder().setPrettyPrinting().create();
	//			pw = new PrintWriter(path+personA.getFirstname()+"_"+personA.getName()+".json");
	//			pw.write(gson.toJson(temp1));
	//			pw.close();
	//		}else{
	//			temp1.putAll(FileLoader.getAllGoodTweets(path+personA.getFirstname()+"_"+personA.getName()+".json"));
	//		}
	//
	//		//-----------------------------------CANDIDAT NUMBER 2
	//		if(!fPersonB.exists()){
	//			ArrayList<Skill> temp2 = personB.getListOfSkill();
	//			temp22.putAll(getAllSkillsWeight(temp2));	
	//			// CREATION FILES FOR WEIGTHS
	//			PrintWriter pw = null;
	//			Gson gson = new GsonBuilder().setPrettyPrinting().create();
	//			pw = new PrintWriter(path+personB.getFirstname()+"_"+personB.getName()+".json");
	//			pw.write(gson.toJson(temp22));
	//			pw.close();
	//		}else{
	//			temp22.putAll(FileLoader.getAllGoodTweets(path+personB.getFirstname()+"_"+personB.getName()+".json"));
	//		}
	//
	//		//-----------------------------------CREATION DU VECTEUR DE TERME
	//		ArrayList<String> terms = new ArrayList<>();
	//		for(String key : temp1.keySet()){
	//			if(!terms.contains(key)){
	//				terms.add(key);
	//			}
	//		}
	//		for(String key2 : temp22.keySet()){
	//			if(!terms.contains(key2)){
	//				terms.add(key2);
	//			}
	//		}
	//
	//		//-----------------------------------COMPARAISON DE 2 VECTEURS
	//		double[][] matrice =  new double[2][terms.size()];
	//		for(int i=0; i<matrice.length ; i++){
	//			for(int j=0; j<matrice[0].length; j++){
	//				if(i==0){
	//					if(temp1.containsKey(terms.get(j))){
	//						matrice[i][j] = temp1.get(terms.get(j));
	//					}else{
	//						matrice[i][j] = 0;
	//					}
	//				}else{
	//					if(temp22.containsKey(terms.get(j))){
	//						matrice[i][j] = temp22.get(terms.get(j));
	//					}else{
	//						matrice[i][j] = 0;	
	//					}
	//				}
	//			}
	//		}
	//
	//		for(String k : terms){
	//			System.out.print(k+"\t");
	//		}
	//		System.out.print("\n");
	//
	//		for(int i=0; i<matrice.length ; i++){
	//			for(int j=0; j<matrice[0].length; j++){
	//				System.out.print(matrice[i][j]+" \t");
	//			}
	//			System.out.println();
	//
	//		}
	//
	//
	//		double sim = CosineSimilarity.cosineSimilarity(matrice[0], matrice[1]);
	//		return sim;
	//	}
	//
	//
	//	public static void main(String[] args) throws Exception {
	//		PrintWriter pw = null;
	//		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
	//		loggerContext.stop();
	//		Date d1 = new Date();
	//		Skill java = new Skill("Java_(programming_language)", 3, 2, 0, null);
	//		ArrayList<Skill> temp = new ArrayList<>();
	//		temp.add(java);
	//		//Skill mysql = new Skill("Falcon_(programming_language)", 3, 2, 0, null);
	//		Skill c = new Skill("PHP", 3, 2, 0, null);
	//		ArrayList<Skill> temp2 = new ArrayList<>();
	//		temp2.add(c);
	//		//temp2.add(mysql);
	//		Person paulinesavelli = new Person("Savelli", "Pauline", d1, "21 avenue du moléson", 1700, "Fribourg", temp, false, true);
	//		Person victorboukhaled = new Person("Boukhaled", "Victor", d1, "21 avenue du moléson", 1700, "Fribourg", temp2, false, true);
	//
	//		System.out.println("la similarité entre java et falcon :"+getSimBetween(paulinesavelli, victorboukhaled));
	//
	//
	//		//		//		//-----------------------------------CANDIDAT NUMBER 1
	//		//		//		//----Files attached
	//		//		//		File cv = new File("cv.pdf");
	//		//		//		HashMap<String, Long> tempMap = UploadPDF.getHashMapAttachedFiles(cv);
	//		//		//		//----Skill
	//		//		//		Skill java = new Skill("Java_(programming_language)", 3, 2, 0, null);
	//		//		//		System.out.println("Start load skill");
	//		//		//		ArrayList<Skill> temp = new ArrayList<>();
	//		//		//		temp.add(java);
	//		//		//		HashMap<String, Double> temp1 = new HashMap<>();
	//		//		//		temp1.putAll(getAllSkillsWeight(temp));
	//		//		//		// Merge hashmap skill and hashmap attached files
	//		//		//		temp1.putAll(mergeSkillAndPDF(temp1, tempMap));
	//		//
	//		//		HashMap<String, Double> temp1 = new HashMap<>();
	//		//		String path = "/Users/paulineboukhaled/git/TM_JobQuest/Java_(programming_language)3_2.json";
	//		//		temp1.putAll(FileLoader.getAllGoodTweets(path));
	//		//
	//		//		// CREATION FILES FOR WEIGTHS
	//		//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
	//		//		pw = new PrintWriter("Java_(programming_language)"+3+"_"+2+".json");
	//		//		pw.write(gson.toJson(temp1));
	//		//		pw.close();
	//		//
	//		//		//-----------------------------------CANDIDAT NUMBER 2
	//		//		//Skill mysql = new Skill("Falcon_(programming_language)", 3, 2, 0, null);
	//		//		Skill c = new Skill("PHP", 3, 2, 0, null);
	//		//		ArrayList<Skill> temp2 = new ArrayList<>();
	//		//		temp2.add(c);
	//		//		//temp2.add(mysql);
	//		//		HashMap<String, Double> temp22 = new HashMap<>();
	//		//		temp22.putAll(getAllSkillsWeight(temp2));
	//		//
	//		//
	//		//		// CREATION DU VECTEUR DE TERME
	//		//		ArrayList<String> terms = new ArrayList<>();
	//		//		for(String key : temp1.keySet()){
	//		//			if(!terms.contains(key)){
	//		//				terms.add(key);
	//		//			}
	//		//		}
	//		//		for(String key2 : temp22.keySet()){
	//		//			if(!terms.contains(key2)){
	//		//				terms.add(key2);
	//		//			}
	//		//		}
	//		//
	//		//		// comparaison de deux vecteurs
	//		//		double[][] matrice =  new double[2][terms.size()];
	//		//		for(int i=0; i<matrice.length ; i++){
	//		//			for(int j=0; j<matrice[0].length; j++){
	//		//				if(i==0){
	//		//					if(temp1.containsKey(terms.get(j))){
	//		//						matrice[i][j] = temp1.get(terms.get(j));
	//		//					}else{
	//		//						matrice[i][j] = 0;
	//		//					}
	//		//				}else{
	//		//					if(temp22.containsKey(terms.get(j))){
	//		//						matrice[i][j] = temp22.get(terms.get(j));
	//		//					}else{
	//		//						matrice[i][j] = 0;	
	//		//					}
	//		//				}
	//		//			}
	//		//		}
	//		//
	//		//		for(String k : terms){
	//		//			System.out.print(k+"\t");
	//		//		}
	//		//		System.out.print("\n");
	//		//
	//		//		for(int i=0; i<matrice.length ; i++){
	//		//			for(int j=0; j<matrice[0].length; j++){
	//		//				System.out.print(matrice[i][j]+" \t");
	//		//			}
	//		//			System.out.println();
	//		//
	//		//		}
	//		//
	//		//		double sim = CosineSimilarity.cosineSimilarity(matrice[0], matrice[1]);
	//
	//
	//
	//
	//
	//	}
	//
	//
	//	/*public void getCosineSimilarity() {
	//		for (int i = 0; i < tfidfDocsVector.size(); i++) {
	//			for (int j = 0; j < tfidfDocsVector.size(); j++) {
	//				System.out.println("between " + i + " and " + j + "  =  "
	//						+ new CosineSimilarity().cosineSimilarity
	//						(
	//								tfidfDocsVector.get(i), 
	//								tfidfDocsVector.get(j)
	//								)
	//						);
	//			}
	//		}
	//	}*/
	//
	//	/**
	//	 * @param args
	//	 */
	/*public static void main(String[] args) {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.stop();
		map.put("Java", 1.0);
		getInfluencedBy("http://dbpedia.org/resource/Java_(programming_language)", "0", LEVEL, 1);
		getInfluenced("http://dbpedia.org/resource/Java_(programming_language)", "0", LEVEL, 1);
		getParadigm("http://dbpedia.org/resource/Java_(programming_language)", "0", LEVEL, 1);


		for (String mapKey : map.keySet()) {
			System.out.println(mapKey+" a "+ map.get(mapKey));
		}

		// TRI DE LA MAP
		ValueComparator comparateur = new ValueComparator(map);
		TreeMap<String,Double> mapTriee = new TreeMap<String,Double>(comparateur);
		mapTriee.putAll(map);
		System.out.println("resultat du tri: "+ mapTriee);
	}*/
}
