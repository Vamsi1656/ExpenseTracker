package Task1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;


class Transaction {
    enum Type { INCOME, EXPENSE }
    private Type type;
    private double amount;
    private String category;
    private LocalDate date;

    public Transaction(Type type, double amount, String category, LocalDate date) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public Type getType() { return type; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public LocalDate getDate() { return date; }

    @Override
    public String toString() {
        return type + "," + amount + "," + category + "," + date;
    }

    public static Transaction fromCSV(String line) {
        String[] parts = line.split(",");
        return new Transaction(
            Type.valueOf(parts[0]),
            Double.parseDouble(parts[1]),
            parts[2],
            LocalDate.parse(parts[3])
        );
    }
}


public class ExpenseTracker {
    private static List<Transaction> transactions = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) throws IOException {
        while (true) {
            System.out.println("\n=== Expense Tracker ===");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. Load from File");
            System.out.println("4. Save to File");
            System.out.println("5. View Monthly Summary");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt(); scanner.nextLine();

            switch (option) {
                case 1: addTransaction(Transaction.Type.INCOME); break;
                case 2: addTransaction(Transaction.Type.EXPENSE); break;
                case 3: loadFromFile(); break;
                case 4: saveToFile(); break;
                case 5: viewMonthlySummary(); break;
                case 6: return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void addTransaction(Transaction.Type type) {
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble(); scanner.nextLine();

        System.out.print("Enter category (e.g., Salary/Business or Food/Rent/Travel): ");
        String category = scanner.nextLine();

        System.out.print("Enter date (yyyy-MM-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine(), dateFormatter);

        transactions.add(new Transaction(type, amount, category, date));
        System.out.println("Transaction added.");
    }

    private static void saveToFile() throws IOException {
        System.out.print("Enter filename to save (e.g., data.csv): ");
        String filename = scanner.nextLine();
        Files.write(Paths.get(filename),
            transactions.stream().map(Transaction::toString).collect(Collectors.toList()));
        System.out.println("Data saved to " + filename);
    }

    private static void loadFromFile() throws IOException {
        System.out.print("Enter filename to load (e.g., data.csv): ");
        String filename = scanner.nextLine();
        List<String> lines = Files.readAllLines(Paths.get(filename));
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                transactions.add(Transaction.fromCSV(line));
            }
        }
        System.out.println("Data loaded from " + filename);
    }

    private static void viewMonthlySummary() {
        System.out.print("Enter year-month (e.g., 2025-05): ");
        String ym = scanner.nextLine();
        double totalIncome = 0, totalExpense = 0;
        Map<String, Double> incomeCategories = new HashMap<>();
        Map<String, Double> expenseCategories = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.getDate().toString().startsWith(ym)) {
                if (t.getType() == Transaction.Type.INCOME) {
                    totalIncome += t.getAmount();
                    incomeCategories.merge(t.getCategory(), t.getAmount(), Double::sum);
                } else {
                    totalExpense += t.getAmount();
                    expenseCategories.merge(t.getCategory(), t.getAmount(), Double::sum);
                }
            }
        }

        System.out.println("\n--- Monthly Summary for " + ym + " ---");
        System.out.println("Total Income: ₹" + totalIncome);
        incomeCategories.forEach((cat, amt) -> System.out.println("  " + cat + ": ₹" + amt));
        System.out.println("Total Expense: ₹" + totalExpense);
        expenseCategories.forEach((cat, amt) -> System.out.println("  " + cat + ": ₹" + amt));
        System.out.println("Net Savings: ₹" + (totalIncome - totalExpense));
    }
}