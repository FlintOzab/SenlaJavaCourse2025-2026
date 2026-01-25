package t4.view;

import java.util.List;

public interface Display {
	 void showMessage(String message);
	 void showError(String error);
	 void showList(List<?> items);
}
