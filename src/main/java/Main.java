import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Swagger swagger = new SwaggerParser().read("swagger.json");
		
		System.out.println(swagger.getPaths());
	}

}
