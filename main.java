import java.awt.BorderLayout;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(main::initializeApplication);
    }

    private static void initializeApplication() {
        JFrame startup = new JFrame("Lifeline | Connecting");
        startup.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        startup.setSize(380, 150);
        startup.setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(UiStyle.BACKGROUND);
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(28, 30, 28, 30));
        JLabel title = new JLabel("Preparing your workspace", SwingConstants.CENTER);
        title.setFont(UiStyle.HEADING_FONT);
        title.setForeground(UiStyle.TEXT);
        JLabel message = new JLabel("Connecting to the blood bank database…", SwingConstants.CENTER);
        message.setFont(UiStyle.BODY_FONT);
        message.setForeground(UiStyle.MUTED);
        content.add(title, BorderLayout.NORTH);
        content.add(message, BorderLayout.CENTER);
        startup.add(content);
        startup.setVisible(true);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws SQLException {
                DataStore.initialize();
                return null;
            }

            @Override
            protected void done() {
                startup.dispose();
                try {
                    get();
                    new LoginFrame();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    showStartupError(exception);
                } catch (java.util.concurrent.ExecutionException exception) {
                    showStartupError(exception.getCause());
                }
            }
        }.execute();
    }

    private static void showStartupError(Throwable exception) {
        String detail = exception == null ? "No additional error details." : exception.getMessage();
        JOptionPane.showMessageDialog(
                null,
                "Could not initialize the blood bank database. Check that MySQL is running, "
                + "the blood_bank database exists, and your connection settings are correct.\n\n"
                + detail,
                "Database Connection Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
