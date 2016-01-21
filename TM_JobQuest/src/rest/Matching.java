package rest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openrdf.model.URI;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.InputPersonId;
import model.InputPositionId;
import model.Position;
import model.OutputPersonSimilarity;
import model.OutputPositionSimilarity;
import processing.Similarity;
import processing.SkillsWeight;

@Path("/matching")
public class Matching {
	
	/*
	 * {
	 * 	positionId : URI
	 * }
	 */
	
	/*
	 * [
	 * 	{
	 * 		userId : URI
	 * 		similarity : double
	 * 	},
	 * 	{
	 * 		userId : URI
	 * 		similarity : double
	 * 	}
	 * ]
	 */
	@POST
	@Path("/position")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response findBestUsersforPosition(String incomingData) {

		Gson gson = new GsonBuilder().create();
		
		InputPositionId inputPositionId = gson.fromJson(incomingData, InputPositionId.class);
		
		List<OutputPersonSimilarity> outputPersonSimilarities = Similarity.processSimilarityFromPosition(inputPositionId.positionId);

		return Response.status(200).entity(gson.toJson(outputPersonSimilarities)).build();
	}
	
	
	/*
	 * {
	 * 	userId : URI
	 * }
	 */
	
	/*
	 * [
	 * 	{
	 * 		positionId : URI
	 * 		similarity : double
	 * 	},
	 * 	{
	 * 		positionId : URI
	 * 		similarity : double
	 * 	}
	 * ]
	 */
	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response findBestPositionsforUser(String incomingData) {

		System.out.println(incomingData);
		Gson gson = new GsonBuilder().create();
		
		InputPersonId inputPersonId = gson.fromJson(incomingData, InputPersonId.class);
		
		List<OutputPositionSimilarity> OutputPositionSimilarities = Similarity.processSimilarityFromPerson(inputPersonId.personId);
		
		return Response.status(200).entity(gson.toJson(OutputPositionSimilarities)).build();

	}

}
