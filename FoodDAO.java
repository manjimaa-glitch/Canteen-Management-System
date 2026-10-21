import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class FoodDAO {

    public static void displayFoodItems() {

        String sql = "SELECT food_id, food_name, price, availability FROM food_items";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            System.out.println("\n========== CANTEEN MENU ==========");
            System.out.printf("%-5s %-20s %-10s %-15s%n",
                    "ID", "Food Name", "Price", "Availability");

            System.out.println("------------------------------------------------");

            while (result.next()) {

                int id = result.getInt("food_id");
                String name = result.getString("food_name");
                double price = result.getDouble("price");
                boolean available = result.getBoolean("availability");

                System.out.printf("%-5d %-20s ₹%-9.2f %-15s%n",
                        id, name, price,
                        available ? "Available" : "Unavailable");
            }

            System.out.println("==================================");

        } catch (SQLException e) {
            System.out.println("Could not retrieve menu from database.");
            System.out.println(e.getMessage());
        }
    }
public static void addFoodItem(String name, double price) {

    String sql = "INSERT INTO food_items (food_name, price, availability) VALUES (?, ?, TRUE)";

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setString(1, name);
        statement.setDouble(2, price);

        int rows = statement.executeUpdate();

        if (rows > 0) {
            System.out.println("Food item added successfully!");
        }

    } catch (SQLException e) {
        System.out.println("Could not add food item.");
        System.out.println(e.getMessage());
    }
}
public static void updateFoodItem(int id, String name, double price) {

    String sql = "UPDATE food_items SET food_name = ?, price = ? WHERE food_id = ?";

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setString(1, name);
        statement.setDouble(2, price);
        statement.setInt(3, id);

        int rows = statement.executeUpdate();

        if (rows > 0) {
            System.out.println("Food item updated successfully!");
        } else {
            System.out.println("Food item ID not found.");
        }

    } catch (SQLException e) {
        System.out.println("Could not update food item.");
        System.out.println(e.getMessage());
    }
}
public static void deleteFoodItem(int id) {

    String sql = "DELETE FROM food_items WHERE food_id = ?";

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setInt(1, id);

        int rows = statement.executeUpdate();

        if (rows > 0) {
            System.out.println("Food item deleted successfully!");
        } else {
            System.out.println("Food item ID not found.");
        }

    } catch (SQLException e) {
        System.out.println("Could not delete food item.");
        System.out.println(e.getMessage());
    }
}
public static void changeAvailability(int id, boolean available) {

    String sql = "UPDATE food_items SET availability = ? WHERE food_id = ?";

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setBoolean(1, available);
        statement.setInt(2, id);

        int rows = statement.executeUpdate();

        if (rows > 0) {
            System.out.println("Availability updated successfully!");
        } else {
            System.out.println("Food item ID not found.");
        }

    } catch (SQLException e) {
        System.out.println("Could not update availability.");
        System.out.println(e.getMessage());
    }
}
public static ArrayList<FoodItem> getAllFoodItems() {

    ArrayList<FoodItem> menu = new ArrayList<>();

    String sql = "SELECT food_id, food_name, price, availability FROM food_items";

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet result = statement.executeQuery()) {

        while (result.next()) {

            int id = result.getInt("food_id");
            String name = result.getString("food_name");
            double price = result.getDouble("price");
            boolean available = result.getBoolean("availability");

            FoodItem item = new FoodItem(id, name, price);
            item.available = available;

            menu.add(item);
        }

    } catch (SQLException e) {
        System.out.println("Could not load food items from database.");
        System.out.println(e.getMessage());
    }

    return menu;
}
}