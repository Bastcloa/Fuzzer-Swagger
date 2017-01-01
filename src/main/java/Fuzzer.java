import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import io.swagger.models.HttpMethod;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.parser.SwaggerParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.hamcrest.CoreMatchers;
import org.json.simple.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

public class Fuzzer {
	private final String USER_AGENT = "Mozilla/5.0";
	
	private final int NBTESTS = 1;

	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@Test
	public void setup() {
		Swagger swagger = new SwaggerParser().read("swagger.json");
		String basePath = "http://localhost:8080" + swagger.getBasePath();

		// System.out.println(swagger.getPaths());
		for (Entry<String, Path> entry : swagger.getPaths().entrySet()) {
			String cle = entry.getKey();
			Path p = entry.getValue();

			Map<HttpMethod, Operation> listOp = p.getOperationMap();
			
			System.out.print("\n\nTest " + cle);
			
			for (Entry<HttpMethod, Operation> list : listOp.entrySet()) {
				HttpMethod method = list.getKey();
				Operation operation = list.getValue();

				System.out.println("\nTest de la méthode " + method + " " + cle);
				System.out.print("Sending request");
				for (int i = 0; i < NBTESTS; i++) {
					System.out.print(".");
					try {
						Pair<Integer, String> result = this.fuzzing(basePath
								+ cle, method.toString(),
								operation.getParameters());

						int codeRetour = result.first();
						String messageRetour = result.second();

						collector
								.checkThat(
										"Erreur test de la méthode "
												+ method
												+ " "
												+ cle
												+ "\nLa méthode appelée ne renvoie pas le bon code retour.\nCode retournée : "
												+ codeRetour
												+ "\nCode attendu : "
												+ operation.getResponses()
														.keySet()
												+ "\nMessage retourné : "
												+ messageRetour,
										operation.getResponses().containsKey(
												String.valueOf(codeRetour)),
										CoreMatchers.equalTo(true));
					} catch (Exception e) {
						e.printStackTrace();
						fail("Connection error");
					}
				}
			}

		}
	}

	public Pair<Integer, String> fuzzing(String path, String method,
			List<Parameter> params) throws Exception {
		if (method.equals("GET")) {
			return this.get(path, params);
		} else {
			return this.otherRequest(path, params, method);
		}
	}

	public Pair<Integer, String> get(String url, List<Parameter> params)
			throws Exception {

		for (Parameter p : params) {
			if (p.getRequired()) {
				if (p.getIn().equals("path")) {
					url = url.replaceAll("\\{.*?\\}",
							String.valueOf(new Random().nextInt()));
				}
			}
		}

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = 0;
		StringBuffer response = new StringBuffer();

		try {
			responseCode = con.getResponseCode();
			//System.out.println("\nSending 'GET' request to URL : " + url);
			//System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

		} catch (IOException e) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getErrorStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		}
		// print result
		//System.out.println(response.toString());
		return new Pair<Integer, String>(responseCode, response.toString());
	}

	public Pair<Integer, String> otherRequest(String url,
			List<Parameter> params, String method) throws Exception {

		JSONObject json = new JSONObject();
		
		for (Parameter p : params) {
			if (p.getRequired()) {
				if (p.getIn().equals("path")) {
					url = url.replaceAll("\\{.*?\\}",
							String.valueOf(new Random().nextInt()));
				}
				
				if (p.getIn().equals("body")) {
					json.put("id", 1);
					json.put("name", "ThePet");
					json.put("tag", "TheTag");
				}
			}
		}		

		URL object = new URL(url);

		HttpURLConnection con = (HttpURLConnection) object.openConnection();

		// add request header
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

		int responseCode = 0;
		StringBuffer response = new StringBuffer();

		try {
			responseCode = con.getResponseCode();
			//System.out.println("\nSending '" + method + "' request to URL : "
			//		+ url);
			//System.out.println("Post parameters : " + json.toJSONString());
			//System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (IOException e) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getErrorStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		}
		// print result
		//System.out.println(response.toString());
		return new Pair<Integer, String>(responseCode, response.toString());
	}
}
