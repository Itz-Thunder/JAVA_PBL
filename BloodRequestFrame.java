import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class BloodRequestFrame extends JPanel {

    private final JTextField patientField = new JTextField();
    private final JTextField hospitalField = new JTextField();
    private final JComboBox<String> bloodBox = new JComboBox<>(
            new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
    private final JLabel availability = new JLabel("Checking selected group…");
    private final UiStyle.FeedbackLabel feedback = UiStyle.feedbackLabel();
    private final JButton issueButton = UiStyle.button("Review issue", true);
    private final Runnable onIssued;
    private boolean issuing;

    public BloodRequestFrame() {
        this(() -> {
        });
    }

    public BloodRequestFrame(Runnable onIssued) {
        this.onIssued = onIssued;
        setOpaque(false);
        setLayout(new BorderLayout(18, 0));

        JPanel form = UiStyle.card();
        form.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        addField(form, c, "Patient name", patientField, 120);
        addField(form, c, "Hospital", hospitalField, 160);

        c.insets = new Insets(0, 0, 6, 0);
        form.add(UiStyle.fieldLabel("Blood group"), c);
        c.gridy++;
        c.insets = new Insets(0, 0, 10, 0);
        UiStyle.styleCombo(bloodBox);
        form.add(bloodBox, c);
        c.gridy++;
        c.insets = new Insets(0, 0, 22, 0);
        availability.setFont(UiStyle.BODY_FONT);
        availability.setForeground(UiStyle.MUTED);
        form.add(availability, c);

        issueButton.addActionListener(event -> reviewIssue());
        c.gridy++;
        c.insets = new Insets(0, 0, 10, 0);
        form.add(issueButton, c);
        c.gridy++;
        c.insets = new Insets(0, 0, 0, 0);
        form.add(feedback, c);

        JPanel help = UiStyle.card();
        help.setLayout(new BorderLayout(0, 12));
        JLabel helpTitle = new JLabel("Request details");
        helpTitle.setFont(UiStyle.HEADING_FONT);
        helpTitle.setForeground(UiStyle.TEXT);
        JLabel helpCopy = new JLabel("<html><div style='width:185px'>"
                + "Review the selected blood group and its available units before confirming. "
                + "Each successful issue records one unit."
                + "</div></html>");
        helpCopy.setFont(UiStyle.BODY_FONT);
        helpCopy.setForeground(UiStyle.MUTED);
        help.add(helpTitle, BorderLayout.NORTH);
        help.add(helpCopy, BorderLayout.CENTER);

        add(form, BorderLayout.CENTER);
        add(help, BorderLayout.EAST);
        bloodBox.addActionListener(event -> loadAvailability(false));
        hospitalField.addActionListener(event -> reviewIssue());
        setPreferredSize(new java.awt.Dimension(760, 480));
        loadAvailability(false);
    }

    private void addField(JPanel form, GridBagConstraints c, String label,
            JTextField field, int maxLength) {
        c.insets = new Insets(0, 0, 6, 0);
        form.add(UiStyle.fieldLabel(label), c);
        c.gridy++;
        c.insets = new Insets(0, 0, 16, 0);
        UiStyle.styleField(field);
        field.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offset, String text,
                    javax.swing.text.AttributeSet attributes)
                    throws javax.swing.text.BadLocationException {
                if (text != null && getLength() + text.length() <= maxLength) {
                    super.insertString(offset, text, attributes);
                }
            }
        });
        form.add(field, c);
        c.gridy++;
    }

    private void loadAvailability(boolean preserveFeedback) {
        final String group = (String) bloodBox.getSelectedItem();
        issueButton.setEnabled(false);
        availability.setText("Checking " + group + " availability…");
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() {
                return DataStore.getStock(group);
            }

            @Override
            protected void done() {
                if (!group.equals(bloodBox.getSelectedItem())) {
                    return;
                }
                try {
                    int units = get();
                    availability.setText(group + ": " + units + " "
                            + (units == 1 ? "unit" : "units") + " available");
                    availability.setForeground(units == 0
                            ? UiStyle.DANGER : units <= 3 ? UiStyle.WARNING : UiStyle.SUCCESS);
                    issueButton.setEnabled(units > 0 && !issuing);
                    if (units == 0) {
                        feedback.setFeedback("This group is currently unavailable.", false);
                    } else if (!preserveFeedback) {
                        feedback.clearFeedback();
                    }
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    availability.setText("Availability check interrupted.");
                    feedback.setFeedback("Please retry the availability check.", false);
                } catch (java.util.concurrent.ExecutionException exception) {
                    availability.setText("Availability could not be checked.");
                    feedback.setFeedback("Could not load stock. Check the database connection.",
                            false);
                }
            }
        }.execute();
    }

    private void reviewIssue() {
        final String patient = patientField.getText().trim();
        final String hospital = hospitalField.getText().trim();
        final String group = (String) bloodBox.getSelectedItem();
        if (patient.isEmpty()) {
            feedback.setFeedback("Enter the patient's name.", false);
            patientField.requestFocusInWindow();
            return;
        }
        if (hospital.isEmpty()) {
            feedback.setFeedback("Enter the hospital name.", false);
            hospitalField.requestFocusInWindow();
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this,
                "Issue 1 unit of " + group + " blood to " + patient
                + " at " + hospital + "?\n\nThis will reduce available stock by one unit.",
                "Review blood issue", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        issuing = true;
        issueButton.setEnabled(false);
        patientField.setEnabled(false);
        hospitalField.setEnabled(false);
        bloodBox.setEnabled(false);
        feedback.setFeedback("Recording request…", false);
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return DataStore.issueBlood(group, patient, hospital);
            }

            @Override
            protected void done() {
                issuing = false;
                patientField.setEnabled(true);
                hospitalField.setEnabled(true);
                bloodBox.setEnabled(true);
                try {
                    if (get()) {
                        patientField.setText("");
                        hospitalField.setText("");
                        feedback.setFeedback("Issue recorded. One " + group
                                + " unit was deducted from stock.", true);
                        onIssued.run();
                        loadAvailability(true);
                    } else {
                        feedback.setFeedback("Stock changed before the issue completed. "
                                + "Review availability and try again.", false);
                        loadAvailability(true);
                    }
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    feedback.setFeedback("Request interrupted. Check inventory before retrying.",
                            false);
                    loadAvailability(true);
                } catch (java.util.concurrent.ExecutionException exception) {
                    Throwable cause = exception.getCause();
                    feedback.setFeedback(cause instanceof IllegalStateException
                            ? cause.getMessage() : "Could not record the request. Please retry.",
                            false);
                    loadAvailability(true);
                }
            }
        }.execute();
    }
}
