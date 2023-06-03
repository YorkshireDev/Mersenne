package view;

import controller.ControllerMersenne;
import model.ModelFlowControl;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private ResourceBundle resourceBundlei8n;

    private ControllerMersenne controllerMersenne;

    private void init() {

        textFieldTimeToRun.setText("60");
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
                ModelFlowControl.latchViewMain.countDown();
                return;
            }

            buttonStartStop.setText(resourceBundleI8n.getString("stop"));

            ModelFlowControl.latchViewMain = new CountDownLatch(1);
            ModelFlowControl.latchControllerMersenne = new CountDownLatch(1);

            controllerMersenne = new ControllerMersenne();

            ExecutorService threadPool = Executors.newFixedThreadPool(3);

            threadPool.submit(resultListUpdateThread);
            threadPool.submit(timeRemainingUpdateThread);
            threadPool.submit(controllerMersenne);

        });

    }

    private final Runnable resultListUpdateThread = () -> {

        while (ModelFlowControl.latchControllerMersenne.getCount() > 0) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) { throw new RuntimeException(e); }
        }

    };

    private final Runnable timeRemainingUpdateThread = () -> {

        boolean runForever = checkBoxRunForever.isSelected();
        double timeRemaining = Double.parseDouble(textFieldTimeToRun.getText());

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
                if (timeRemainingInt <= 0) ModelFlowControl.latchViewMain.countDown();
            } catch (InterruptedException e) { throw new RuntimeException(e); }
        }

        SwingUtilities.invokeLater(() -> buttonStartStop.setText(resourceBundlei8n.getString("start")));
        SwingUtilities.invokeLater(() -> labelTimeRemaining.setText(resourceBundlei8n.getString("notApplicable")));

    };

}
