package upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.net.ssl.SSLEngineResult.Status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class FileLoader {
	
	private static String TWEET_PATH = "/Users/paulineboukhaled/Desktop/git/TM_JobQuest/Java_(programming_language)3_2.json";
	private static String ARTICLE_PATH = "./dataArticles";




	/**
	 * Recuperation du calcul des skills depuis un fichier JSON
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, Double> getAllGoodTweets(String path) throws IOException {
		// On ouvre le fichier qui contient les tweets
		BufferedReader br = new BufferedReader(new FileReader(path));
		String content = "";
		String line;
		while((line = br.readLine())!=null) {
			content += line + "\n";
		}
		br.close();

		// Depuis le json on recupere les tweets dans une liste de tweet
		Gson json = new Gson();
		HashMap<String, Double> skills = json.fromJson(content, new TypeToken<HashMap<String, Double>>() {}.getType());
		return skills;
	}


	



	/*public static ArrayList<POJOStatus> getAllTweets(String path) throws IOException {
		// On ouvre le fichier qui contient les tweets
				BufferedReader br = new BufferedReader(new FileReader(path));
				String content = "";
				String line;
				while((line = br.readLine())!=null) {
					content += line + "\n";
				}
//				System.out.println(content);

				
				br.close();

				ArrayList<POJOStatus> tweets2 = new ArrayList<POJOStatus>();
				content= content.substring(1, content.length()-1);

				Gson gson= new GsonBuilder().setPrettyPrinting().create();
				int bug = 0;
				String[] toks = content.split("\\{\"createdAt");
				for (int i = 1 ; i < toks.length ; i++) {
					String tok = toks[i];
					tok = "{\"createAt" + tok;
					tok = tok.substring(0, tok.length() -1);
					
					try {
						POJOStatus obj = gson.fromJson(tok, POJOStatus.class);
						tweets2.add(obj);
					} catch(Exception e) {
						System.err.println("Petit bug... : " + bug++);
					}
					
				
				}
				System.out.println(toks.length);

//				Gson json = new Gson();
//				ArrayList<POJOStatus> tweets2 = json.fromJson(br, new TypeToken<ArrayList<POJOStatus>>() {}.getType());

				
				// Depuis le json on recupere les tweets dans une liste de tweet
//				Gson json = new Gson();
//				ArrayList<POJOStatus> tweets2 = json.fromJson(content, new TypeToken<ArrayList<POJOStatus>>() {}.getType());
//				return tweets2;
				
				return tweets2;
	}*/

	
}
