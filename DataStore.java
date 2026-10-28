import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataStore {

    private static final Map<String, String> DOTENV = loadDotenv();
    private static final String[] BLOOD_GROUPS = {
        "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
    };
    private static final int[] INITIAL_STOCK = {10, 5, 8, 4, 6, 3, 12, 5};

    public static final ArrayList<Donor> donors = new ArrayList<>();

    private static Connection connect() throws SQLException {
        String host = envOrDefault("MYSQL_HOST", "localhost");
        String port = envOrDefault("MYSQL_PORT", "3306");
        String database = envOrDefault("MYSQL_DATABASE", "blood_bank");
        String username = envOrDefault("MYSQL_USER", "root");
        String password = envOrDefault("MYSQL_PASSWORD", "");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?serverTimezone=UTC";
        return DriverManager.getConnection(url, username, password);
    }

    private static String envOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value == null || value.trim().isEmpty()) {
            value = DOTENV.get(name);
        }
        return value == null || value.trim().isEmpty() ? defaultValue : value;
    }

    private static Map<String, String> loadDotenv() {
        Map<String, String> values = new HashMap<>();
        Path path = Paths.get(".env");
        if (!Files.exists(path)) {
            return values;
        }

        try {
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                    continue;
                }

                int separator = trimmedLine.indexOf('=');
                if (separator <= 0) {
                    continue;
                }

                String name = trimmedLine.substring(0, separator).trim();
                String value = trimmedLine.substring(separator + 1).trim();
                if (value.length() >= 2
                        && ((value.startsWith("\"") && value.endsWith("\""))
                        || (value.startsWith("'") && value.endsWith("'")))) {
                    value = value.substring(1, value.length() - 1);
                }
                values.put(name, value);
            }
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Could not read database settings from .env.", exception);
        }
        return values;
    }

    public static void initialize() throws SQLException {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS donors ("
                    + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(120) NOT NULL, age VARCHAR(10) NOT NULL,"
                    + "gender VARCHAR(30) NOT NULL, blood_group VARCHAR(3) NOT NULL,"
                    + "phone VARCHAR(40) NOT NULL, city VARCHAR(120) NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS blood_stock ("
                    + "blood_group VARCHAR(3) NOT NULL PRIMARY KEY,"
                    + "units INT NOT NULL DEFAULT 0)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS blood_requests ("
                    + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                    + "patient_name VARCHAR(120) NOT NULL,"
                    + "hospital VARCHAR(160) NOT NULL,"
                    + "blood_group VARCHAR(3) NOT NULL,"
                    + "issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");

            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT IGNORE INTO blood_stock (blood_group, units) VALUES (?, ?)")) {
                for (int i = 0; i < BLOOD_GROUPS.length; i++) {
                    insert.setString(1, BLOOD_GROUPS[i]);
                    insert.setInt(2, INITIAL_STOCK[i]);
                    insert.addBatch();
                }
                insert.executeBatch();
            }
        }

        loadDonors();
    }

    private static void loadDonors() throws SQLException {
        ArrayList<Donor> loadedDonors = new ArrayList<>();
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(
                     "SELECT name, age, gender, blood_group, phone, city "
                     + "FROM donors ORDER BY id")) {
            while (results.next()) {
                loadedDonors.add(new Donor(
                        results.getString("name"),
                        results.getString("age"),
                        results.getString("gender"),
                        results.getString("blood_group"),
                        results.getString("phone"),
                        results.getString("city")));
            }
        }
        donors.clear();
        donors.addAll(loadedDonors);
    }

    public static void addDonor(Donor donor) {
        String insertDonor = "INSERT INTO donors "
                + "(name, age, gender, blood_group, phone, city) VALUES (?, ?, ?, ?, ?, ?)";
        String addStock = "UPDATE blood_stock SET units = units + 1 WHERE blood_group = ?";

        try (Connection connection = connect()) {
            connection.setAutoCommit(false);
            try (PreparedStatement donorStatement = connection.prepareStatement(insertDonor);
                 PreparedStatement stockStatement = connection.prepareStatement(addStock)) {
                donorStatement.setString(1, donor.name);
                donorStatement.setString(2, donor.age);
                donorStatement.setString(3, donor.gender);
                donorStatement.setString(4, donor.bloodGroup);
                donorStatement.setString(5, donor.phone);
                donorStatement.setString(6, donor.city);
                donorStatement.executeUpdate();

                stockStatement.setString(1, donor.bloodGroup);
                if (stockStatement.executeUpdate() != 1) {
                    throw new SQLException("Unknown blood group: " + donor.bloodGroup);
                }
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not save donor to the database.", exception);
        }
        donors.add(donor);
    }

    public static int getStock(String bloodGroup) {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT units FROM blood_stock WHERE blood_group = ?")) {
            statement.setString(1, bloodGroup);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? results.getInt("units") : 0;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not load blood stock from the database.", exception);
        }
    }

    public static Map<String, Integer> getStockLevels() {
        Map<String, Integer> levels = new LinkedHashMap<>();
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(
                     "SELECT blood_group, units FROM blood_stock")) {
            while (results.next()) {
                levels.put(results.getString("blood_group"), results.getInt("units"));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not load blood stock from the database.", exception);
        }
        return levels;
    }

    public static boolean issueBlood(String bloodGroup, String patient, String hospital) {
        String reduceStock = "UPDATE blood_stock SET units = units - 1 "
                + "WHERE blood_group = ? AND units > 0";
        String insertRequest = "INSERT INTO blood_requests "
                + "(patient_name, hospital, blood_group) VALUES (?, ?, ?)";

        try (Connection connection = connect()) {
            connection.setAutoCommit(false);
            try (PreparedStatement stockStatement = connection.prepareStatement(reduceStock);
                 PreparedStatement requestStatement = connection.prepareStatement(insertRequest)) {
                stockStatement.setString(1, bloodGroup);
                if (stockStatement.executeUpdate() != 1) {
                    connection.rollback();
                    return false;
                }

                requestStatement.setString(1, patient);
                requestStatement.setString(2, hospital);
                requestStatement.setString(3, bloodGroup);
                requestStatement.executeUpdate();
                connection.commit();
                return true;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not issue blood from the database.", exception);
        }
    }
}