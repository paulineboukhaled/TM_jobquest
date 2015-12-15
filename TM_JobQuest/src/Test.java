import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

public class Test {
	static HashMap<String, Double> map = new HashMap<String, Double>();
	static ArrayList<Skill> listOfSkill = new ArrayList<>();
	static int count = 0;


	public static ArrayList<Skill> getSkillsRelated(String URI, String URIParent, int level, double value){
		count = 0;
		//System.out.println("LE LEVEL: \t"+level+" URI: \t"+ URI+" \t "+URIParent);
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
			while(result.hasNext() && count<30) {
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
					//System.out.println("Le poids de :"+listTemp.get(i).getName()+" est "+listTemp.get(i).getWeight()+" SIZE:"+listTemp.size()+" value:"+value);
					if(map.containsKey(listTemp.get(i).getName())){
						System.out.println("Le poids de :"+listTemp.get(i).getName()+" est "+listTemp.get(i).getWeight()+" SIZE:"+listTemp.size()+" value:"+value);
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
			//System.out.println("LE LEVEL DIFFERENT DE 0: "+level);
			for(int i=0; i<listTemp.size(); i++){
				//System.out.println("Taille de la liste:"+listTemp.size());
				double value2 = 1.0/listTemp.size();
				ArrayList<Skill> Temp = getSkillsRelated(listTemp.get(i).getURI(), URI, level-1, value2);
				if(!Temp.isEmpty()){
					listOfSkill.addAll(Temp);
				}
			}
		}
		return listOfSkill;
	}

	public static void main(String[] args) {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.stop();
		ArrayList<Skill> finalList =  getSkillsRelated("http://dbpedia.org/resource/Java_(programming_language)", "0", 2, 1);
		map.put("Java_(programming_language)", 1.0);
		/*for(int i=0; i<finalList.size(); i++){
			if(map.containsKey(finalList.get(i).getName())){
				map.put(finalList.get(i).getName(), map.get(finalList.get(i).getName())+finalList.get(i).getWeight());
			}else{
				map.put(finalList.get(i).getName(), finalList.get(i).getWeight());
			}
		}*/
		for (String mapKey : map.keySet()) {
			System.out.println(mapKey+" a "+ map.get(mapKey));
		}
	}
}
