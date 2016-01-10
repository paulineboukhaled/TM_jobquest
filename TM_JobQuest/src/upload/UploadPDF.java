package upload;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.pdfbox.PDFReader;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ch.qos.logback.core.net.SyslogOutputStream;

/**
 * Servlet implementation class UploadPDF
 */
@WebServlet("/UploadPDF")
public class UploadPDF extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletFileUpload uploader = null;
	private static HashMap<String, Long> frequency= new HashMap<>() ;


	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadPDF() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	static class ValueComparator implements Comparator<String> {
		Map<String, Long> base;
		
		public ValueComparator(Map<String, Long> base) {
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

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		DiskFileItemFactory fileFactory = new DiskFileItemFactory();
		File filesDir = new File(".");
		fileFactory.setRepository(filesDir);
		this.uploader = new ServletFileUpload(fileFactory);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(!ServletFileUpload.isMultipartContent(request)){
			throw new ServletException("Content type is not multipart/form-data");
		}

		ArrayList<String> keywords = new ArrayList<>();
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.write("<html><head></head><body>");
		try {
			List<FileItem> fileItemsList = uploader.parseRequest(request);
			Iterator<FileItem> fileItemsIterator = fileItemsList.iterator();
			while(fileItemsIterator.hasNext()){
				FileItem fileItem = fileItemsIterator.next();
				System.out.println("FieldName="+fileItem.getFieldName());
				System.out.println("FileName="+fileItem.getName());
				System.out.println("ContentType="+fileItem.getContentType());
				System.out.println("Size in bytes="+fileItem.getSize());
				if(fileItem.getContentType()!=null && fileItem.getFieldName()!=null){

					File file = new File("./"+File.separator+fileItem.getName());
					String cat2 = OCRRestAPI.pdfToTxt(file.getPath());

					Gson gson = new GsonBuilder().create();
					ResponseOCR ocr = gson.fromJson(cat2, ResponseOCR.class);
					System.out.println(ocr.getOCRText());
					int i = 0;
					for (List<String> v : ocr.getOCRText()){
						for(String j : v){
							String x = j.replaceAll("[^A-Za-z0-9 ]","");
							x = x.replaceAll("\\s", "%20");
							String url = "http://spotlight.sztaki.hu:2222/rest/annotate?text="+x+"&confidence=0.2&support=20";
							HttpClient client = new DefaultHttpClient();
							HttpGet request2 = new HttpGet(url);
							request2.addHeader("accept", "application/json");
							HttpResponse response2 = client.execute(request2);
							int code = response2.getStatusLine().getStatusCode();
							if(code==200){
								BufferedReader rd = new BufferedReader (new InputStreamReader(response2.getEntity().getContent()));
								String line = "";
								String result="";
								while ((line = rd.readLine()) != null) {
									result+=line;
								}
								gson = new GsonBuilder().create();
								ResponseSpotlight resultSpotlight = gson.fromJson(result, ResponseSpotlight.class);
								for(ResponseSpotlight.Resource r : resultSpotlight.getResources()){
									String uri = r.getURI();
									uri = uri.replace("http://dbpedia.org/resource/", "");
									uri = uri.replace("_", " ");
									keywords.add(uri);
									if(frequency.containsKey(uri)){
										frequency.put(uri, frequency.get(uri)+1);
									}else{
										frequency.put(uri, (long) 1);
									}
								}
							}
						}
					}
				}
				
				// TRI DE LA MAP
				ValueComparator comparateur = new ValueComparator(frequency);
				TreeMap<String,Long> mapTriee = new TreeMap<String,Long>(comparateur);
				mapTriee.putAll(frequency);
				System.out.println("resultat du tri: "+ mapTriee);
				//fileItem.write(file);
				out.write("File "+fileItem.getName()+ " uploaded successfully.");
				out.write("<br>");
				out.write("<a href=\"UploadDownloadFileServlet?fileName="+fileItem.getName()+"\">Download "+fileItem.getName()+"</a>");
			}
		} catch (FileUploadException e) {
			out.write("Exception in file uploading file.");
		} catch (Exception e) {
			out.write("Exception in uploading file.");
			e.printStackTrace();
		}
		out.write("</body></html>");
	}
	
	public static HashMap<String, Long> getHashMapAttachedFiles(File file) throws Exception {
		String cat2 = OCRRestAPI.pdfToTxt(file.getPath());
		Gson gson = new GsonBuilder().create();
		ResponseOCR ocr = gson.fromJson(cat2, ResponseOCR.class);
		System.out.println(ocr.getOCRText());
		int i = 0;
		ArrayList<String> keywords = new ArrayList<>();
		for (List<String> v : ocr.getOCRText()){
			for(String j : v){
				String x = j.replaceAll("[^A-Za-z0-9 ]","");
				x = x.replaceAll("\\s", "%20");
				String url = "http://spotlight.sztaki.hu:2222/rest/annotate?text="+x+"&confidence=0.2&support=20";
				HttpClient client = new DefaultHttpClient();
				HttpGet request2 = new HttpGet(url);
				request2.addHeader("accept", "application/json");
				HttpResponse response2 = client.execute(request2);
				int code = response2.getStatusLine().getStatusCode();
				if(code==200){
					BufferedReader rd = new BufferedReader (new InputStreamReader(response2.getEntity().getContent()));
					String line = "";
					String result="";
					while ((line = rd.readLine()) != null) {
						result+=line;
					}
					gson = new GsonBuilder().create();
					ResponseSpotlight resultSpotlight = gson.fromJson(result, ResponseSpotlight.class);
					for(ResponseSpotlight.Resource r : resultSpotlight.getResources()){
						String uri = r.getURI();
						uri = uri.replace("http://dbpedia.org/resource/", "");
						uri = uri.replace("_", " ");
						keywords.add(uri);
						if(frequency.containsKey(uri)){
							frequency.put(uri, frequency.get(uri)+1);
						}else{
							frequency.put(uri, (long) 1);
						}
					}
				}
			}
		getFrequency();
		}
		return frequency;
	}
	
	public static HashMap<String, Long> getFrequency() {
		return frequency;
	}

	public static void setFrequency(HashMap<String, Long> frequency) {
		UploadPDF.frequency = frequency;
	}

}
