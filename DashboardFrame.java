import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DashboardFrame extends JFrame {

    private final CardLayout pageLayout = new CardLayout();
    private final JPanel pageHost = new JPanel(pageLayout);
    private final JLabel pageTitle = new JLabel();
    private final JLabel pageSubtitle = new JLabel();
    private final JLabel donorCount = UiStyle.metric("0", "REGISTERED DONORS", UiStyle.TEAL_DARK);
    private final JPanel stockPreview = new JPanel(new GridLayout(1, 4, 12, 0));
    private DefaultTableModel donorModel;
    private final BloodStockFrame inventoryPage = new BloodStockFrame();
    private final List<JButton> navigationButtons = new ArrayList<>();
    private final String[] pageIds = {"overview", "donors", "inventory", "requests"};
    private final String[] pageTitles = {
        "Overview", "Donor directory", "Blood inventory", "Issue blood"
    };
    private final String[] pageSubtitles = {
        "Your blood bank at a glance.", "Search and review registered donor records.",
        "Available units by blood group.", "Record a hospital request against available stock."
    };

    public DashboardFrame() {
        setTitle("Lifeline | Blood bank");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(920, 640));
        setSize(1180, 780);
        setLocationRelativeTo(null);

        pageHost.setOpaque(false);
        pageHost.add(createOverview(), "overview");
        pageHost.add(createDonorDirectory(), "donors");
        pageHost.add(new DonorFrame(this::refreshDashboard), "donorForm");
        pageHost.add(inventoryPage, "inventory");
        pageHost.add(new BloodRequestFrame(this::refreshDashboard), "requests");

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiStyle.BACKGROUND);
        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createWorkspace(), BorderLayout.CENTER);
        add(root);
        refreshDonorCount();
        showPage("overview");
        loadDashboardStock();
        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 30));
        sidebar.setBackground(UiStyle.NAVY);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(28, 18, 22, 18));

        JPanel brand = new JPanel(new BorderLayout(0, 5));
        brand.setOpaque(false);
        JLabel name = new JLabel("LIFELINE");
        name.setFont(new Font("Segoe UI", Font.BOLD, 20));
        name.setForeground(Color.WHITE);
        JLabel caption = new JLabel("BLOOD BANK WORKSPACE");
        caption.setFont(new Font("Segoe UI", Font.BOLD, 9));
        caption.setForeground(new Color(155, 176, 197));
        brand.add(name, BorderLayout.NORTH);
        brand.add(caption, BorderLayout.SOUTH);

        JPanel nav = new JPanel(new GridLayout(0, 1, 0, 8));
        nav.setOpaque(false);
        nav.add(navItem("Overview", "overview"));
        nav.add(navItem("Donor directory", "donors"));
        nav.add(navItem("Blood inventory", "inventory"));
        nav.add(navItem("Issue blood", "requests"));

        JPanel footer = new JPanel(new BorderLayout(0, 14));
        footer.setOpaque(false);
        JLabel status = new JLabel("<html><b>Workspace ready</b><br>"
                + "<span style='color:#b9c8d8'>Database initialized</span></html>");
        status.setForeground(Color.WHITE);
        status.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JButton logout = UiStyle.button("Log out", false);
        logout.setHorizontalAlignment(SwingConstants.LEFT);
        logout.addActionListener(event -> {
            dispose();
            new LoginFrame();
        });
        footer.add(status, BorderLayout.NORTH);
        footer.add(logout, BorderLayout.SOUTH);

        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(nav, BorderLayout.CENTER);
        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton navItem(String text, String pageId) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(new Color(190, 203, 218));
        button.setBackground(UiStyle.NAVY);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 10));
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        button.addActionListener(event -> showPage(pageId));
        navigationButtons.add(button);
        return button;
    }

    private JPanel createWorkspace() {
        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setOpaque(false);

        JPanel topbar = new JPanel(new BorderLayout(16, 0));
        topbar.setBackground(Color.WHITE);
        topbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.BORDER),
                BorderFactory.createEmptyBorder(18, 30, 18, 30)));
        JPanel titleArea = new JPanel(new BorderLayout(0, 4));
        titleArea.setOpaque(false);
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 21));
        pageTitle.setForeground(UiStyle.TEXT);
        pageSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pageSubtitle.setForeground(UiStyle.MUTED);
        titleArea.add(pageTitle, BorderLayout.NORTH);
        titleArea.add(pageSubtitle, BorderLayout.SOUTH);
        JButton addDonor = UiStyle.button("+  Add donor", true);
        addDonor.addActionListener(event -> showPage("donorForm"));
        topbar.add(titleArea, BorderLayout.CENTER);
        topbar.add(addDonor, BorderLayout.EAST);

        JPanel content = UiStyle.page();
        content.add(pageHost, BorderLayout.CENTER);
        workspace.add(topbar, BorderLayout.NORTH);
        workspace.add(content, BorderLayout.CENTER);
        return workspace;
    }

    private JPanel createOverview() {
        JPanel overview = new JPanel(new BorderLayout(0, 20));
        overview.setOpaque(false);

        JPanel banner = new JPanel(new BorderLayout(18, 0));
        banner.setBackground(UiStyle.NAVY_LIGHT);
        banner.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel bannerText = new JLabel("<html><span style='font-size:18px;font-weight:bold'>"
                + "Care starts with being ready.</span><br>"
                + "<span style='font-size:12px;color:#d2deea'>"
                + "Manage your donor records and keep an eye on available stock.</span></html>");
        bannerText.setForeground(Color.WHITE);
        banner.add(bannerText, BorderLayout.CENTER);

        JPanel sections = new JPanel(new BorderLayout(0, 10));
        sections.setOpaque(false);
        JPanel metrics = new JPanel(new GridLayout(1, 2, 12, 0));
        metrics.setOpaque(false);
        metrics.add(donorCount);
        metrics.add(UiStyle.metric("8", "SUPPORTED BLOOD GROUPS", UiStyle.NAVY_LIGHT));

        JPanel stockSection = new JPanel(new BorderLayout(0, 10));
        stockSection.setOpaque(false);
        JPanel sectionHeader = new JPanel(new BorderLayout());
        sectionHeader.setOpaque(false);
        JLabel stockTitle = new JLabel("Stock at a glance");
        stockTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        stockTitle.setForeground(UiStyle.TEXT);
        JButton viewInventory = UiStyle.button("View all groups", false);
        viewInventory.addActionListener(event -> showPage("inventory"));
        sectionHeader.add(stockTitle, BorderLayout.WEST);
        sectionHeader.add(viewInventory, BorderLayout.EAST);
        stockPreview.setOpaque(false);
        stockPreview.add(UiStyle.groupTile("A+", "Loading"));
        stockPreview.add(UiStyle.groupTile("B+", "Loading"));
        stockPreview.add(UiStyle.groupTile("O+", "Loading"));
        stockPreview.add(UiStyle.groupTile("AB+", "Loading"));
        stockSection.add(sectionHeader, BorderLayout.NORTH);
        stockSection.add(stockPreview, BorderLayout.CENTER);

        JPanel quickActions = new JPanel(new GridLayout(1, 3, 12, 0));
        quickActions.setOpaque(false);
        quickActions.add(actionButton("Register donor", "Add a donor and record their details.",
                () -> showPage("donorForm")));
        quickActions.add(actionButton("Review donors", "Search the existing donor directory.",
                () -> showPage("donors")));
        quickActions.add(actionButton("Issue blood", "Check availability and record a request.",
                () -> showPage("requests")));
        sections.add(metrics, BorderLayout.NORTH);
        sections.add(stockSection, BorderLayout.CENTER);
        sections.add(quickActions, BorderLayout.SOUTH);

        overview.add(banner, BorderLayout.NORTH);
        overview.add(sections, BorderLayout.CENTER);
        return overview;
    }

    private JButton actionButton(String title, String description, Runnable action) {
        JButton button = new JButton("<html><div style='text-align:left'>"
                + "<b style='font-size:14px'>" + title + "</b><br>"
                + "<span style='font-size:11px;color:#718096'>" + description
                + "</span><br><br><span style='font-size:10px;color:#007e72'>OPEN  &rsaquo;</span>"
                + "</div></html>");
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(UiStyle.TEXT);
        button.setBackground(Color.WHITE);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiStyle.BORDER),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        button.addActionListener(event -> action.run());
        return button;
    }

    private JPanel createDonorDirectory() {
        JPanel directory = new JPanel(new BorderLayout(0, 14));
        directory.setOpaque(false);

        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);
        JTextField search = new JTextField();
        search.setToolTipText("Search by name, blood group, phone or city");
        search.putClientProperty("JTextField.placeholderText", "Search name, blood group, phone or city");
        UiStyle.styleField(search);
        JButton register = UiStyle.button("+  Register donor", true);
        register.addActionListener(event -> showPage("donorForm"));
        toolbar.add(search, BorderLayout.CENTER);
        toolbar.add(register, BorderLayout.EAST);

        String[] columns = {"Name", "Age", "Gender", "Blood group", "Phone", "City"};
        donorModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(donorModel);
        UiStyle.styleTable(table);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(donorModel);
        table.setRowSorter(sorter);
        search.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                String value = search.getText().trim();
                sorter.setRowFilter(value.isEmpty() ? null : RowFilter.regexFilter(
                        "(?i)" + java.util.regex.Pattern.quote(value)));
            }

            @Override
            public void insertUpdate(DocumentEvent event) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                filter();
            }
        });
        JPanel searchPanel = new JPanel(new BorderLayout(0, 5));
        searchPanel.setOpaque(false);
        searchPanel.add(UiStyle.fieldLabel("Search donors"), BorderLayout.NORTH);
        searchPanel.add(search, BorderLayout.CENTER);
        toolbar.add(searchPanel, BorderLayout.CENTER);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(UiStyle.BORDER));
        directory.add(toolbar, BorderLayout.NORTH);
        directory.add(tableScroll, BorderLayout.CENTER);
        refreshDonorTable(donorModel);
        return directory;
    }

    private void refreshDonorTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Donor donor : DataStore.donors) {
            model.addRow(new Object[]{
                donor.name, donor.age, donor.gender, donor.bloodGroup, donor.phone, donor.city
            });
        }
    }

    private void refreshDonorCount() {
        donorCount.setText("<html><div style='text-align:center'>"
                + "<span style='font-size:25px;font-weight:bold;color:#"
                + String.format("%02x%02x%02x", UiStyle.TEAL_DARK.getRed(),
                        UiStyle.TEAL_DARK.getGreen(), UiStyle.TEAL_DARK.getBlue())
                + "'>" + DataStore.donors.size() + "</span><br>"
                + "<span style='font-size:11px;color:#718096'>REGISTERED DONORS</span>"
                + "</div></html>");
    }

    private void loadDashboardStock() {
        new javax.swing.SwingWorker<java.util.Map<String, Integer>, Void>() {
            @Override
            protected java.util.Map<String, Integer> doInBackground() {
                return DataStore.getStockLevels();
            }

            @Override
            protected void done() {
                try {
                    java.util.Map<String, Integer> levels = get();
                    stockPreview.removeAll();
                    for (String group : new String[]{"A+", "B+", "O+", "AB+"}) {
                        stockPreview.add(UiStyle.groupTile(group,
                                String.valueOf(levels.get(group))));
                    }
                    stockPreview.revalidate();
                    stockPreview.repaint();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    stockPreview.removeAll();
                    stockPreview.add(new JLabel("Stock update interrupted."));
                    stockPreview.revalidate();
                    stockPreview.repaint();
                } catch (java.util.concurrent.ExecutionException exception) {
                    stockPreview.removeAll();
                    stockPreview.add(new JLabel("Could not load stock. Open inventory to retry."));
                    stockPreview.revalidate();
                    stockPreview.repaint();
                }
            }
        }.execute();
    }

    private void refreshDashboard() {
        refreshDonorCount();
        if (donorModel != null) {
            refreshDonorTable(donorModel);
        }
        loadDashboardStock();
    }

    private void showPage(String pageId) {
        pageLayout.show(pageHost, pageId);
        String navigationPageId = "donorForm".equals(pageId) ? "donors" : pageId;
        int selected = 0;
        for (int i = 0; i < pageIds.length; i++) {
            if (pageIds[i].equals(navigationPageId)) {
                selected = i;
                break;
            }
        }
        pageTitle.setText("donorForm".equals(pageId) ? "Register a donor" : pageTitles[selected]);
        pageSubtitle.setText("donorForm".equals(pageId)
                ? "Capture donor details and update the matching stock group."
                : pageSubtitles[selected]);
        for (int i = 0; i < navigationButtons.size(); i++) {
            boolean active = i == selected;
            JButton button = navigationButtons.get(i);
            button.setForeground(active ? Color.WHITE : new Color(190, 203, 218));
            button.setBackground(active ? UiStyle.NAVY_LIGHT : UiStyle.NAVY);
        }
        if ("donors".equals(pageId)) {
            refreshDonorCount();
        } else if ("inventory".equals(pageId)) {
            inventoryPage.refreshStock();
        }
    }
}
