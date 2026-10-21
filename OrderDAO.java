import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDAO {

    public static int saveOrder(ArrayList<FoodItem> items, ArrayList<Integer> quantities) {

        String orderSQL =
            "INSERT INTO orders (total_amount) VALUES (?)";

        String itemSQL =
            "INSERT INTO order_items " +
            "(order_id, food_id, food_name, quantity, price, subtotal) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                double total = 0;

                for (int i = 0; i < items.size(); i++) {
                    total += items.get(i).price * quantities.get(i);
                }

                int orderId;

                try (PreparedStatement orderStatement =
                         connection.prepareStatement(
                             orderSQL,
                             PreparedStatement.RETURN_GENERATED_KEYS
                         )) {

                    orderStatement.setDouble(1, total);
                    orderStatement.executeUpdate();

                    try (ResultSet keys = orderStatement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Could not generate order ID.");
                        }

                        orderId = keys.getInt(1);
                    }
                }

                try (PreparedStatement itemStatement =
                         connection.prepareStatement(itemSQL)) {

                    for (int i = 0; i < items.size(); i++) {

                        FoodItem item = items.get(i);
                        int quantity = quantities.get(i);
                        double subtotal = item.price * quantity;

                        itemStatement.setInt(1, orderId);
                        itemStatement.setInt(2, item.id);
                        itemStatement.setString(3, item.name);
                        itemStatement.setInt(4, quantity);
                        itemStatement.setDouble(5, item.price);
                        itemStatement.setDouble(6, subtotal);

                        itemStatement.addBatch();
                    }

                    itemStatement.executeBatch();
                }

                connection.commit();
                connection.setAutoCommit(true);

                return orderId;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Could not save order: " + e.getMessage());
            return -1;
        }
    }
public static String getOrderHistory() {

    StringBuilder history = new StringBuilder();

    String sql = "SELECT order_id, order_date, total_amount " +
                 "FROM orders ORDER BY order_id DESC";

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet result = statement.executeQuery()) {

        history.append(String.format(
            "%-10s %-25s %-15s%n",
            "Order ID", "Order Date", "Total (₹)"
        ));

        history.append(
            "----------------------------------------------------------\n"
        );

        boolean found = false;

        while (result.next()) {
            found = true;

            history.append(String.format(
                "%-10d %-25s ₹%-14.2f%n",
                result.getInt("order_id"),
                result.getTimestamp("order_date"),
                result.getDouble("total_amount")
            ));
        }

        if (!found) {
            return "No orders have been placed yet.";
        }

    } catch (SQLException e) {
        return "Could not retrieve order history.\n" + e.getMessage();
    }

    return history.toString();
}
public static String getOrderDetails(int orderId) {

    StringBuilder details = new StringBuilder();

    String sql = """
        SELECT food_name, quantity, price, subtotal
        FROM order_items
        WHERE order_id = ?
        """;

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setInt(1, orderId);

        try (ResultSet result = statement.executeQuery()) {

            details.append("ORDER ID: ").append(orderId).append("\n\n");

            details.append(String.format(
                "%-20s %8s %10s %12s%n",
                "Food", "Quantity", "Price", "Subtotal"
            ));

            details.append(
                "--------------------------------------------------------\n"
            );

            double total = 0;
            boolean found = false;

            while (result.next()) {
                found = true;

                String name = result.getString("food_name");
                int quantity = result.getInt("quantity");
                double price = result.getDouble("price");
                double subtotal = result.getDouble("subtotal");

                total += subtotal;

                details.append(String.format(
                    "%-20s %8d ₹%9.2f ₹%11.2f%n",
                    name, quantity, price, subtotal
                ));
            }

            if (!found) {
                return "No items found for order #" + orderId;
            }

            details.append(
                "--------------------------------------------------------\n"
            );

            details.append(String.format("TOTAL: ₹%.2f", total));
        }

    } catch (SQLException e) {
        return "Could not retrieve order details.\n" + e.getMessage();
    }

    return details.toString();
}
public static String getSalesSummary() {
    StringBuilder summary = new StringBuilder();

    String sql = """
        SELECT
            COUNT(*) AS total_orders,
            COALESCE(SUM(total_amount), 0) AS total_sales
        FROM orders
        """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        if (rs.next()) {
            summary.append("===== SALES SUMMARY =====\n\n");
            summary.append("Total Orders: ")
                   .append(rs.getInt("total_orders"))
                   .append("\n");

            summary.append("Total Sales: ₹")
                   .append(String.format("%.2f",
                           rs.getDouble("total_sales")))
                   .append("\n");
        }

    } catch (SQLException e) {
        return "Could not load sales summary.\nError: " + e.getMessage();
    }

    return summary.toString();
}
}