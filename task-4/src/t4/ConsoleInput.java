package t4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

@Component
public class ConsoleInput implements Input {
    
    private final Scanner scanner;
    private final SimpleDateFormat dateFormat;
    
    public ConsoleInput() {
        this.scanner = new Scanner(System.in);
        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    }
    
    @Override
    public String readString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine();
    }
    
    @Override
    public int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число");
            }
        }
    }
    
    @Override
    public long readLong(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число");
            }
        }
    }
    
    @Override
    public Date readDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + " (дд.мм.гггг): ");
                String dateStr = scanner.nextLine();
                return dateFormat.parse(dateStr);
            } catch (ParseException e) {
                System.out.println("Ошибка: введите дату в формате дд.мм.гггг");
            }
        }
    }
}
