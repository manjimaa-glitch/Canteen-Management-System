import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
public class CanteenGUI extends JFrame {

    // App colours
    private final Color background = new Color(247, 248, 252);
    private final Color navy = new Color(29, 41, 81);
    private final Color purple = new Color(105, 85, 220);
    private final Color textColor = new Color(45, 50, 70);
    private void showAdminPanel() {

   String[] options = {
    "View All Food Items",
    "Add Food Item",
    "Update Food Item",
    "Delete Food Item",
    "Change Availability",
    "View Order History",
    "View Order Details",
    "View Sales Summary",
    "Back"
};

    while (true) {

        int choice = JOptionPane.showOptionDialog(
            this,
            "Manage your canteen menu",
            "Admin Panel",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );

        switch (choice) {

    case 0:
        showFoodMenu();
        break;

    case 1:
        addFoodItemGUI();
        break;

    case 2:
        updateFoodItemGUI();
        break;

    case 3:
        deleteFoodItemGUI();
        break;

    case 4:
        changeAvailabilityGUI();
        break;

    case 5:
    showOrderHistory();
    break;

case 6:
    showOrderDetails();
    break;

case 7:
    showSalesSummary();
    break;

default:
    return;

}
    }
}
    private void addFoodItemGUI() {

    JTextField nameField = new JTextField();
    JTextField priceField = new JTextField();

    Object[] fields = {
        "Food Name:", nameField,
        "Price (₹):", priceField
    };

    int result = JOptionPane.showConfirmDialog(
        this, fields, "Add Food Item",
        JOptionPane.OK_CANCEL_OPTION
    );

    if (result != JOptionPane.OK_OPTION) {
        return;
    }

    String name = nameField.getText().trim();

    if (name.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Enter a food name.");
        return;
    }

    try {
        double price = Double.parseDouble(priceField.getText().trim());

        if (!Double.isFinite(price) || price < 0) {
            JOptionPane.showMessageDialog(
                this, "Enter a valid, non-negative price."
            );
            return;
        }

        FoodDAO.addFoodItem(name, price);
        JOptionPane.showMessageDialog(
            this, "Request processed. Check the menu to verify the result."
        );

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Enter a valid price.");
    }
}
    private void updateFoodItemGUI() {

    ArrayList<FoodItem> items = FoodDAO.getAllFoodItems();

    if (items.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No food items found.");
        return;
    }

    String[] choices = new String[items.size()];

    for (int i = 0; i < items.size(); i++) {
        FoodItem item = items.get(i);
        choices[i] = item.id + " - " + item.name;
    }

    String selected = (String) JOptionPane.showInputDialog(
        this,
        "Choose a food item:",
        "Update Food Item",
        JOptionPane.PLAIN_MESSAGE,
        null,
        choices,
        choices[0]
    );

    if (selected == null) {
        return;
    }

    int id = Integer.parseInt(selected.split(" - ")[0]);

    FoodItem selectedItem = null;

    for (FoodItem item : items) {
        if (item.id == id) {
            selectedItem = item;
            break;
        }
    }

    if (selectedItem == null) {
        return;
    }

    JTextField nameField = new JTextField(selectedItem.name);
    JTextField priceField = new JTextField(
        String.valueOf(selectedItem.price)
    );

    Object[] fields = {
        "Food Name:", nameField,
        "Price (₹):", priceField
    };

    int result = JOptionPane.showConfirmDialog(
        this, fields, "Update Food Item",
        JOptionPane.OK_CANCEL_OPTION
    );

    if (result != JOptionPane.OK_OPTION) {
        return;
    }

    String name = nameField.getText().trim();

    if (name.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Enter a food name.");
        return;
    }

    try {
        double price = Double.parseDouble(priceField.getText().trim());

        if (!Double.isFinite(price) || price < 0) {
            JOptionPane.showMessageDialog(
                this, "Enter a valid, non-negative price."
            );
            return;
        }

        FoodDAO.updateFoodItem(id, name, price);

        JOptionPane.showMessageDialog(
            this, "Request processed. Check the menu to verify the result."
        );

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Enter a valid price.");
    }
}
    private void deleteFoodItemGUI() {

    ArrayList<FoodItem> items = FoodDAO.getAllFoodItems();

    if (items.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No food items found.");
        return;
    }

    String[] choices = new String[items.size()];

    for (int i = 0; i < items.size(); i++) {
        FoodItem item = items.get(i);
        choices[i] = item.id + " - " + item.name;
    }

    String selected = (String) JOptionPane.showInputDialog(
        this,
        "Choose a food item to delete:",
        "Delete Food Item",
        JOptionPane.WARNING_MESSAGE,
        null,
        choices,
        choices[0]
    );

    if (selected == null) {
        return;
    }

    int id = Integer.parseInt(selected.split(" - ")[0]);

    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to delete " + selected + "?",
        "Confirm Deletion",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        FoodDAO.deleteFoodItem(id);

        JOptionPane.showMessageDialog(
            this, "Request processed. Check the menu to verify the result."
        );
    }
}
private void changeAvailabilityGUI() {

    ArrayList<FoodItem> items = FoodDAO.getAllFoodItems();

    if (items.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No food items found.");
        return;
    }

    String[] choices = new String[items.size()];

    for (int i = 0; i < items.size(); i++) {
        FoodItem item = items.get(i);

        choices[i] = item.id + " - " + item.name
            + " (" + (item.available ? "Available" : "Unavailable") + ")";
    }

    String selected = (String) JOptionPane.showInputDialog(
        this,
        "Choose a food item:",
        "Change Availability",
        JOptionPane.PLAIN_MESSAGE,
        null,
        choices,
        choices[0]
    );

    if (selected == null) {
        return;
    }

    int id = Integer.parseInt(selected.split(" - ")[0]);

    FoodItem selectedItem = null;

    for (FoodItem item : items) {
        if (item.id == id) {
            selectedItem = item;
            break;
        }
    }

    if (selectedItem == null) {
        return;
    }

    Object[] options = {"Available", "Unavailable", "Cancel"};

    int choice = JOptionPane.showOptionDialog(
        this,
        "Choose availability for " + selectedItem.name,
        "Change Availability",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[0]
    );

    if (choice == 0) {
        FoodDAO.changeAvailability(id, true);
    } else if (choice == 1) {
        FoodDAO.changeAvailability(id, false);
    } else {
        return;
    }

    JOptionPane.showMessageDialog(
        this, "Request processed. Check the menu to verify the result."
    );
}
private void showOrderHistory() {

    String history = OrderDAO.getOrderHistory();

    JTextArea textArea = new JTextArea(history, 15, 50);
    textArea.setEditable(false);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

    JScrollPane scrollPane = new JScrollPane(textArea);

    JOptionPane.showMessageDialog(
        this,
        scrollPane,
        "Order History",
        JOptionPane.INFORMATION_MESSAGE
    );
} 
private void showOrderDetails() {

    ArrayList<String> orderIds = new ArrayList<>();

    String sql = "SELECT order_id FROM orders ORDER BY order_id DESC";

    try (java.sql.Connection connection = DBConnection.getConnection();
         java.sql.PreparedStatement statement =
             connection.prepareStatement(sql);
         java.sql.ResultSet result = statement.executeQuery()) {

        while (result.next()) {
            orderIds.add(String.valueOf(result.getInt("order_id")));
        }

    } catch (java.sql.SQLException e) {
        JOptionPane.showMessageDialog(
            this,
            "Could not load orders.\n" + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    if (orderIds.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No orders found yet.");
        return;
    }

    String selected = (String) JOptionPane.showInputDialog(
        this,
        "Select an order to view its details:",
        "Order Details",
        JOptionPane.PLAIN_MESSAGE,
        null,
        orderIds.toArray(),
        orderIds.get(0)
    );

    if (selected == null) {
        return;
    }

    String details = OrderDAO.getOrderDetails(
        Integer.parseInt(selected)
    );

    JTextArea textArea = new JTextArea(details, 15, 55);
    textArea.setEditable(false);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

    JOptionPane.showMessageDialog(
        this,
        new JScrollPane(textArea),
        "Order #" + selected + " — Details",
        JOptionPane.INFORMATION_MESSAGE
    );
}  
private void showOrderScreen() {

    ArrayList<FoodItem> foodItems = FoodDAO.getAllFoodItems();

    ArrayList<FoodItem> availableItems = new ArrayList<>();

    for (FoodItem item : foodItems) {
        if (item.available) {
            availableItems.add(item);
        }
    }

    if (availableItems.isEmpty()) {
        JOptionPane.showMessageDialog(
            this,
            "No food items are currently available."
        );
        return;
    }

    String[] columns = {
        "Food ID", "Food Name", "Price (₹)", "Quantity"
    };

    DefaultTableModel model = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 3;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            if (column == 0) return Integer.class;
            if (column == 2) return Double.class;
            return String.class;
        }
    };

    for (FoodItem item : availableItems) {
        model.addRow(new Object[]{
            item.id,
            item.name,
            item.price,
            0
        });
    }

    JTable table = new JTable(model);
    table.setRowHeight(30);
    table.setFont(new Font("SansSerif", Font.PLAIN, 14));

    table.getColumnModel().getColumn(0).setPreferredWidth(65);
    table.getColumnModel().getColumn(1).setPreferredWidth(180);
    table.getColumnModel().getColumn(2).setPreferredWidth(100);
    table.getColumnModel().getColumn(3).setPreferredWidth(90);

    JLabel totalLabel = new JLabel("Total: ₹0.00");
    totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

    Runnable updateTotal = () -> {
        double total = 0;

        for (int row = 0; row < model.getRowCount(); row++) {
            Object value = model.getValueAt(row, 3);

            try {
                int quantity = Integer.parseInt(value.toString());

                if (quantity < 0) {
                    quantity = 0;
                }

                double price = ((Number) model.getValueAt(row, 2))
                    .doubleValue();

                total += price * quantity;

            } catch (NumberFormatException ex) {
                // Ignore invalid quantities while editing.
            }
        }

        totalLabel.setText(String.format("Total: ₹%.2f", total));
    };

    model.addTableModelListener(e -> updateTotal.run());

    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setPreferredSize(new Dimension(550, 350));

    JLabel heading = new JLabel("Select food and enter quantities");
    heading.setFont(new Font("SansSerif", Font.BOLD, 16));

    panel.add(heading, BorderLayout.NORTH);
    panel.add(new JScrollPane(table), BorderLayout.CENTER);

    JPanel bottom = new JPanel(new BorderLayout());
    bottom.add(totalLabel, BorderLayout.WEST);

    JButton placeOrderButton = new JButton("Place Order");
    bottom.add(placeOrderButton, BorderLayout.EAST);

    panel.add(bottom, BorderLayout.SOUTH);

    JDialog dialog = new JDialog(
        this,
        "Place an Order",
        true
    );

    placeOrderButton.addActionListener(e -> {

        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        double total = 0;
        StringBuilder bill = new StringBuilder();

        bill.append("YOUR CANTEEN BILL\n");
        bill.append("--------------------------\n");

        for (int row = 0; row < model.getRowCount(); row++) {

            int quantity;

            try {
                quantity = Integer.parseInt(
                    model.getValueAt(row, 3).toString()
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Please enter valid whole-number quantities."
                );
                return;
            }

            if (quantity < 0) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Quantities cannot be negative."
                );
                return;
            }

            if (quantity > 0) {

                String name = model.getValueAt(row, 1).toString();

                double price = ((Number) model.getValueAt(row, 2))
                    .doubleValue();

                double subtotal = price * quantity;
                total += subtotal;

                bill.append(String.format(
                    "%s x %d = ₹%.2f%n",
                    name,
                    quantity,
                    subtotal
                ));
            }
        }

        if (total == 0) {
            JOptionPane.showMessageDialog(
                dialog,
                "Please select at least one food item."
            );
            return;
        }

        bill.append("--------------------------\n");
        bill.append(String.format("TOTAL: ₹%.2f", total));

       ArrayList<FoodItem> orderedItems = new ArrayList<>();
ArrayList<Integer> quantities = new ArrayList<>();

for (int row = 0; row < model.getRowCount(); row++) {

    int quantity = Integer.parseInt(
        model.getValueAt(row, 3).toString()
    );

    if (quantity > 0) {
        int foodId = ((Number) model.getValueAt(row, 0)).intValue();

        for (FoodItem item : availableItems) {
            if (item.id == foodId) {
                orderedItems.add(item);
                quantities.add(quantity);
                break;
            }
        }
    }
}

int orderId = OrderDAO.saveOrder(orderedItems, quantities);

if (orderId == -1) {
    JOptionPane.showMessageDialog(
        dialog,
        "Could not save your order. Please try again.",
        "Order Failed",
        JOptionPane.ERROR_MESSAGE
    );
    return;
}

bill.append("\nOrder ID: ").append(orderId);

JOptionPane.showMessageDialog(
    dialog,
    bill.toString(),
    "Order Confirmed",
    JOptionPane.INFORMATION_MESSAGE
);

        dialog.dispose();

    }); // End of Place Order button action listener

    dialog.add(panel);
    dialog.pack();
    dialog.setMinimumSize(new Dimension(600, 400));
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);

} // End of showOrderScreen()
    private void showFoodMenu() {

    String[] columns = {"Food ID", "Food Name", "Price (₹)", "Availability"};

    DefaultTableModel model = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    try {
        ArrayList<FoodItem> foodItems = FoodDAO.getAllFoodItems();

        for (FoodItem item : foodItems) {
            model.addRow(new Object[]{
                item.id,
                item.name,
                item.price,
                item.available ? "Available" : "Unavailable"
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(
            new Font("SansSerif", Font.BOLD, 14)
        );

        JScrollPane scrollPane = new JScrollPane(table);

        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Food Menu",
            JOptionPane.PLAIN_MESSAGE
        );

    } catch (Exception e) {
        JOptionPane.showMessageDialog(
            this,
            "Could not load the food menu.\n" + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
    public CanteenGUI() {
        setTitle("Campus Canteen | Management Console");
        setSize(1180, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(980, 650));

        Color sidebarColor = new Color(24, 35, 65);
        Color accent = new Color(105, 85, 220);
        Color muted = new Color(115, 123, 145);
        Color page = new Color(246, 247, 251);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(page);

        // Left navigation sidebar
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(225, 0));
        sidebar.setBackground(sidebarColor);
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 15, 20, 15));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel brandIcon = new JLabel("  CAMPUS");
        brandIcon.setFont(new Font("SansSerif", Font.BOLD, 21));
        brandIcon.setForeground(Color.WHITE);
        JLabel brandSub = new JLabel("  CANTEEN MANAGEMENT");
        brandSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        brandSub.setForeground(new Color(185, 195, 220));
        brandIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(brandIcon);
        sidebar.add(Box.createVerticalStrut(3));
        sidebar.add(brandSub);
        sidebar.add(Box.createVerticalStrut(36));

        JLabel navLabel = new JLabel("  WORKSPACE");
        navLabel.setForeground(new Color(150, 162, 192));
        navLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(navLabel);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(createNavButton("Dashboard", true, () -> {}));
        sidebar.add(createNavButton("Food Menu", false, this::showFoodMenu));
        sidebar.add(createNavButton("Place an Order", false, this::showOrderScreen));
        sidebar.add(createNavButton("Administration", false, this::showAdminPanel));
        sidebar.add(Box.createVerticalGlue());

        JPanel sideBottom = new JPanel();
        sideBottom.setOpaque(false);
        sideBottom.setLayout(new BoxLayout(sideBottom, BoxLayout.Y_AXIS));
        JLabel online = new JLabel("  ●  SYSTEM ONLINE");
        online.setForeground(new Color(133, 232, 178));
        online.setFont(new Font("SansSerif", Font.BOLD, 11));
        JLabel version = new JLabel("  Java Swing  •  MySQL");
        version.setForeground(new Color(170, 180, 204));
        version.setFont(new Font("SansSerif", Font.PLAIN, 10));
        online.setAlignmentX(Component.LEFT_ALIGNMENT);
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        sideBottom.add(online);
        sideBottom.add(Box.createVerticalStrut(7));
        sideBottom.add(version);
        sidebar.add(sideBottom);
        root.add(sidebar, BorderLayout.WEST);

        // Main workspace
        JPanel workspace = new JPanel(new BorderLayout(0, 24));
        workspace.setBackground(page);
        workspace.setBorder(BorderFactory.createEmptyBorder(26, 30, 24, 30));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel pageTitle = new JLabel("Dashboard Overview");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 27));
        pageTitle.setForeground(navy);
        JLabel pageSubtitle = new JLabel("Manage meals, orders and daily canteen operations.");
        pageSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pageSubtitle.setForeground(muted);
        titles.add(pageTitle);
        titles.add(Box.createVerticalStrut(6));
        titles.add(pageSubtitle);
        topBar.add(titles, BorderLayout.WEST);

        JLabel dateBadge = new JLabel("  ADMIN CONSOLE  ");
        dateBadge.setOpaque(true);
        dateBadge.setBackground(new Color(235, 232, 255));
        dateBadge.setForeground(accent);
        dateBadge.setFont(new Font("SansSerif", Font.BOLD, 11));
        dateBadge.setBorder(BorderFactory.createEmptyBorder(9, 8, 9, 8));
        topBar.add(dateBadge, BorderLayout.EAST);
        workspace.add(topBar, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JPanel welcomePanel = new JPanel(new BorderLayout(15, 0));
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(231, 233, 241)),
            BorderFactory.createEmptyBorder(22, 24, 22, 24)));
        welcomePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));
        JPanel welcomeText = new JPanel();
        welcomeText.setOpaque(false);
        welcomeText.setLayout(new BoxLayout(welcomeText, BoxLayout.Y_AXIS));
        JLabel welcome = new JLabel("Good food. Great day.");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 23));
        welcome.setForeground(textColor);
        JLabel welcomeDesc = new JLabel("Everything you need to keep your campus canteen running smoothly.");
        welcomeDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        welcomeDesc.setForeground(muted);
        welcomeText.add(welcome);
        welcomeText.add(Box.createVerticalStrut(8));
        welcomeText.add(welcomeDesc);
        welcomePanel.add(welcomeText, BorderLayout.CENTER);
        JLabel foodIcon = new JLabel("  FOOD  ");
        foodIcon.setOpaque(true);
        foodIcon.setBackground(new Color(239, 236, 255));
        foodIcon.setForeground(accent);
        foodIcon.setFont(new Font("SansSerif", Font.BOLD, 15));
        welcomePanel.add(foodIcon, BorderLayout.EAST);
        center.add(welcomePanel);
        center.add(Box.createVerticalStrut(24));

        JLabel quickTitle = new JLabel("QUICK ACTIONS");
        quickTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        quickTitle.setForeground(muted);
        quickTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(quickTitle);
        center.add(Box.createVerticalStrut(12));

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setOpaque(false);
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 235));
        cards.add(createCard("MENU", "Food Menu", "Browse food items, prices and availability.", "VIEW MENU", accent));
        cards.add(createCard("ORDER", "Place an Order", "Select quantities and generate a bill.", "START ORDER", new Color(31, 150, 105)));
        cards.add(createCard("ADMIN", "Administration", "Manage menu items and review sales.", "OPEN ADMIN", new Color(221, 132, 52)));
        center.add(cards);
        center.add(Box.createVerticalStrut(24));

        JPanel operations = new JPanel(new BorderLayout(16, 0));
        operations.setBackground(Color.WHITE);
        operations.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(231, 233, 241)),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        operations.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));
        JPanel opsText = new JPanel();
        opsText.setOpaque(false);
        opsText.setLayout(new BoxLayout(opsText, BoxLayout.Y_AXIS));
        JLabel opsHeading = new JLabel("Daily operations");
        opsHeading.setFont(new Font("SansSerif", Font.BOLD, 16));
        opsHeading.setForeground(textColor);
        JLabel opsDescription = new JLabel("Use the shortcuts to check the menu, create orders or manage canteen records.");
        opsDescription.setFont(new Font("SansSerif", Font.PLAIN, 12));
        opsDescription.setForeground(muted);
        opsText.add(opsHeading);
        opsText.add(Box.createVerticalStrut(8));
        opsText.add(opsDescription);
        operations.add(opsText, BorderLayout.CENTER);
        JButton salesButton = new JButton("VIEW SALES SUMMARY");
        salesButton.setFont(new Font("SansSerif", Font.BOLD, 11));
        salesButton.setForeground(Color.WHITE);
        salesButton.setBackground(navy);
        salesButton.setFocusPainted(false);
        salesButton.setBorderPainted(false);
        salesButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        salesButton.addActionListener(e -> showSalesSummary());
        operations.add(salesButton, BorderLayout.EAST);
        center.add(operations);

        workspace.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        JLabel footerText = new JLabel("CANTEEN MANAGEMENT SYSTEM  •  JAVA + MYSQL");
        footerText.setFont(new Font("SansSerif", Font.PLAIN, 10));
        footerText.setForeground(new Color(135, 140, 158));
        JButton exitButton = new JButton("Exit");
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(e -> System.exit(0));
        footer.add(footerText, BorderLayout.WEST);
        footer.add(exitButton, BorderLayout.EAST);
        workspace.add(footer, BorderLayout.SOUTH);

        root.add(workspace, BorderLayout.CENTER);
        setContentPane(root);
        setVisible(true);
    }

    private JButton createNavButton(String text, boolean selected, Runnable action) {
        JButton button = new JButton("  " + text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("SansSerif", selected ? Font.BOLD : Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(selected ? new Color(105, 85, 220) : new Color(24, 35, 65));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setPreferredSize(new Dimension(190, 42));
        button.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 8));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(e -> action.run());
        return button;
    }


    private JPanel createCard(
        String icon,
        String title,
        String description,
        String buttonText,
        Color accent
    ) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(230, 232, 240)
                ),
                BorderFactory.createEmptyBorder(20, 18, 20, 18)
            )
        );

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 32));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(textColor);

        JLabel descriptionLabel = new JLabel(
            "<html><div style='width:170px'>"
                + description +
            "</div></html>"
        );
        descriptionLabel.setFont(
            new Font("SansSerif", Font.PLAIN, 12)
        );
        descriptionLabel.setForeground(new Color(120, 125, 145));

        JButton button = new JButton(buttonText);
        button.setFont(new Font("SansSerif", Font.BOLD, 11));
        button.setForeground(Color.WHITE);
        button.setBackground(accent);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.addActionListener(e -> {
    if (title.equals("Food Menu")) {
        showFoodMenu();
    } else if (title.equals("Place an Order")) {
        showOrderScreen();
    } else {
        showAdminPanel();
    }
});
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(descriptionLabel);
        card.add(Box.createVerticalGlue());
        card.add(Box.createVerticalStrut(15));
        card.add(button);

        return card;
    }

    private void showSalesSummary() {
        String summary = OrderDAO.getSalesSummary();

        JOptionPane.showMessageDialog(
            this,
            summary,
            "Sales Summary",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CanteenGUI());
    }
}
