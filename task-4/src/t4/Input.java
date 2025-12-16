package t4;

import java.util.Date;

public interface Input {
	String readString(String prompt);
    int readInt(String prompt);
    long readLong(String prompt);
    Date readDate(String prompt);
}
