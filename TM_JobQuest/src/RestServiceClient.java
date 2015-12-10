import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
 
import org.json.JSONObject;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPRepository;
 
public class RestServiceClient {
	public static Person personJohn = new Person();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String string = "";
		String sesameServer = "http://semantic.ilab-research.ch:8080/openrdf-sesame/";
		String repositoryID = "titi";

		Repository repo = new HTTPRepository(sesameServer, repositoryID);
		repo.initialize();
		ValueFactory f = repo.getValueFactory();
		String namespace ="http://hf.ch/";
		RepositoryConnection conn = repo.getConnection();
		try {
			conn.begin();
			URI john = f.createURI(namespace, "john");

			// Add statements to the rep John is a Person, John's name is John 
			conn.add(john, RDF.TYPE, FOAF.PERSON);
			conn.add(john, RDFS.LABEL, f.createLiteral("John", XMLSchema.STRING));		
			conn.add(john, FOAF.FAMILY_NAME, f.createLiteral("Lennon", XMLSchema.STRING));
			conn.commit();
			conn.begin();
			String query="Select ?name ?familyname where {?person <"+RDF.TYPE+"> <"+FOAF.PERSON+">"+
							" . ?person <"+RDFS.LABEL+"> ?name"
							+" . ?person <"+FOAF.FAMILY_NAME+"> ?familyname}";
			System.out.println(query);
			TupleQuery result = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
			TupleQueryResult res = result.evaluate();
			while(res.hasNext()){
				BindingSet actualRes = res.next();
				personJohn.setNom(actualRes.getValue("name").stringValue());
				personJohn.setPrenom(actualRes.getValue("familyname").stringValue());
				System.out.println("Name: "+actualRes.getValue("name")+" Family Name:"+actualRes.getValue("familyname"));
			}
			conn.commit();
			
			// Step1: Let's 1st read file from fileSystem
			// Change CrunchifyJSON.txt path here
			InputStream crunchifyInputStream = new FileInputStream("/Users/paulineboukhaled/Documents/test.txt");
			InputStreamReader crunchifyReader = new InputStreamReader(crunchifyInputStream);
			BufferedReader br = new BufferedReader(crunchifyReader);
			String line;
			while ((line = br.readLine()) != null) {
				string += line + "\n";

			}
			JSONObject jsonObject = new JSONObject(string);
			System.out.println(jsonObject);
 
			// Step2: Now pass JSON File Data to REST Service
			try {
				URL url = new URL("http://localhost:8080/TM_JobQuest/api/RestService");
				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(5000);
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				out.write(jsonObject.toString());
				out.close();
 
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
 
				while (in.readLine() != null) {
				}
				System.out.println("\nCrunchify REST Service Invoked Successfully..");
				in.close();
			} catch (Exception e) {
				System.out.println("\nError while calling Crunchify REST Service");
				System.out.println(e);
			}
 
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			conn.close();
		}
		
	}
}