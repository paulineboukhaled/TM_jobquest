import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
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

import ch.qos.logback.classic.LoggerContext;
import upload.UploadPDF;

public class SkillsWeight {
	static HashMap<String, Double> map = new HashMap<String, Double>();
	static ArrayList<Skill> listOfSkill = new ArrayList<>();
	static final int MAX_RESULT = 50;
	static final int LEVEL = 2;

	static int count = 0;

//	static HashMap<String, ObjDBPEDIA>
	
	
	public static HashMap<String, Double> getAllSkillsWeight(ArrayList<Skill> listOfSkills){
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.stop();		
		for(Skill s : listOfSkills){
			System.out.println(s.getName());
			map.put(s.getName(), 1.0);
			getInfluencedBy("http://dbpedia.org/resource/"+s.getName(), "0", LEVEL, 1);
			getInfluenced("http://dbpedia.org/resource/"+s.getName(), "0", LEVEL, 1);
			getParadigm("http://dbpedia.org/resource/"+s.getName(), "0", LEVEL, 1);	
		}
		return map;			
	}


	public static ArrayList<Skill> getInfluencedBy(String URI, String URIParent, int level, double value){
		count = 0;
		ArrayList<Skill> listTemp = new ArrayList<>();
		Repository repo = new SPARQLRepository("http://dbpedia.org/sparql");
		repo.initialize();
		RepositoryConnection conn = repo.getConnection();
		boolean hasResult=true;
		try {
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
			while(result.hasNext() && count<MAX_RESULT) {
				count++;
				BindingSet bs = result.next(); 
				Value uri = bs.getValue("languages");
				Value name = bs.getValue("name");
				Skill skill = new Skill(name.stringValue(), 3, 2, 0, uri.stringValue());
				listTemp.add(skill);
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
				ArrayList<Skill> Temp1 = getInfluencedBy(listTemp.get(i).getURI(), URI, level-1, value2);
				ArrayList<Skill> Temp2 = getInfluenced(listTemp.get(i).getURI(), URI, level-1, value2);
				ArrayList<Skill> Temp3 = getParadigm(listTemp.get(i).getURI(), URI, level-1, value2);
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
	
	
	public static ArrayList<Skill> getInfluenced(String URI, String URIParent, int level, double value){
		count = 0;
		ArrayList<Skill> listTemp = new ArrayList<>();
		Repository repo = new SPARQLRepository("http://dbpedia.org/sparql");
		repo.initialize();
		RepositoryConnection conn = repo.getConnection();
		boolean hasResult=true;
		try {
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
			while(result.hasNext() && count<MAX_RESULT) {
				count++;
				BindingSet bs = result.next();
				Value uri = bs.getValue("languages");
				Value name = bs.getValue("name");
				Skill skill = new Skill(name.stringValue(), 3, 2, 0, uri.stringValue());
				listTemp.add(skill);
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
				ArrayList<Skill> Temp1 = getInfluencedBy(listTemp.get(i).getURI(), URI, level-1, value2);
				ArrayList<Skill> Temp2 = getInfluenced(listTemp.get(i).getURI(), URI, level-1, value2);
				ArrayList<Skill> Temp3 = getParadigm(listTemp.get(i).getURI(), URI, level-1, value2);
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
	
	public static ArrayList<Skill> getParadigm(String URI, String URIParent, int level, double value){
		count = 0;
		ArrayList<Skill> listTemp = new ArrayList<>();
		Repository repo = new SPARQLRepository("http://dbpedia.org/sparql");
		repo.initialize();
		RepositoryConnection conn = repo.getConnection();
		boolean hasResult=true;
		try {
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
			while(result.hasNext() && count<MAX_RESULT) {
				count++;
				BindingSet bs = result.next();
				Value uri = bs.getValue("paradigm");
				Value name = bs.getValue("name");
				Skill skill = new Skill(name.stringValue(), 3, 2, 0, uri.stringValue());
				listTemp.add(skill);
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
				ArrayList<Skill> Temp1 = getInfluencedBy(listTemp.get(i).getURI(), URI, level-1, value2);
				ArrayList<Skill> Temp2 = getInfluenced(listTemp.get(i).getURI(), URI, level-1, value2);
				ArrayList<Skill> Temp3 = getParadigm(listTemp.get(i).getURI(), URI, level-1, value2);
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

	static class ValueComparator implements Comparator<String> {
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
	
	
	public static void main(String[] args) throws Exception {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.stop();
		
		File cv = new File("cv.pdf");
	    System.out.println(cv.getAbsolutePath()); 
		HashMap<String, Long> tempMap = UploadPDF.getHashMapAttachedFiles(cv);
		
		
		// TRI DE LA MAP
		ValueComparator2 comparateur2 = new ValueComparator2(tempMap);
		TreeMap<String,Long> mapTriee2 = new TreeMap<String,Long>(comparateur2);
		mapTriee2.putAll(tempMap);
		System.out.println("resultat du tri: "+ mapTriee2);
		
		Skill java = new Skill("Java_(programming_language)", 3, 2, 0, null);
		//Skill mysql = new Skill("MySQL", 3, 2, 0, null);
		//Skill c = new Skill("C_(programming_language)", 3, 2, 0, null);
		ArrayList<Skill> temp = new ArrayList<>();
		temp.add(java);
		//temp.add(c);
		//temp.add(mysql);
				
		for (String mapKey : getAllSkillsWeight(temp).keySet()) {
			//System.out.println(mapKey+" a "+ map.get(mapKey));
		}

		for(String key : tempMap.keySet()){
			if(map.containsKey(key)){
				System.out.println("Même élément: "+key);
				map.put(key, map.get(key)+tempMap.get(key));
			}else{
				map.put(key, (double)tempMap.get(key));

			}
			
		}
		
		// TRI DE LA MAP
		ValueComparator comparateur = new ValueComparator(map);
		TreeMap<String,Double> mapTriee = new TreeMap<String,Double>(comparateur);
		mapTriee.putAll(map);
		System.out.println("resultat du tri: "+ mapTriee);
		
	}

	/**
	 * @param args
	 */
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
