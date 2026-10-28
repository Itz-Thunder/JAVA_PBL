import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class BloodStockFrame extends JPanel {

    private final JPanel grid = new JPanel(new GridLayout(2, 4, 12, 12));
    private final UiStyle.FeedbackLabel feedback = UiStyle.feedbackLabel();
    private final JButton refreshButton = UiStyle.button("Refresh stock", false);

    public BloodStockFrame() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        grid.setOpaque(false);

        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);
        JLabel legend = new JLabel("Zero units: unavailable   •   1–3 units: low stock");
        legend.setFont(UiStyle.BODY_FONT);
        legend.setForeground(UiStyle.MUTED);
        toolbar.add(legend, BorderLayout.CENTER);
        toolbar.add(refreshButton, BorderLayout.EAST);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        content.add(toolbar, BorderLayout.NORTH);
        content.add(grid, BorderLayout.CENTER);
        content.add(feedback, BorderLayout.SOUTH);
        add(content, BorderLayout.CENTER);
        refreshButton.addActionListener(event -> loadStock());
        setPreferredSize(new java.awt.Dimension(760, 500));
        loadStock();
    }

    private void loadStock() {
        refreshButton.setEnabled(false);
        feedback.setFeedback("Loading current inventory…", false);
        grid.removeAll();
        grid.add(new JLabel("Loading inventory…"));
        new SwingWorker<Map<String, Integer>, Void>() {
            @Override
            protected Map<String, Integer> doInBackground() {
                return DataStore.getStockLevels();
            }

            @Override
            protected void done() {
                refreshButton.setEnabled(true);
                grid.removeAll();
                try {
                    Map<String, Integer> levels = get();
                    grid.removeAll();
                    for (String group : new String[]{"A+", "A-", "B+", "B-",
                            "AB+", "AB-", "O+", "O-"}) {
                        Integer units = levels.get(group);
                        if (units == null) {
                            throw new IllegalStateException("Missing inventory row for " + group + ".");
                        }
                        grid.add(UiStyle.groupTile(group, units));
                    }
                    feedback.setFeedback("Inventory updated just now.", true);
                    grid.revalidate();
                    grid.repaint();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    grid.add(new JLabel("Refresh interrupted."));
                    feedback.setFeedback("Inventory refresh interrupted.", false);
                } catch (java.util.concurrent.ExecutionException | IllegalStateException exception) {
                    Throwable cause = exception instanceof java.util.concurrent.ExecutionException
                            ? exception.getCause() : exception;
                    grid.add(new JLabel("Inventory unavailable."));
                    feedback.setFeedback(cause instanceof IllegalStateException
                            ? cause.getMessage() : "Could not load inventory. Check the database and retry.",
                            false);
                }
                grid.revalidate();
                grid.repaint();
            }
        }.execute();
    }

    void refreshStock() {
        loadStock();
    }
}
