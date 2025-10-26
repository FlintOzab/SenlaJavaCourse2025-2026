package t2;

import java.util.ArrayList;
import java.util.List;

public class Main {
	
	public enum Colors
	{
		RED, WHITE, PINK, YELLOW, PURPLE, ORANGE
	}
	
	public static abstract class Flower {
		protected String name;
		protected float price;
		protected Colors color;
		
		public Flower(String name, float price, Colors color)
		{
			this.name = name;
			this.price = price;
			this.color = color;
		}
		 public float getPrice() {return this.price;}
		 public String getName() {return this.name;}
		 public Colors getColor() {return this.color;}
		 public abstract String getFlower();
		}
	
	public static class Rose extends Flower
	{
		private boolean thorns;
		public Rose(String name, float price, Colors color, boolean thorns)
		{
			super(name, price, color); 
			this.thorns = thorns;
		}
		@Override
		public String getFlower()
		{
			String flower;
			if (this.thorns) flower = ("This is " + this.name + ", " + this.color + ", costs " + price + ". Has thorns.");
			else flower = ("This is " + this.name + ", " + this.color + ", costs " + price + ". Doesn't have thorns.");
			return flower;
		}
	}
	
	public static class Chrysanthemum extends Flower
	{
		private boolean oneYear;
		public Chrysanthemum(String name, float price, Colors color, boolean oneYear)
		{
			super(name, price, color); 
			this.oneYear = oneYear;
		}	
		@Override
		public String getFlower()
		{
			String flower;
			if (this.oneYear) flower = ("This is one-year " + this.name + ", " + this.color + ", costs " + price + ".");
			else flower = ("This is perennial " + this.name + ", " + this.color + ", costs " + price + ".");
			return flower;
		}
	}
	public static class Lily extends Flower
	{
		public Lily(String name, float price, Colors color)
		{
			super(name, price, color); 
		}
		@Override
		public String getFlower()
		{
			String flower;
			flower = ("This is " + this.name + ", " + this.color + ", costs " + price + ".");
			return flower;
		}
	}
		
	public static class Bouquet
	{
		private List<Flower> flowers;
		private String name;
		private int flowersNumber;
		private float price;
		
		public Bouquet(String name) {
	        this.name = name;
	        this.flowersNumber = 0;
	        this.price = 0;
	        this.flowers = new ArrayList<>();
	    }
	    
	    public void addFlower(Flower flower) {
	        flowers.add(flower);
	        this.flowersNumber++;
	        
	    }
	    
	    public void removeFlower(Flower flower) {
	        flowers.remove(flower);
	        this.flowersNumber--;
	    }

	    
	    public float countPrice() {
	        for (Flower flower : flowers) {
	            this.price += flower.getPrice();
	        }
	        return price;
	    }
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        System.out.println("Test 1");
        Rose rose1 = new Rose("Rosa gallica", 15.5f, Colors.RED, true);
        Chrysanthemum chrys1 = new Chrysanthemum("Chrysanthemum morifolium", 12.0f, Colors.WHITE, true);
        Lily lily1 = new Lily("Lilium candidum", 18.75f, Colors.WHITE);
        Bouquet bouqet1 = new Bouquet("Bouquet1");
        bouqet1.addFlower(rose1);
        bouqet1.addFlower(chrys1);
        bouqet1.addFlower(lily1);
        System.out.println("Цветы:");
        System.out.println(rose1.getFlower());
        System.out.println(chrys1.getFlower());
        System.out.println(lily1.getFlower());
        float springPrice = bouqet1.countPrice();
        System.out.println("Общая стоимость: " + springPrice);

        System.out.println("Test 2");
        Rose rose2 = new Rose("Rosa damascena", 20.0f, Colors.PINK, false);
        Rose rose3 = new Rose("Rosa rubiginosa", 17.25f, Colors.RED, true);
        Chrysanthemum chrys2 = new Chrysanthemum("Chrysanthemum indicum", 14.5f, Colors.YELLOW, false);
        Lily lily2 = new Lily("Lilium longiflorum", 22.0f, Colors.WHITE);
        Lily lily3 = new Lily("Lilium martagon", 19.8f, Colors.PINK);

        Bouquet bouquet2 = new Bouquet("Bouquet2");
        bouquet2.addFlower(rose2);
        bouquet2.addFlower(rose3);
        bouquet2.addFlower(chrys2);
        bouquet2.addFlower(lily2);
        bouquet2.addFlower(lily3);
        System.out.println("Цветы:");
        System.out.println(rose2.getFlower());
        System.out.println(rose3.getFlower());
        System.out.println(chrys2.getFlower());
        System.out.println(lily2.getFlower());
        System.out.println(lily3.getFlower());
        float romanticPrice = bouquet2.countPrice();
        System.out.println("Общая стоимость: " + romanticPrice);
    }
}



