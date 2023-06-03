package view;

import javax.swing.*;
import java.awt.*;

public class ViewMain extends JFrame {

    private JPanel panelMain;
    private JLabel labelGithubUrl;
    private JLabel labelTimeRemaining;
    private JButton buttonStartStop;
    private JCheckBox checkBoxRunForever;
    private JCheckBox checkBoxSaveFindings;
    private JLabel labelTimeToRun;
    private JLabel labelThreadCount;
    private JTextField textFieldTimeToRun;
    private JTextField textFieldThreadCount;
    private JList<String> listResults;
    private JLabel labelResults;
    private DefaultListModel<String> listResultsModel;

    private void init() {

        textFieldTimeToRun.setText("60");
        textFieldThreadCount.setText(String.valueOf(Runtime.getRuntime().availableProcessors()));

        DefaultListCellRenderer listResultsRenderer = (DefaultListCellRenderer) listResults.getCellRenderer();
        listResultsRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        listResultsModel = new DefaultListModel<>();
        listResults.setModel(listResultsModel);

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