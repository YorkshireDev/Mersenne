package view;

import controller.ControllerMersenne;
import controller.ControllerSave;
import model.ModelFlowControl;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewMain extends JFrame {

    private JPanel panelMain;
    private JLabel labelTimeRemaining;
    private JButton buttonStartStop;
    private JCheckBox checkBoxRunForever;
    private JCheckBox checkBoxSaveFindings;
    private JTextField textFieldTimeToRun;
    private JTextField textFieldThreadCount;
    private JList<String> listResults;
    private DefaultListModel<String> listResultsModel;

    private ResourceBundle resourceBundlei8n;

    private int timeToRunHistory;
    private ControllerMersenne controllerMersenne;

    private void init() {

        textFieldTimeToRun.setText("60");
        timeToRunHistory = Integer.parseInt(textFieldTimeToRun.getText());
        textFieldThreadCount.setText(String.valueOf(Runtime.getRuntime().availableProcessors()));

        DefaultListCellRenderer listResultsRenderer = (DefaultListCellRenderer) listResults.getCellRenderer();
        listResultsRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        listResultsModel = new DefaultListModel<>();
        listResults.setModel(listResultsModel);

        checkBoxSaveFindings.setSelected(true);

        ModelFlowControl.latchControllerMersenne = new CountDownLatch(0);

        setContentPane(panelMain);
        setTitle("Mersenne");
        setPreferredSize(new Dimension(560, 302));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    public ViewMain(ResourceBundle resourceBundleI8n) {

        this.resourceBundlei8n = resourceBundleI8n;

        SwingUtilities.invokeLater(this::init);

        buttonStartStop.addActionListener(actionEvent -> {

            if (ModelFlowControl.latchControllerMersenne.getCount() > 0) {
                ModelFlowControl.latchViewMain.countDown(); // Tell the controller to stop...
                return;
            }

            buttonStartStop.setText(resourceBundleI8n.getString("stop"));

            ModelFlowControl.latchViewMain = new CountDownLatch(1);
            ModelFlowControl.latchControllerMersenne = new CountDownLatch(1);

            controllerMersenne = new ControllerMersenne(Integer.parseInt(textFieldThreadCount.getText()));

            ExecutorService threadPool = Executors.newFixedThreadPool(3);

            threadPool.submit(resultListUpdateThread);
            threadPool.submit(timeRemainingUpdateThread);
            threadPool.submit(controllerMersenne);

        });

        checkBoxRunForever.addActionListener(actionEvent -> {

            if (checkBoxRunForever.isSelected()) {

                SwingUtilities.invokeLater(() -> {
                    timeToRunHistory = Integer.parseInt(textFieldTimeToRun.getText());
                    textFieldTimeToRun.setText(resourceBundleI8n.getString("notApplicable"));
                    textFieldTimeToRun.setEnabled(false);
                });

            } else {

                SwingUtilities.invokeLater(() -> {
                    textFieldTimeToRun.setText(String.valueOf(timeToRunHistory));
                    textFieldTimeToRun.setEnabled(true);
                });

            }

        });

    }

    private final Runnable resultListUpdateThread = () -> {

        boolean saveFindings = checkBoxSaveFindings.isSelected();

        ControllerSave controllerSave = null;

        if (saveFindings) controllerSave = new ControllerSave();
        Set<Integer> resultSet = new HashSet<>();

        listResultsModel.clear();

        while (ModelFlowControl.latchControllerMersenne.getCount() > 0) {
            try {
                for (int exponent : ControllerMersenne.resultSet) {
                    if (resultSet.contains(exponent)) continue;
                    SwingUtilities.invokeLater(() -> listResultsModel.addElement("2^" + exponent + "-1"));
                    resultSet.add(exponent);
                    if (saveFindings) controllerSave.save(resultSet);
                }
                Thread.sleep(500L);
            } catch (InterruptedException e) { throw new RuntimeException(e); }
        }

    };

    private final Runnable timeRemainingUpdateThread = () -> {

        boolean runForever = checkBoxRunForever.isSelected();

        double timeRemaining;

        try { timeRemaining = Double.parseDouble(textFieldTimeToRun.getText()); }
        catch (NumberFormatException ignored) { timeRemaining = 0.0d; }

        final int[] runForeverIndex = {0};
        String[] runForeverAnimation = new String[] {".", "..", "...", "....", ".....", "....", "...", ".."};

        while (ModelFlowControl.latchControllerMersenne.getCount() > 0) {
            try {
                if (runForever) {
                    SwingUtilities.invokeLater(() -> labelTimeRemaining.setText(runForeverAnimation[runForeverIndex[0]++ % runForeverAnimation.length]));
                    Thread.sleep(500L);
                    continue;
                }
                int timeRemainingInt = (int) timeRemaining;
                if (timeRemainingInt >= 0)
                    SwingUtilities.invokeLater(() -> labelTimeRemaining.setText(String.valueOf(timeRemainingInt)));
                Thread.sleep(500L);
                timeRemaining -= 0.5d;
                if (timeRemainingInt <= 0) ModelFlowControl.latchViewMain.countDown(); // Tell the controller to stop...
            } catch (InterruptedException e) { throw new RuntimeException(e); }
        }

        SwingUtilities.invokeLater(() -> buttonStartStop.setText(resourceBundlei8n.getString("start")));
        SwingUtilities.invokeLater(() -> labelTimeRemaining.setText(resourceBundlei8n.getString("notApplicable")));

    };

}
