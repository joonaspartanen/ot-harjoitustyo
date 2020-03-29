package recipebook;

import java.util.Scanner;
import recipebook.ui.TextUi;

public class Main {

    public static void main(String[] args) {

        Scanner reader = new Scanner(System.in);
        TextUi tui = new TextUi(reader);
        tui.start();
    }
}
