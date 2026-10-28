import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class DonorFrame extends JPanel {

    private final JTextField nameField = new JTextField();
    private final JTextField ageField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField cityField = new JTextField();
    private final JComboBox<String> genderBox = new JComboBox<>(
            new String[]{"Female", "Male", "Other", "Prefer not to say"});
    private final JComboBox<String> bloodBox = new JComboBox<>(
            new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
    private final UiStyle.FeedbackLabel feedback = UiStyle.feedbackLabel();
    private final JButton saveButton = UiStyle.button("Save donor", true);
    private final JButton clearButton = UiStyle.button("Clear form", false);
    private final Runnable onSaved;

    public DonorFrame() {
        this(() -> {
        });
    }

    public DonorFrame(Runnable onSaved) {
        this.onSaved = onSaved;
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
        addField(form, c, "Full name", nameField, 120);
        addField(form, c, "Age", ageField, 10);
        addField(form, c, "Gender", genderBox, 30);
        addField(form, c, "Blood group", bloodBox, 3);
        addField(form, c, "Phone number", phoneField, 40);
        addField(form, c, "City", cityField, 120);

        JPanel actions = new JPanel(new BorderLayout(12, 0));
        actions.setOpaque(false);
        clearButton.addActionListener(event -> clearForm());
        saveButton.addActionListener(event -> saveDonor());
        cityField.addActionListener(event -> saveDonor());
        actions.add(clearButton, BorderLayout.WEST);
        actions.add(saveButton, BorderLayout.EAST);

        c.gridy++;
        c.insets = new Insets(0, 0, 10, 0);
        form.add(feedback, c);
        c.gridy++;
        c.insets = new Insets(0, 0, 0, 0);
        form.add(actions, c);

        JPanel help = UiStyle.card();
        help.setLayout(new BorderLayout(0, 12));
        JLabel helpTitle = new JLabel("Before you save");
        helpTitle.setFont(UiStyle.HEADING_FONT);
        helpTitle.setForeground(UiStyle.TEXT);
        JLabel helpCopy = new JLabel("<html><div style='width:180px'>"
                + "Check the blood group and contact details before saving. "
                + "Adding a donor increases the matching stock in this prototype."
                + "</div></html>");
        helpCopy.setFont(UiStyle.BODY_FONT);
        helpCopy.setForeground(UiStyle.MUTED);
        help.add(helpTitle, BorderLayout.NORTH);
        help.add(helpCopy, BorderLayout.CENTER);

        add(form, BorderLayout.CENTER);
        add(help, BorderLayout.EAST);
        setPreferredSize(new java.awt.Dimension(760, 580));
        setVisible(true);
    }

    private void addField(JPanel form, GridBagConstraints c, String label,
            java.awt.Component field, int maxLength) {
        c.insets = new Insets(0, 0, 6, 0);
        form.add(UiStyle.fieldLabel(label), c);
        c.gridy++;
        c.insets = new Insets(0, 0, 13, 0);
        if (field instanceof JTextField) {
            JTextField textField = (JTextField) field;
            UiStyle.styleField(textField);
            textField.setDocument(new javax.swing.text.PlainDocument() {
                @Override
                public void insertString(int offset, String text,
                        javax.swing.text.AttributeSet attributes)
                        throws javax.swing.text.BadLocationException {
                    if (text != null && getLength() + text.length() <= maxLength) {
                        super.insertString(offset, text, attributes);
                    }
                }
            });
        } else {
            UiStyle.styleCombo((JComboBox<?>) field);
        }
        form.add(field, c);
        c.gridy++;
    }

    private void saveDonor() {
        String name = nameField.getText().trim();
        String age = ageField.getText().trim();
        String phone = phoneField.getText().trim();
        String city = cityField.getText().trim();
        if (name.isEmpty()) {
            showError("Enter the donor's full name.", nameField);
            return;
        }
        int ageValue;
        try {
            ageValue = Integer.parseInt(age);
        } catch (NumberFormatException exception) {
            showError("Enter age as a whole number.", ageField);
            return;
        }
        if (ageValue < 1 || ageValue > 120) {
            showError("Enter an age between 1 and 120.", ageField);
            return;
        }
        String digits = phone.replaceAll("\\D", "");
        if (!phone.matches("[0-9+(). -]+") || digits.length() < 7 || digits.length() > 15) {
            showError("Enter a valid phone number with 7–15 digits.", phoneField);
            return;
        }
        if (city.isEmpty()) {
            showError("Enter a city.", cityField);
            return;
        }

        final Donor donor = new Donor(name, age, (String) genderBox.getSelectedItem(),
                (String) bloodBox.getSelectedItem(), phone, city);
        setBusy(true);
        feedback.setFeedback("Saving donor…", false);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                DataStore.addDonor(donor);
                return null;
            }

            @Override
            protected void done() {
                setBusy(false);
                try {
                    get();
                    clearForm();
                    feedback.setFeedback("Donor saved and inventory updated.", true);
                    onSaved.run();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    feedback.setFeedback("Save interrupted. Please try again.", false);
                } catch (java.util.concurrent.ExecutionException exception) {
                    Throwable cause = exception.getCause();
                    feedback.setFeedback(cause instanceof IllegalStateException
                            ? cause.getMessage() : "Could not save donor. Please try again.", false);
                }
            }
        }.execute();
    }

    private void showError(String message, JTextField field) {
        feedback.setFeedback(message, false);
        field.requestFocusInWindow();
    }

    private void setBusy(boolean busy) {
        saveButton.setEnabled(!busy);
        clearButton.setEnabled(!busy);
        nameField.setEnabled(!busy);
        ageField.setEnabled(!busy);
        phoneField.setEnabled(!busy);
        cityField.setEnabled(!busy);
        genderBox.setEnabled(!busy);
        bloodBox.setEnabled(!busy);
    }

    private void clearForm() {
        nameField.setText("");
        ageField.setText("");
        phoneField.setText("");
        cityField.setText("");
        genderBox.setSelectedIndex(0);
        bloodBox.setSelectedIndex(0);
        nameField.requestFocusInWindow();
    }
}
