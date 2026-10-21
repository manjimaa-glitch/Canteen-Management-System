import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FoodItem {
    int id;
    String name;
    double price;
    boolean available;

    public FoodItem(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.available = true;
    }

    public void displayItem() {
        System.out.printf(
            "%-5d %-20s Rs. %-10.2f %s%n",
            id,
            name,
            price,
            available ? "Available" : "Unavailable"
        );
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
}