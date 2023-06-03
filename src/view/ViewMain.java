package view;

import javax.swing.*;
import java.awt.*;

public class ViewMain extends JFrame {

    private JPanel panelMain;

    private void init() {

        setContentPane(panelMain);
        setTitle("Mersenne");
        setPreferredSize(new Dimension(640, 480));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    public ViewMain() {

        SwingUtilities.invokeLater(this::init);

    }

}
