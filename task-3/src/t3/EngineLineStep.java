package t3;

public class EngineLineStep implements ILineStep{
	 @Override
	 public IProductPart buildProductPart() {
	    CarEngine engine = new CarEngine();
	    System.out.println("Двигатель готов");
	    return engine;
	}
}
