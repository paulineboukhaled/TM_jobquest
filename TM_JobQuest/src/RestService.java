import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.net.URL;

import javax.print.attribute.standard.Media;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
//import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
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
import org.openrdf.repository.sparql.SPARQLRepository;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ch.qos.logback.classic.LoggerContext;
import upload.UploadPDF;

@Path("/a")
public class RestService {
	static final int NUMBER_OF_TAGS = 10;

	

	
	@POST
	@Path("/RestService")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWords(InputStream incomingData) {
		StringBuilder crunchifyBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				crunchifyBuilder.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - ");
		}
		System.out.println("Data Received2: " + crunchifyBuilder.toString());
		// return HTTP response 200 in case of success
		return Response.status(200).entity(crunchifyBuilder.toString()).build();
	}

	@POST
	@Path("/spotlight")
	@Produces(MediaType.APPLICATION_ATOM_XML)
	public Response getAnnotations(InputStream incomingData) throws ClientProtocolException, IOException {
		String url = "http://spotlight.sztaki.hu:2222/rest/annotate?text=Michelle%20Obama%20called%20Thursday%20on%20Congress%20to%20extend%20a%20tax%20break%20for%20students%20included%20in%20last%20year%27s%20economic%20stimulus%20package,%20arguing%20that%20the%20policy%20provides%20more%20generous%20assistance.&confidence=0.2&support=20";
		System.out.println("spotligth lauched");
		//return HTTP response 200 in case of success
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		request.addHeader("accept", "application/json");
		System.out.println("spotligth lauched2");
		HttpResponse response = client.execute(request);
		System.out.println("spotligth lauched3");

		BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
		String line = "";
		String result="";
		while ((line = rd.readLine()) != null) {
			result+=line;
			System.out.println(line);
		}

		return Response.status(200).entity(result).build();

	}

	@POST
	@Path("/post")
	@Consumes(MediaType.APPLICATION_JSON)
	public String createDataInJSON(String data) { 

		String result = data;

		Gson gson = new Gson();
		//convert the json string back to object
		Person obj = gson.fromJson(data, Person.class);

		System.out.println(obj.getNom());

		String sesameServer = "http://semantic.ilab-research.ch:8080/openrdf-sesame/";
		String repositoryID = "titi";

		Repository repo = new HTTPRepository(sesameServer, repositoryID);
		repo.initialize();
		ValueFactory f = repo.getValueFactory();
		String namespace ="http://hf.ch/";
		RepositoryConnection conn = repo.getConnection();
		try {
			conn.begin();
			URI newPerson = f.createURI(namespace, obj.getNom());
			// Add statements to the rep John is a Person, John's name is John 
			conn.add(newPerson, RDF.TYPE, FOAF.PERSON);
			conn.add(newPerson, RDFS.LABEL, f.createLiteral(obj.getNom(), XMLSchema.STRING));		
			conn.add(newPerson, FOAF.FAMILY_NAME, f.createLiteral(obj.getPrenom(), XMLSchema.STRING));
			conn.add(newPerson, FOAF.MEMBER, f.createLiteral("true", XMLSchema.BOOLEAN));
			conn.commit();		
		}finally{
			conn.close();
		} 
		//System.out.println(result);
		return result; 
	}

	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	public String deleteDataInJSON(String data) { 

		String result = data;

		Gson gson = new Gson();
		//convert the json string back to object
		Person obj = gson.fromJson(data, Person.class);

		System.out.println(obj.getNom());

		String sesameServer = "http://semantic.ilab-research.ch:8080/openrdf-sesame/";
		String repositoryID = "titi";

		Repository repo = new HTTPRepository(sesameServer, repositoryID);
		repo.initialize();
		ValueFactory f = repo.getValueFactory();
		String namespace ="http://hf.ch/";
		RepositoryConnection conn = repo.getConnection();
		try {
			conn.begin();
			URI newPerson = f.createURI(namespace, obj.getNom());
			// Add statements to the rep John is a Person, John's name is John 
			//conn.add(newPerson, FOAF.MEMBER, f.createLiteral("false", XMLSchema.BOOLEAN));
			conn.commit();		
		}finally{
			conn.close();
		} 
		//System.out.println(result);
		return result; 
	}


	@GET
	@Path("/verify")
	@Produces(MediaType.TEXT_PLAIN)
	public Response verifyRESTService(InputStream incomingData) {
		String result = "CrunchifyRESTService Successfully started..";
		System.out.println("verifyRESTService");
		// return HTTP response 200 in case of success
		return Response.status(200).entity(result).build();
	}

	@GET
	@Path("/labels")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listLabels(InputStream incomingData) throws IOException {
		String url = "http://sisinflab.poliba.it/led/api/get/tags?q=php&limit=15&key=8d092ae3&format=json";
		System.out.println("verifyRESTService");
		// return HTTP response 200 in case of success
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);

		BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
		String line = "";
		String result="";
		while ((line = rd.readLine()) != null) {
			result+=line;
			System.out.println(line);
		}

		Gson gson = new Gson();
		RequestLabel obj = gson.fromJson(result, RequestLabel.class);
		System.out.println(obj.getQuery());
		ArrayList<String> skills = new ArrayList<String>();
		for(int i=0; i<obj.getResults().size(); i++){
			skills.add(obj.getResults().get(i).getLabel());
			System.out.println(obj.getResults().get(i).getLabel());
		}
		
		System.out.println(skills.size());
		ResponseLabel test= new ResponseLabel(skills);
		String test2 = gson.toJson(test);
		System.out.println(test2);
		return Response.status(200).entity(test2).build();
	}


	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listTags(@QueryParam("skill") String id) throws IOException {		
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.stop();
		System.out.println(id);
		SkillsWeight.map.put(id, 1.0);
		SkillsWeight.getInfluencedBy("http://dbpedia.org/resource/"+id, "0", SkillsWeight.LEVEL, 1);
		for (String mapKey : SkillsWeight.map.keySet()) {
			System.out.println(mapKey+" a "+ SkillsWeight.map.get(mapKey));
		}
		
		for (String mapKey : UploadPDF.getFrequency().keySet()) {
			if(SkillsWeight.map.containsKey(mapKey)){
				SkillsWeight.map.put(mapKey, SkillsWeight.map.get(mapKey)+UploadPDF.getFrequency().get(mapKey));
			}else{
				SkillsWeight.map.put(mapKey, (double)UploadPDF.getFrequency().get(mapKey));
			}
		}

		SkillsWeight.ValueComparator comparateur = new SkillsWeight.ValueComparator(SkillsWeight.map);
		TreeMap<String,Double> mapTriee = new TreeMap<String,Double>(comparateur);
		mapTriee.putAll(SkillsWeight.map);
		ArrayList<Tag> finalTags = new ArrayList<>();
		int count = 0;
		for (String mapKey : mapTriee.keySet()) {
			if(count<mapTriee.size() && count<NUMBER_OF_TAGS){
				count++;
				Tag tg= new Tag(mapKey);
				finalTags.add(tg);
			}
		}
		return Response.status(200).entity(finalTags).build();
	}

	@GET
	@Path("/listPerson")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listPerson(InputStream incomingData) {
		String sesameServer = "http://semantic.ilab-research.ch:8080/openrdf-sesame/";
		String repositoryID = "titi";
		Repository repo = new HTTPRepository(sesameServer, repositoryID);
		repo.initialize();
		ValueFactory f = repo.getValueFactory();
		RepositoryConnection conn = repo.getConnection();
		ArrayList<Person> listpersons = new ArrayList<Person>();
		try {
			conn.begin();
			String query="Select ?name ?familyname ?member where {?person <"+RDF.TYPE+"> <"+FOAF.PERSON+">"+
					" . ?person <"+RDFS.LABEL+"> ?name"
					+" . ?person <"+FOAF.FAMILY_NAME+"> ?familyname"
					+" . ?person <"+FOAF.MEMBER+"> ?member}";
			System.out.println(query);
			TupleQuery result = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
			TupleQueryResult res = result.evaluate();
			while(res.hasNext()){
				BindingSet actualRes = res.next();
				Person personJohn = new Person();
				if(actualRes.getValue("member").stringValue().compareTo("true")==0){
					personJohn.setNom(actualRes.getValue("name").stringValue());
					personJohn.setPrenom(actualRes.getValue("familyname").stringValue());
					System.out.println("Name: "+actualRes.getValue("name").stringValue()+" Family Name:"+actualRes.getValue("familyname").stringValue());
					listpersons.add(personJohn);
				}
			}
			conn.commit();
		}finally{
			conn.close();
		}
		return Response.status(200).entity(listpersons).build();
	}



}