package rest;
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
import java.net.URISyntaxException;
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
import com.google.gson.GsonBuilder;

import ch.qos.logback.classic.LoggerContext;
import model.Person;
import model.Position;
import model.RequestLabel;
import model.ResponseLabel;
import model.Skill;
import model.Tag;
import processing.PDFWeigth;
import processing.SaveOnSesame;
import processing.SkillsWeight;
import upload.UploadPDF;

@Path("/user")
public class UserInformation {
	//static final int NUMBER_OF_TAGS = 10;
	
	SaveOnSesame sesame = new SaveOnSesame();

	
	@POST
	@Path("/getForm")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendInformation(InputStream incomingData) {
		StringBuilder crunchifyBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				crunchifyBuilder.append(line);
			}
		} catch (Exception e) {
			System.err.println("Error Parsing: - ");
		}
		System.out.println("Data Received2: " + crunchifyBuilder.toString());
		
		
		Gson gson = new GsonBuilder().create();
		
		Person newCandidat = gson.fromJson(crunchifyBuilder.toString(), Person.class);
		
		URI identifier = sesame.saveCandidat(newCandidat);

		if(identifier==null){
			return Response.status(500).build();
		}
		
		HashMap<String, Double> weigthSkills = SkillsWeight.getAllSkillsWeight(newCandidat.getSkills());
		System.out.println(weigthSkills);
		HashMap<String, Long> weightPDFs = new HashMap<>();
		for(model.File nameFile: newCandidat.getFiles()){
			File f = new File(nameFile.getId());
			try {
				HashMap<String, Long> weightPDFs_ = PDFWeigth.getHashMapAttachedFiles(f);
				for(String key : weightPDFs_.keySet()){
					if(weightPDFs.containsKey(key)){
						weightPDFs.put(key, weightPDFs.get(key)+weightPDFs_.get(key));
					}else{
						weightPDFs.put(key, weightPDFs_.get(key));
					}
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		HashMap<String, Double> hashmapFinal = SkillsWeight.mergeSkillAndPDF(weigthSkills, weightPDFs);
		System.out.println(hashmapFinal);
		
		 sesame.saveHM(identifier, hashmapFinal);

		
		// return HTTP response 200 in case of success
		return Response.status(200).entity(crunchifyBuilder.toString()).build();
	}
	
	@GET
	@Path("/globallist")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGlobalList(InputStream incomingData) throws URISyntaxException {
		ArrayList<Person> listPosition = sesame.getUsers();
		Gson gson = new GsonBuilder().create();			
		return Response.status(200).entity(gson.toJson(listPosition)).build();
	}
	
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getList(InputStream incomingData) throws URISyntaxException {
		
		
		ArrayList<Person> listPerson = sesame.getUsers();
		
		Gson gson = new GsonBuilder().create();
		String result = "{";
		int i = 0;
		for(Person person : listPerson){
			result += "\""+i+"\": { \"lastname\":\""+ person.getName()+"\", \"firstname\":\""+ person.getFirstname() +"\", \"position\": \""+(i+1)+"\", \"isSelected\":\"false\", \"skills\":{ \"computer\":{";
			int j = 0;
			for(Skill s: person.getSkills()){
				result+="\""+s.getName()+"\":{ \"years\": "+ s.getYearOfExperience()+", \"level\":"+ s.getLevel()+"}";	
				if(j != person.getSkills().size()-1){
					result +=",";
				}
				j++;
			}
			result+="}}}";
			if(i != listPerson.size()-1){
				result +=",";	
			}
			i++;
		}
		result+="}";

		System.out.println(result);
		
				
		return Response.status(200).entity(result).build();
	}
	
	
	
	

//
//	$scope.candidats = {
//			0:{
//		firstname:"Pauline",
//		lastname:"Bou Khaled",
//		position: 10,
//		skills:{
//		computer:{
//		"java":{
//		years:2,
//		level: 1
//	},
//		"cpp":{
//		years:4,
//		level: 2
//	},
//		"html":{
//		years:10,
//		level: 1
//	},
//		"css":{
//		years:1,
//		level: 2
//	}
//	}
//	},
//		isSelected: true
//	},
//
//
//	}

	
//	@POST
//	@Path("/RestService")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response getWords(InputStream incomingData) {
//		StringBuilder crunchifyBuilder = new StringBuilder();
//		try {
//			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
//			String line = null;
//			while ((line = in.readLine()) != null) {
//				crunchifyBuilder.append(line);
//			}
//		} catch (Exception e) {
//			System.out.println("Error Parsing: - ");
//		}
//		System.out.println("Data Received2: " + crunchifyBuilder.toString());
//		// return HTTP response 200 in case of success
//		return Response.status(200).entity(crunchifyBuilder.toString()).build();
//	}
	
	


//	@POST
//	@Path("/spotlight")
//	@Produces(MediaType.APPLICATION_ATOM_XML)
//	public Response getAnnotations(InputStream incomingData) throws ClientProtocolException, IOException {
//		String url = "http://spotlight.sztaki.hu:2222/rest/annotate?text=Michelle%20Obama%20called%20Thursday%20on%20Congress%20to%20extend%20a%20tax%20break%20for%20students%20included%20in%20last%20year%27s%20economic%20stimulus%20package,%20arguing%20that%20the%20policy%20provides%20more%20generous%20assistance.&confidence=0.2&support=20";
//		System.out.println("spotligth lauched");
//		//return HTTP response 200 in case of success
//		HttpClient client = new DefaultHttpClient();
//		HttpGet request = new HttpGet(url);
//		request.addHeader("accept", "application/json");
//		System.out.println("spotligth lauched2");
//		HttpResponse response = client.execute(request);
//		System.out.println("spotligth lauched3");
//
//		BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
//		String line = "";
//		String result="";
//		while ((line = rd.readLine()) != null) {
//			result+=line;
//			System.out.println(line);
//		}
//		return Response.status(200).entity(result).build();
//	}

//	@POST
//	@Path("/post")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public String createDataInJSON(String data) { 
//
//		String result = data;
//
//		Gson gson = new Gson();
//		//convert the json string back to object
//		Person obj = gson.fromJson(data, Person.class);
//
//		System.out.println(obj.getNom());
//
//		String sesameServer = "http://semantic.ilab-research.ch:8080/openrdf-sesame/";
//		String repositoryID = "titi";
//
//		Repository repo = new HTTPRepository(sesameServer, repositoryID);
//		repo.initialize();
//		ValueFactory f = repo.getValueFactory();
//		String namespace ="http://hf.ch/";
//		RepositoryConnection conn = repo.getConnection();
//		try {
//			conn.begin();
//			URI newPerson = f.createURI(namespace, obj.getNom());
//			// Add statements to the rep John is a Person, John's name is John 
//			conn.add(newPerson, RDF.TYPE, FOAF.PERSON);
//			conn.add(newPerson, RDFS.LABEL, f.createLiteral(obj.getNom(), XMLSchema.STRING));		
//			conn.add(newPerson, FOAF.FAMILY_NAME, f.createLiteral(obj.getPrenom(), XMLSchema.STRING));
//			conn.add(newPerson, FOAF.MEMBER, f.createLiteral("true", XMLSchema.BOOLEAN));
//			conn.commit();		
//		}finally{
//			conn.close();
//		} 
//		//System.out.println(result);
//		return result; 
//	}
//
//	@POST
//	@Path("/delete")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public String deleteDataInJSON(String data) { 
//
//		String result = data;
//
//		Gson gson = new Gson();
//		//convert the json string back to object
//		Person obj = gson.fromJson(data, Person.class);
//
//		System.out.println(obj.getNom());
//
//		String sesameServer = "http://semantic.ilab-research.ch:8080/openrdf-sesame/";
//		String repositoryID = "titi";
//
//		Repository repo = new HTTPRepository(sesameServer, repositoryID);
//		repo.initialize();
//		ValueFactory f = repo.getValueFactory();
//		String namespace ="http://hf.ch/";
//		RepositoryConnection conn = repo.getConnection();
//		try {
//			conn.begin();
//			URI newPerson = f.createURI(namespace, obj.getNom());
//			// Add statements to the rep John is a Person, John's name is John 
//			//conn.add(newPerson, FOAF.MEMBER, f.createLiteral("false", XMLSchema.BOOLEAN));
//			conn.commit();		
//		}finally{
//			conn.close();
//		} 
//		//System.out.println(result);
//		return result; 
//	}



//
//	@GET
//	@Path("/")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response listTags(@QueryParam("skill") String id) throws IOException {		
//		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
//		loggerContext.stop();
//		System.out.println(id);
//		SkillsWeight.map.put(id, 1.0);
//		SkillsWeight.getInfluencedBy("http://dbpedia.org/resource/"+id, "0", SkillsWeight.LEVEL, 1);
//		for (String mapKey : SkillsWeight.map.keySet()) {
//			System.out.println(mapKey+" a "+ SkillsWeight.map.get(mapKey));
//		}
//		
//		for (String mapKey : UploadPDF.getFrequency().keySet()) {
//			if(SkillsWeight.map.containsKey(mapKey)){
//				SkillsWeight.map.put(mapKey, SkillsWeight.map.get(mapKey)+UploadPDF.getFrequency().get(mapKey));
//			}else{
//				SkillsWeight.map.put(mapKey, (double)UploadPDF.getFrequency().get(mapKey));
//			}
//		}
//
//		SkillsWeight.ValueComparator comparateur = new SkillsWeight.ValueComparator(SkillsWeight.map);
//		TreeMap<String,Double> mapTriee = new TreeMap<String,Double>(comparateur);
//		mapTriee.putAll(SkillsWeight.map);
//		ArrayList<Tag> finalTags = new ArrayList<>();
//		int count = 0;
//		for (String mapKey : mapTriee.keySet()) {
//			if(count<mapTriee.size() && count<NUMBER_OF_TAGS){
//				count++;
//				Tag tg= new Tag(mapKey);
//				finalTags.add(tg);
//			}
//		}
//		return Response.status(200).entity(finalTags).build();
//	}

//	@GET
//	@Path("/listPerson")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response listPerson(InputStream incomingData) {
//		String sesameServer = "http://semantic.ilab-research.ch:8080/openrdf-sesame/";
//		String repositoryID = "titi";
//		Repository repo = new HTTPRepository(sesameServer, repositoryID);
//		repo.initialize();
//		ValueFactory f = repo.getValueFactory();
//		RepositoryConnection conn = repo.getConnection();
//		ArrayList<Person> listpersons = new ArrayList<Person>();
//		try {
//			conn.begin();
//			String query="Select ?name ?familyname ?member where {?person <"+RDF.TYPE+"> <"+FOAF.PERSON+">"+
//					" . ?person <"+RDFS.LABEL+"> ?name"
//					+" . ?person <"+FOAF.FAMILY_NAME+"> ?familyname"
//					+" . ?person <"+FOAF.MEMBER+"> ?member}";
//			System.out.println(query);
//			TupleQuery result = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
//			TupleQueryResult res = result.evaluate();
//			while(res.hasNext()){
//				BindingSet actualRes = res.next();
//				Person personJohn = new Person();
//				if(actualRes.getValue("member").stringValue().compareTo("true")==0){
//					personJohn.setNom(actualRes.getValue("name").stringValue());
//					personJohn.setPrenom(actualRes.getValue("familyname").stringValue());
//					System.out.println("Name: "+actualRes.getValue("name").stringValue()+" Family Name:"+actualRes.getValue("familyname").stringValue());
//					listpersons.add(personJohn);
//				}
//			}
//			conn.commit();
//		}finally{
//			conn.close();
//		}
//		return Response.status(200).entity(listpersons).build();
//	}



}