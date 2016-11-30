import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;

import java.util.Map.Entry;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Swagger swagger = new SwaggerParser().read("swagger.json");
		
		//System.out.println(swagger.getPaths());
		for(Entry<String, Path> entry : swagger.getPaths().entrySet()) {
		    String cle = entry.getKey();
		    
		   // Fuzzer.
		    
		    System.out.println(cle);
		}
	}

}
