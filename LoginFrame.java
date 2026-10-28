import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends JFrame {

    JTextField usernameField;
    JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Smart Blood Bank | Sign in");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 500));
        setSize(900, 580);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiStyle.BACKGROUND);

        JPanel brand = new JPanel(new GridBagLayout());
        brand.setBackground(UiStyle.NAVY);
        brand.setPreferredSize(new Dimension(350, 0));
        GridBagConstraints brandConstraints = new GridBagConstraints();
        brandConstraints.gridx = 0;
        brandConstraints.gridy = 0;
        brandConstraints.weightx = 1;
        brandConstraints.anchor = GridBagConstraints.WEST;
        brandConstraints.insets = new Insets(0, 42, 12, 36);
        JLabel brandName = new JLabel("LIFELINE");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        brandName.setForeground(new Color(115, 222, 208));
        brand.add(brandName, brandConstraints);

        brandConstraints.gridy++;
        brandConstraints.insets = new Insets(0, 42, 14, 36);
        JLabel brandTitle = new JLabel("<html>Blood,<br>when it<br>matters.</html>");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 38));
        brandTitle.setForeground(Color.WHITE);
        brand.add(brandTitle, brandConstraints);

        brandConstraints.gridy++;
        brandConstraints.insets = new Insets(0, 42, 0, 36);
        JLabel brandCopy = new JLabel("<html>A simpler way to manage donors,<br>"
                + "inventory and blood requests.</html>");
        brandCopy.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        brandCopy.setForeground(new Color(190, 203, 218));
        brand.add(brandCopy, brandConstraints);

        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        JPanel form = UiStyle.card();
        form.setLayout(new GridBagLayout());
        form.setPreferredSize(new Dimension(390, 370));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 7, 0);
        JLabel title = new JLabel("Welcome back");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(UiStyle.TEXT);
        form.add(title, c);

        c.gridy++;
        c.insets = new Insets(0, 0, 24, 0);
        JLabel subtitle = new JLabel("Sign in to continue to your workspace.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(UiStyle.MUTED);
        form.add(subtitle, c);

        c.gridy++;
        c.insets = new Insets(0, 0, 18, 0);
        JLabel demoAccess = new JLabel("Demo access:  admin  /  1234");
        demoAccess.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        demoAccess.setForeground(UiStyle.TEAL_DARK);
        form.add(demoAccess, c);

        c.gridy++;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 8, 0);
        form.add(UiStyle.fieldLabel("Username"), c);
        c.gridy++;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 18, 0);
        usernameField = new JTextField();
        UiStyle.styleField(usernameField);
        form.add(usernameField, c);

        c.gridy++;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 8, 0);
        form.add(UiStyle.fieldLabel("Password"), c);
        c.gridy++;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 22, 0);
        passwordField = new JPasswordField();
        UiStyle.styleField(passwordField);
        form.add(passwordField, c);

        c.gridy++;
        JButton loginButton = UiStyle.button("Sign in", true);
        form.add(loginButton, c);
        loginButton.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());
        getRootPane().setDefaultButton(loginButton);

        GridBagConstraints rightConstraints = new GridBagConstraints();
        rightConstraints.insets = new Insets(30, 30, 30, 30);
        right.add(form, rightConstraints);

        root.add(brand, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        add(root);
        setVisible(true);
        usernameField.requestFocusInWindow();
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.equals("admin") && password.equals("1234")) {
            dispose();
            new DashboardFrame();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "That username or password doesn't match. Please try again.",
                    "Sign in failed",
                    JOptionPane.WARNING_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocusInWindow();
        }
    }
}
