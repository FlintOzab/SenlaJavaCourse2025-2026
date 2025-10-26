package t1;

public class Main {

	public static void main(String[] args) {
		int random = new java.util.Random().nextInt(100, 999);
		int max = 0;
		System.out.println(random);
		for (int i = 1; i <= 3; i++)
		{
			int num = random % 10;
			if ( num > max)
			{
				max = num;
			}
			if (max == 9) { break; }
			random = random/10;
		}
		System.out.println(max);
		
	}

}
