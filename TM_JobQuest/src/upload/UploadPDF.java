package upload;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class UploadPDF
 */
@WebServlet("/UploadPDF")
public class UploadPDF extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletFileUpload uploader = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadPDF() {
        super();
        // TODO Auto-generated constructor stub
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

				File file = new File("./"+File.separator+fileItem.getName());
				//System.out.println(TextExtractor.pdftoText(file.getPath()));
				//String cat = TextExtractor.pdftoText(file.getPath());
				String cat2 = OCRRestAPI.pdfToTxt(file.getPath());
				System.out.println(cat2);
				//JsonParser parser = new JsonParser();
				//JsonObject o = parser.parse(cat2).getAsJsonObject();
				//JsonElement jsonObject = o.get("OCRText");

				//String url = "http://spotlight.sztaki.hu:2222/rest/annotate?text="+jsonObject.getString("OCRText")+"&confidence=0.2&support=20";
	
				//String url = "http://spotlight.sztaki.hu:2222/rest/annotate?text="+cat2+"&confidence=0.2&support=20";
				
				/*System.out.println("spotligth lauched:"+ url);
				//return HTTP response 200 in case of success
				HttpClient client = new DefaultHttpClient();
				HttpGet request2 = new HttpGet(url);
				request2.addHeader("accept", "application/json");
				HttpResponse response2 = client.execute(request2);

				//convert the json string back to object
				System.out.println("Absolute Path at server="+file.getAbsolutePath());

				BufferedReader rd = new BufferedReader (new InputStreamReader(response2.getEntity().getContent()));
				String line = "";
				String result="";
				while ((line = rd.readLine()) != null) {
					result+=line;
					System.out.println(line);
					out.write(line);

				}*/
				
				fileItem.write(file);
				out.write("File "+fileItem.getName()+ " uploaded successfully.");
				//out.write("Le contenu est :"+cat2);
				out.write("<br>");
				out.write("<a href=\"UploadDownloadFileServlet?fileName="+fileItem.getName()+"\">Download "+fileItem.getName()+"</a>");
			}
		} catch (FileUploadException e) {
			out.write("Exception in uploading file.");
		} catch (Exception e) {
			out.write("Exception in uploading file.");
		}
		out.write("</body></html>");
	}

}
