import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.junit.Test;


public class Fuzzer {
	private final String USER_AGENT = "Mozilla/5.0";
	
	@Test
	public void setup() {
		// TODO Auto-generated method stub
		Swagger swagger = new SwaggerParser().read("swagger.json");
		String basePath = "http://localhost:8080" + swagger.getBasePath();
		
		//System.out.println(swagger.getPaths());
		for(Entry<String, Path> entry : swagger.getPaths().entrySet()) {
		    String cle = entry.getKey();
		    Path p = entry.getValue();
		    
		    System.out.println(p.getOperationMap());
		    
		    try {
				this.fuzzing(basePath + cle);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    System.out.println(cle);
		}
	}
	
	public void fuzzing(String path) throws Exception {
		//this.get(path);
		this.post(path);
	}
	
	public void get(String url) throws Exception{

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
	}
	
	public void post(String url) throws Exception{
		JSONObject json = new JSONObject();
		json.put("id", 666);
		json.put("name", "Jacky");
		json.put("tag", "Salut");
		
		URL object=new URL(url);

		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		
		//add request header
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept", "application/json");
		con.setRequestMethod("POST");

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(json.toJSONString());
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + json.toJSONString());
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
	}
}
