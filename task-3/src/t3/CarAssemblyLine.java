package t3;

public class CarAssemblyLine implements IAssemblyLine{
	private ILineStep bodyStep;
    private ILineStep chassisStep;
    private ILineStep engineStep;
    
    public CarAssemblyLine(ILineStep bodyStep, ILineStep chassisStep, ILineStep engineStep) {
        this.bodyStep = bodyStep;
        this.chassisStep = chassisStep;
        this.engineStep = engineStep;
    }
    
    @Override
    public IProduct assembleProduct(IProduct product) {
        System.out.println("Запуск сборочной линии");

        System.out.println("Установка кузова");
        IProductPart body = bodyStep.buildProductPart();
        product.installFirstPart(body);
        
        System.out.println("Установка шасси");
        IProductPart chassis = chassisStep.buildProductPart();
        product.installSecondPart(chassis);
       
        System.out.println("Установка двигателя");
        IProductPart engine = engineStep.buildProductPart();
        product.installThirdPart(engine);
        
        System.out.println("Сборка завершена");
        
        return product;
    }
}
