import view.ViewMain;

import javax.swing.*;
import java.util.ResourceBundle;

public class Main {

    public static void main(String[] args) throws
            UnsupportedLookAndFeelException,
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException {

        ResourceBundle resourceBundleI8n = ResourceBundle.getBundle("i8n");

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(() -> new ViewMain(resourceBundleI8n));

    }

}
