package rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.http.HTTPRepository;

import com.google.common.reflect.Parameter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.Person;
import model.Position;
import processing.PDFWeigth;
import processing.SaveOnSesame;
import processing.SkillsWeight;

@Path("/position")
public class PositionInformation {

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
		
		Position newPosition = gson.fromJson(crunchifyBuilder.toString(), Position.class);

		URI identifier = sesame.savePosition(newPosition);
		System.out.println(identifier);
		

		if(identifier==null){
			return Response.status(500).build();
		}
		
		HashMap<String, Double> weigthSkills = SkillsWeight.getAllSkillsWeight(newPosition.getSkills());
		System.out.println(weigthSkills);		
		// return HTTP response 200 in case of success
		return Response.status(200).entity(crunchifyBuilder.toString()).build();
	}
	
	
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getList(InputStream incomingData) {
		
		
//		ArrayList<Position> listPosition = sesame.getPositions();
				
		return Response.status(200).entity("").build();
	}
}
