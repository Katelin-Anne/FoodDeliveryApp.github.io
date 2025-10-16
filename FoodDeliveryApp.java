import javax.swing.*;
import java.util.*;

public class FoodDeliveryApp {

    static double TAX_RATE = 0.15;              // 15% VAT
    static double BASE_DELIVERY_FEE = 25.00;    // Default delivery fee

    static String[] menuItems = {"Burger", "Pizza", "Pasta", "Salad", "Soda","Juice","Water"};
    static double[] prices = {50.00, 90.00, 70.00, 45.00, 20.00, 15.00, 10.00};

    public static void main(String[] args) {

        // --- Customer Information ---
        String customerName = JOptionPane.showInputDialog(null, "Enter your full name:", "Customer Information", JOptionPane.QUESTION_MESSAGE);
        if (customerName == null || customerName.trim().isEmpty()) customerName = "Guest";

        String customerEmail = JOptionPane.showInputDialog(null, "Enter your email address:", "Customer Information", JOptionPane.QUESTION_MESSAGE);
        if (customerEmail == null || customerEmail.trim().isEmpty()) customerEmail = "Not provided";

        // --- Select Delivery Area ---
        String[] areas = {"City Center", "Suburbs", "Outskirts"};
        double[] deliveryFees = {25.00, 40.00, 60.00};
        String area = (String) JOptionPane.showInputDialog(null, "Select your delivery area:",
                "Delivery Area", JOptionPane.QUESTION_MESSAGE, null, areas, areas[0]);

        if (area == null) {
            JOptionPane.showMessageDialog(null, "Order cancelled.", "QuickEats", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }

        double DELIVERY_FEE = deliveryFees[Arrays.asList(areas).indexOf(area)];

        JOptionPane.showMessageDialog(null,
                "Welcome to QuickEats Food Delivery!\nLet's place your order.",
                "QuickEats", JOptionPane.INFORMATION_MESSAGE);

        // --- Take Order ---
        Map<String, Integer> order = takeOrder();

        if (order.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No items ordered. Thank you for visiting QuickEats!",
                    "QuickEats", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }

        // --- Calculate Total ---
        double subtotal = calculateSubtotal(order);
        double discount = (subtotal > 300) ? subtotal * 0.10 : 0; // 10% off for big orders
        double total = calculateTotal(subtotal, discount, DELIVERY_FEE);

        // --- Display Receipt ---
        displayReceipt(order, subtotal, discount, DELIVERY_FEE, total, customerName, customerEmail, area);

        // --- Track Delivery ---
        trackOrderStatus(customerName);
    }

    // --- Take the customer's order ---
    public static Map<String, Integer> takeOrder() {
        Map<String, Integer> order = new LinkedHashMap<>();
        char moreItems = 'Y';

        while (moreItems == 'Y') {

            // Build menu text
            StringBuilder menuText = new StringBuilder("Menu:\n\n");
            for (int i = 0; i < menuItems.length; i++) {
                menuText.append(String.format("%d. %s - R%.2f\n", i + 1, menuItems[i], prices[i]));
            }
            menuText.append("\nEnter the number of the item you wish to order:");

            String itemNumberStr = JOptionPane.showInputDialog(menuText.toString());
            if (itemNumberStr == null) break;

            int itemNumber;
            try {
                itemNumber = Integer.parseInt(itemNumberStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (itemNumber < 1 || itemNumber > menuItems.length) {
                JOptionPane.showMessageDialog(null, "Invalid choice. Please select an item from the menu.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String qtyStr = JOptionPane.showInputDialog("Enter quantity for " + menuItems[itemNumber - 1] + ":");
            if (qtyStr == null) break;

            int quantity;
            try {
                quantity = Integer.parseInt(qtyStr);
                if (quantity <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid quantity. Please enter a positive number.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String item = menuItems[itemNumber - 1];
            order.put(item, order.getOrDefault(item, 0) + quantity);

            int response = JOptionPane.showConfirmDialog(null,
                    "Do you want to add another item?", "Continue Ordering",
                    JOptionPane.YES_NO_OPTION);

            moreItems = (response == JOptionPane.YES_OPTION) ? 'Y' : 'N';
        }

        return order;
    }

    // --- Calculate subtotal ---
    public static double calculateSubtotal(Map<String, Integer> order) {
        double subtotal = 0.0;

        for (Map.Entry<String, Integer> entry : order.entrySet()) {
            String item = entry.getKey();
            int quantity = entry.getValue();

            for (int i = 0; i < menuItems.length; i++) {
                if (menuItems[i].equals(item)) {
                    subtotal += prices[i] * quantity;
                }
            }
        }
        return subtotal;
    }

    // --- Calculate total ---
    public static double calculateTotal(double subtotal, double discount, double deliveryFee) {
        double tax = (subtotal - discount) * TAX_RATE;
        return (subtotal - discount) + tax + deliveryFee;
    }

    // --- Show receipt ---
    public static void displayReceipt(Map<String, Integer> order, double subtotal, double discount, double deliveryFee,
                                      double total, String name, String email, String area) {
        StringBuilder receipt = new StringBuilder("----- ORDER RECEIPT -----\n");
        receipt.append("Customer: ").append(name).append("\n");
        receipt.append("Email: ").append(email).append("\n");
        receipt.append("Delivery Area: ").append(area).append("\n\n");

        for (Map.Entry<String, Integer> entry : order.entrySet()) {
            receipt.append(String.format("%s x %d\n", entry.getKey(), entry.getValue()));
        }

        receipt.append(String.format("\nSubtotal: R%.2f", subtotal));
        if (discount > 0)
            receipt.append(String.format("\nDiscount (10%%): -R%.2f", discount));

        receipt.append(String.format("\nDelivery Fee: R%.2f", deliveryFee));
        receipt.append(String.format("\nTax (15%%): R%.2f", (subtotal - discount) * TAX_RATE));
        receipt.append(String.format("\n\nTotal Amount: R%.2f", total));
        receipt.append("\n------------------------------");

        JOptionPane.showMessageDialog(null, receipt.toString(), "Order Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- Order Tracking ---
    public static void trackOrderStatus(String name) {
        String[] statuses = {"Order Received", "Preparing", "Out for Delivery", "Delivered"};

        for (String status : statuses) {
            JOptionPane.showMessageDialog(null, status, "Order Status", JOptionPane.INFORMATION_MESSAGE);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        JOptionPane.showMessageDialog(null,
                "Thank you, " + name + "! Your order has been successfully delivered.\nWe hope to serve you again soon!",
                "Delivery Complete", JOptionPane.INFORMATION_MESSAGE);
    }
}
