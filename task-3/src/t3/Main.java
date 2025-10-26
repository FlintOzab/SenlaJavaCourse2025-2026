package t3;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	     ILineStep bodyStep = new BodyLineStep();
	     ILineStep chassisStep = new ChassisLineStep();
	     ILineStep engineStep = new EngineLineStep();
	     IAssemblyLine assemblyLine = new CarAssemblyLine(bodyStep, chassisStep, engineStep);
	     IProduct car = new Car();
	     IProduct assembledCar = assemblyLine.assembleProduct(car);
	}

}
