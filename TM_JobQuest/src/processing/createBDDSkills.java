package processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import model.SkillBDD;

public class createBDDSkills {
	
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("uri.txt"));
		String content = "";
		String line;
		ArrayList<SkillBDD> bdd = new ArrayList<>();
		while((line = br.readLine())!=null) {
			SkillBDD x = new SkillBDD("", line);
			bdd.add(x);
			content += line + "\n";
		}
		br.close();
		br = new BufferedReader(new FileReader("name.txt"));
		ArrayList<SkillBDD> bdd2 = new ArrayList<>();
		while((line = br.readLine())!=null) {
			SkillBDD x = new SkillBDD(line, "");
			bdd2.add(x);
			content += line + "\n";
		}
		int i=0;
		for(SkillBDD s : bdd){
			s.setName(bdd2.get(i).getName());
			i++;
		}
		br.close();


		PrintWriter pw=null;
		// CREATION FILES FOR WEIGTHS
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		pw = new PrintWriter("skillsdbpedia.json");
		pw.write(gson.toJson(bdd));
		pw.close();
	}

}
