import static org.junit.Assert.*;
import io.swagger.models.HttpMethod;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.parser.SwaggerParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import junit.framework.Assert;

import org.json.simple.JSONObject;
import org.junit.Test;


public class Fuzzer {
	private final String USER_AGENT = "Mozilla/5.0";
	
	@Test
	public void setup() {
		Swagger swagger = new SwaggerParser().read("swagger.json");
		String basePath = "http://localhost:8080" + swagger.getBasePath();
		
		//System.out.println(swagger.getPaths());
		for(Entry<String, Path> entry : swagger.getPaths().entrySet()) {
		    String cle = entry.getKey();
		    Path p = entry.getValue();
		    
		    Map<HttpMethod, Operation> listOp = p.getOperationMap();
		    
		    for(Entry<HttpMethod, Operation> list : listOp.entrySet()) {
			    HttpMethod method = list.getKey();
			    Operation operation = list.getValue();
			    
			    System.out.println(method);
			    
			    // If getIn() == path  et require remplacer {id} par un chiffre
			    // chiffre aléatoire + modifier serveur node pour renvoyer erreur
			    System.out.println(operation.getParameters().get(0).getIn());
			    
			    try {
					JSONObject json = new JSONObject();
					json.put("id", 666);
					json.put("name", "Jacky");
					json.put("tag", "Salut");
					
					// for verifier code retour avec getResponses()
					System.out.println(operation.getResponses());
					assertTrue("La méthode appelée ne renvoie pas le bon code retour", operation.getResponses().containsKey(String.valueOf(this.fuzzing(basePath + cle, method.toString(), operation.getParameters()))));
				} catch (Exception e) {
					e.printStackTrace();
					fail("Connection error");
				}
		    }
		    
		    System.out.println(cle);
		}
	}
	
	public int fuzzing(String path, String method, List<Parameter> params) throws Exception {
		if(method.equals("GET")){
			return this.get(path, params);
		}
		else{
			return this.otherRequest(path, params, method);
		}
	}
	
	public int get(String url, List<Parameter> params) throws Exception{
		
		for (Parameter p : params) {
			if (p.getRequired()) {
				if (p.getIn().equals("path")) {
					url = url.replaceAll("\\{.*?\\}", String.valueOf(new Random().nextInt()));
				}
			}
		}

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
		return responseCode;
	}
	
	public int otherRequest(String url, List<Parameter> params, String method) throws Exception{
		
		for (Parameter p : params) {
			if (p.getRequired()) {
				if (p.getIn().equals("path")) {
					url = url.replaceAll("\\{.*?\\}", String.valueOf(new Random().nextInt()));
				}
			}
		}JSONObject json = new JSONObject();
		json.put("id", 666);
		json.put("name", "Jacky");
		json.put("tag", "Salut");
		
		URL object=new URL(url);

		HttpURLConnection con = (HttpURLConnection) object.openConnection();
		
		//add request header
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept", "application/json");
		con.setRequestMethod(method);

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(json.toJSONString());
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending '" + method + "' request to URL : " + url);
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
		return responseCode;
	}
}
