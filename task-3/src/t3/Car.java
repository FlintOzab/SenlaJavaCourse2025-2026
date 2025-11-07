package t3;

public class Car implements IProduct {
	 private CarBody body;
	 private CarChassis chassis;
	 private CarEngine engine;
	 @Override
	    public void installFirstPart(IProductPart part) {
	            this.body = (CarBody) part;
	            System.out.println("Установлен кузов" );
	    }
	    
	    @Override
	    public void installSecondPart(IProductPart part) {
	            this.chassis = (CarChassis) part;
	            System.out.println("Установлено шасси");
	    }
	    
	    @Override
	    public void installThirdPart(IProductPart part) {
	            this.engine = (CarEngine) part;
	            System.out.println("Установлен двигатель");
	    }
}
