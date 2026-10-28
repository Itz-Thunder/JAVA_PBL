import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

final class UiStyle {

    static final Color NAVY = new Color(20, 37, 59);
    static final Color NAVY_LIGHT = new Color(31, 54, 81);
    static final Color TEAL = new Color(0, 153, 139);
    static final Color TEAL_DARK = new Color(0, 126, 114);
    static final Color BACKGROUND = new Color(244, 247, 251);
    static final Color WHITE = Color.WHITE;
    static final Color TEXT = new Color(34, 48, 66);
    static final Color MUTED = new Color(112, 128, 146);
    static final Color BORDER = new Color(222, 229, 237);
    static final Color DANGER = new Color(184, 55, 63);
    static final Color WARNING = new Color(166, 103, 15);
    static final Color SUCCESS = new Color(0, 126, 114);
    static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 16);
    static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    private UiStyle() {
    }

    static JPanel page() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        return panel;
    }

    static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(24, 26, 24, 26)));
        return panel;
    }

    static JPanel heading(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));
        titleLabel.setForeground(TEXT);
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(MUTED);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subtitleLabel, BorderLayout.SOUTH);
        return panel;
    }

    static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT);
        return label;
    }

    static FeedbackLabel feedbackLabel() {
        return new FeedbackLabel();
    }

    static void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setBackground(WHITE);
        field.setPreferredSize(new Dimension(260, 42));
        Border outline = BorderFactory.createLineBorder(BORDER);
        field.setBorder(BorderFactory.createCompoundBorder(
                outline, BorderFactory.createEmptyBorder(0, 12, 0, 12)));
    }

    static void styleCombo(javax.swing.JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setForeground(TEXT);
        combo.setBackground(WHITE);
        combo.setPreferredSize(new Dimension(260, 42));
        combo.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    static JButton button(String text, boolean primary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(primary ? WHITE : TEXT);
        button.setBackground(primary ? TEAL : WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary ? TEAL : BORDER),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)));
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        button.setMargin(new Insets(8, 14, 8, 14));
        return button;
    }

    static JPanel actionCard(String title, String description) {
        JPanel panel = card();
        panel.setLayout(new BorderLayout(0, 12));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(TEXT);
        JLabel descriptionLabel = new JLabel("<html>" + description + "</html>");
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionLabel.setForeground(MUTED);
        JLabel arrow = new JLabel("OPEN  ›");
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 11));
        arrow.setForeground(TEAL_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(descriptionLabel, BorderLayout.CENTER);
        panel.add(arrow, BorderLayout.SOUTH);
        panel.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        return panel;
    }

    static void styleTable(javax.swing.JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setBackground(new Color(239, 243, 248));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
    }

    static JLabel metric(String value, String caption, Color color) {
        JLabel label = new JLabel("<html><div style='text-align:center'>"
                + "<span style='font-size:25px;font-weight:bold;color:#"
                + hex(color) + "'>" + value + "</span><br>"
                + "<span style='font-size:11px;color:#718096'>" + caption
                + "</span></div></html>", JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setOpaque(true);
        label.setBackground(WHITE);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(16, 12, 16, 12)));
        return label;
    }

    static JPanel groupTile(String group, int units) {
        return groupTile(group, String.valueOf(units));
    }

    static JPanel groupTile(String group, String units) {
        JPanel tile = new JPanel(new BorderLayout(0, 12));
        tile.setBackground(WHITE);
        Color accent = MUTED;
        String availability = "units available";
        if (!"Loading".equals(units)) {
            try {
                int count = Integer.parseInt(units);
                accent = count == 0 ? DANGER : count <= 3 ? WARNING : TEAL_DARK;
                availability = count == 0 ? "Unavailable"
                        : count <= 3 ? "Low stock · " + count + " units"
                        : count + (count == 1 ? " unit available" : " units available");
            } catch (NumberFormatException exception) {
                availability = units;
            }
        }
        tile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createEmptyBorder(16, 19, 16, 19))));
        JLabel groupLabel = new JLabel(group);
        groupLabel.setFont(new Font("Segoe UI", Font.BOLD, 23));
        groupLabel.setForeground(accent);
        JLabel unitsLabel = new JLabel(availability);
        unitsLabel.setFont(BODY_FONT);
        unitsLabel.setForeground(accent);
        tile.add(groupLabel, BorderLayout.CENTER);
        tile.add(unitsLabel, BorderLayout.SOUTH);
        tile.setPreferredSize(new Dimension(150, 110));
        return tile;
    }

    static final class FeedbackLabel extends JLabel {
        private FeedbackLabel() {
            super(" ");
            setFont(BODY_FONT);
            setBorder(BorderFactory.createEmptyBorder(9, 11, 9, 11));
            setOpaque(true);
            setBackground(BACKGROUND);
            setForeground(MUTED);
            setVisible(false);
        }

        void setFeedback(String message, boolean success) {
            setText(message);
            setForeground(success ? SUCCESS : DANGER);
            setBackground(success ? new Color(232, 247, 242) : new Color(253, 238, 238));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(success
                            ? new Color(196, 231, 219) : new Color(241, 207, 207)),
                    BorderFactory.createEmptyBorder(9, 11, 9, 11)));
            setVisible(true);
        }

        void clearFeedback() {
            setText(" ");
            setVisible(false);
        }
    }

    private static String hex(Color color) {
        return String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

}
