package t3;

public class BodyLineStep implements ILineStep{
	 @Override
	 public IProductPart buildProductPart() {
	    CarBody body = new CarBody();
	    System.out.println("Кузов готов");
	    return body;
	}
}
