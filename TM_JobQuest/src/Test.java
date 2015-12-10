import java.util.ArrayList;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sparql.SPARQLRepository;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ArrayList<Skill> listOfSkill = new ArrayList<>();
		Repository repo = new SPARQLRepository("http://dbpedia.org/sparql");
		repo.initialize();

		
		RepositoryConnection conn = repo.getConnection();
		try {
		    StringBuilder qb = new StringBuilder();
		    qb.append("PREFIX onto: <http://dbpedia.org/ontology/> \n");
		    qb.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n");
		    qb.append("SELECT DISTINCT ?name ?languages \n");
		    qb.append("WHERE { ?languages rdf:type onto:ProgrammingLanguage. ?languages onto:influencedBy  <http://dbpedia.org/resource/Java_(programming_language)>.  \n");
		    qb.append("?languages foaf:name ?name.  }  \n");
		    


		    TupleQueryResult result = 
		         conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 


		    result = conn.prepareTupleQuery(QueryLanguage.SPARQL, qb.toString()).evaluate(); 
		    while(result.hasNext()) {
		         BindingSet bs = result.next();
		         Value URI = bs.getValue("languages");
		         Value name = bs.getValue("name");
		         //System.out.println("name = " + name.stringValue());
		         //System.out.println("languages = " + URI.stringValue());
		         Skill skill = new Skill(name.stringValue(), 3, 2, 0, URI.stringValue());
		         listOfSkill.add(skill);
		    }
		    
		    for(int i=0; i<listOfSkill.size(); i++){
		    	listOfSkill.get(i).setWeight((1.0/listOfSkill.size())*listOfSkill.get(i).getNumberOfYear()*listOfSkill.get(i).getLevelOfExpertise());
		         System.out.println("URI: "+listOfSkill.get(i).getURI()+" Skill = " +listOfSkill.get(i).getName()+" Level: "+listOfSkill.get(i).getLevelOfExpertise()+" Year "+listOfSkill.get(i).getNumberOfYear()+" Weight:"+listOfSkill.get(i).getWeight());
		    }
		    
		    
		 }
		 finally {
		    conn.close();
		 }

	}

}
