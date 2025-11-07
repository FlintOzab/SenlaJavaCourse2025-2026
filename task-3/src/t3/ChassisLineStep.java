package t3;

public class ChassisLineStep implements ILineStep{
	 @Override
	 public IProductPart buildProductPart() {
	    CarChassis chassis = new CarChassis();
	    System.out.println("Шасси готово");
	    return chassis;
	}
}
