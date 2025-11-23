package com.greencommute.controller;

import com.greencommute.dao.CommuteDAO;
import com.greencommute.dao.UserDAO;
import com.greencommute.model.Commute;
import com.greencommute.model.User;
import com.greencommute.model.Challenge;
import com.greencommute.model.Trip;
import com.greencommute.model.Reward;
import com.greencommute.util.SessionManager;
import com.greencommute.util.CO2Calculator;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.chart.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class DashboardController {

    // Home Tab Elements
    @FXML private Label welcomeLabel;
    @FXML private Label pointsLabel;
    @FXML private Label tierLabel;
    @FXML private Label streakLabel;
    @FXML private ProgressBar weeklyGoalProgress;
    @FXML private Label weeklyGoalLabel;
    @FXML private VBox challengesBox;
    @FXML private VBox recentTripsBox;

    // Trip Planner Tab
    @FXML private TextField tripFromField;
    @FXML private TextField tripToField;
    @FXML private TextField tripDistanceField;
    @FXML private VBox recommendedTripsBox;

    // Rewards Tab
    @FXML private Label availablePointsLabel;
    @FXML private VBox rewardsListBox;
    @FXML private TextArea rewardHistoryArea;

    // CO2 Tracker Tab
    @FXML private Label totalCO2Label;
    @FXML private Label treesEquivLabel;
    @FXML private TextField ecoDistanceField;
    @FXML private ComboBox<String> ecoModeCombo;
    @FXML private Label ecoResultLabel;

    // Log Trip Tab
    @FXML private DatePicker logDatePicker;
    @FXML private TextField logFromField;
    @FXML private TextField logToField;
    @FXML private ComboBox<String> logModeCombo;
    @FXML private TextField logDistanceField;
    @FXML private TextField logDurationField;
    @FXML private Label logStatusLabel;

    // Commute History Table
    @FXML private TableView<Commute> commuteTable;
    @FXML private TableColumn<Commute, LocalDate> dateCol;
    @FXML private TableColumn<Commute, String> fromCol;
    @FXML private TableColumn<Commute, String> toCol;
    @FXML private TableColumn<Commute, String> modeCol;
    @FXML private TableColumn<Commute, Double> distanceCol;
    @FXML private TableColumn<Commute, Integer> durationCol;
    @FXML private TableColumn<Commute, Double> co2Col;
    @FXML private TableColumn<Commute, Integer> pointsCol;

    // Chart Elements - NEW
    @FXML private BarChart<String, Number> modeChart;
    @FXML private LineChart<String, Number> co2Chart;
    @FXML private PieChart distanceChart;
    @FXML private Label totalTripsLabel;
    @FXML private Label totalDistanceLabel;
    @FXML private Label totalPointsEarnedLabel;

    private User currentUser;
    private ObservableList<Commute> commuteList;
    private List<Challenge> challenges;

    @FXML
    private void initialize() {
        currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            showAlert("Error", "No user logged in!");
            return;
        }

        // Initialize all tabs
        initializeHomeTab();
        initializeTripPlannerTab();
        initializeRewardsTab();
        initializeCO2Tab();
        initializeLogTripTab();
        initializeHistoryTable();
        initializeCharts();  // NEW - Initialize charts
        loadUserData();
    }

    private void initializeHomeTab() {
        welcomeLabel.setText("Welcome back, " + currentUser.getFullname() + "!");
        pointsLabel.setText("Points: " + currentUser.getTotalPoints());
        tierLabel.setText("Tier: " + currentUser.getRewardTier());
        streakLabel.setText("Current Streak: " + currentUser.getCurrentStreak() + " days");

        // Weekly goal (example: 5 trips)
        weeklyGoalProgress.setProgress(0.4); // Example: 2/5 trips
        weeklyGoalLabel.setText("Weekly Goal: 2/5 trips");

        // Load challenges
        loadChallenges();
        displayChallenges();

        // Load recent trips
        loadRecentTrips();
    }

    private void loadChallenges() {
        challenges = new ArrayList<>();
        challenges.add(new Challenge(1, "Car-Free Week", "Don't use a car for 7 days", 200, 7));
        challenges.add(new Challenge(2, "Bike Champion", "Bike 50 km this month", 150, 50));
        challenges.add(new Challenge(3, "Public Transit Hero", "Use public transit 10 times", 100, 10));

        // Set some example progress
        challenges.get(0).setProgress(3);
        challenges.get(1).setProgress(25);
        challenges.get(2).setProgress(7);
    }

    private void displayChallenges() {
        challengesBox.getChildren().clear();
        for (Challenge challenge : challenges) {
            VBox challengeCard = createChallengeCard(challenge);
            challengesBox.getChildren().add(challengeCard);
        }
    }

    private VBox createChallengeCard(Challenge challenge) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-padding: 15; " +
                "-fx-background-radius: 10; -fx-border-color: #4CAF50; -fx-border-width: 1; -fx-border-radius: 10;");

        Label nameLabel = new Label(challenge.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        Label descLabel = new Label(challenge.getDescription());
        descLabel.setStyle("-fx-text-fill: #e0e0e0;");

        ProgressBar progressBar = new ProgressBar(challenge.getProgressPercentage() / 100.0);
        progressBar.setPrefWidth(250);
        progressBar.setStyle("-fx-accent: #4CAF50;");

        Label progressLabel = new Label(String.format("%d/%d - %d points",
                challenge.getProgress(), challenge.getTarget(), challenge.getRewardPoints()));
        progressLabel.setStyle("-fx-text-fill: #b0b0b0; -fx-font-size: 12px;");

        card.getChildren().addAll(nameLabel, descLabel, progressBar, progressLabel);
        return card;
    }

    private void loadRecentTrips() {
        recentTripsBox.getChildren().clear();
        try {
            List<Commute> recent = CommuteDAO.getCommutesByUser(currentUser.getId());
            int count = Math.min(5, recent.size());
            for (int i = 0; i < count; i++) {
                Commute c = recent.get(i);
                Label tripLabel = new Label(String.format("%s: %s → %s (%s) - %d pts",
                        c.getDate().toString(), c.getFromLocation(), c.getToLocation(),
                        c.getMode(), c.getPointsEarned()));
                tripLabel.setStyle("-fx-text-fill: #e0e0e0; -fx-padding: 5;");
                recentTripsBox.getChildren().add(tripLabel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeTripPlannerTab() {
        // Will display recommended trips when user enters route
    }

    @FXML
    private void onPlanTrip() {
        String from = tripFromField.getText().trim();
        String to = tripToField.getText().trim();
        String distStr = tripDistanceField.getText().trim();

        if (from.isEmpty() || to.isEmpty() || distStr.isEmpty()) {
            showAlert("Input Error", "Please fill all fields");
            return;
        }

        try {
            double distance = Double.parseDouble(distStr);
            displayRecommendedTrips(from, to, distance);
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Invalid distance value");
        }
    }

    private void displayRecommendedTrips(String from, String to, double distance) {
        recommendedTripsBox.getChildren().clear();

        List<Trip> trips = new ArrayList<>();
        trips.add(new Trip("Walk", (int) (distance * 12), distance, 20, distance * 0.1,
                CO2Calculator.calculateCO2Saved("walk", distance), "🚶"));
        trips.add(new Trip("Bike", (int) (distance * 4), distance, 20, distance * 0.1,
                CO2Calculator.calculateCO2Saved("bike", distance), "🚴"));
        trips.add(new Trip("Bus", (int) (distance * 3), distance, 15, distance * 0.08,
                CO2Calculator.calculateCO2Saved("bus", distance), "🚌"));
        trips.add(new Trip("Train", (int) (distance * 2.5), distance, 15, distance * 0.08,
                CO2Calculator.calculateCO2Saved("train", distance), "🚆"));
        trips.add(new Trip("Carpool", (int) (distance * 2), distance, 10, distance * 0.05,
                CO2Calculator.calculateCO2Saved("carpool", distance), "🚗"));

        for (Trip trip : trips) {
            VBox tripCard = createTripCard(trip, from, to, distance);
            recommendedTripsBox.getChildren().add(tripCard);
        }
    }

    private VBox createTripCard(Trip trip, String from, String to, double distance) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-padding: 15; " +
                "-fx-background-radius: 10; -fx-border-color: #4CAF50; -fx-border-width: 1; -fx-border-radius: 10;");

        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        Label iconLabel = new Label(trip.getIcon());
        iconLabel.setStyle("-fx-font-size: 24px;");
        Label modeLabel = new Label(trip.getMode());
        modeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        headerBox.getChildren().addAll(iconLabel, modeLabel);

        Label durationLabel = new Label("Duration: " + trip.getDuration() + " min");
        durationLabel.setStyle("-fx-text-fill: #e0e0e0;");

        Label distanceLabel = new Label(String.format("Distance: %.1f km", trip.getDistance()));
        distanceLabel.setStyle("-fx-text-fill: #e0e0e0;");

        Label pointsLabel = new Label("Points: " + trip.getPoints());
        pointsLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");

        Label co2Label = new Label(String.format("CO₂ Saved: %.2f kg", trip.getCo2Saved()));
        co2Label.setStyle("-fx-text-fill: #8BC34A;");

        Label fuelLabel = new Label(String.format("Fuel Saved: %.2f L", trip.getFuelSaved()));
        fuelLabel.setStyle("-fx-text-fill: #b0b0b0; -fx-font-size: 12px;");

        Button selectBtn = new Button("Select This Trip");
        selectBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
        selectBtn.setOnAction(e -> {
            // Pre-fill log trip tab
            logFromField.setText(from);
            logToField.setText(to);
            logModeCombo.setValue(trip.getMode());
            logDistanceField.setText(String.valueOf(distance));
            logDurationField.setText(String.valueOf(trip.getDuration()));
            showAlert("Trip Selected", "Trip details filled in Log Trip tab. Please review and log!");
        });

        card.getChildren().addAll(headerBox, durationLabel, distanceLabel, pointsLabel,
                co2Label, fuelLabel, selectBtn);
        return card;
    }

    private void initializeRewardsTab() {
        availablePointsLabel.setText("Available Points: " + currentUser.getTotalPoints());
        displayRewards();
    }

    private void displayRewards() {
        rewardsListBox.getChildren().clear();

        List<Reward> rewards = new ArrayList<>();
        rewards.add(new Reward(1, "$5 Coffee Gift Card", "Redeem at participating cafes", 100, "Food"));
        rewards.add(new Reward(2, "$10 Transit Pass", "Monthly transit credit", 200, "Transport"));
        rewards.add(new Reward(3, "$25 Eco Store Voucher", "Shop sustainable products", 500, "Shopping"));
        rewards.add(new Reward(4, "$50 Green Energy Credit", "Solar/wind energy credit", 1000, "Energy"));
        rewards.add(new Reward(5, "$100 Bike Shop Voucher", "Upgrade your bike!", 2000, "Transport"));

        for (Reward reward : rewards) {
            VBox rewardCard = createRewardCard(reward);
            rewardsListBox.getChildren().add(rewardCard);
        }
    }

    private VBox createRewardCard(Reward reward) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-padding: 15; " +
                "-fx-background-radius: 10; -fx-border-color: #FFD700; -fx-border-width: 1; -fx-border-radius: 10;");

        Label nameLabel = new Label(reward.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        Label descLabel = new Label(reward.getDescription());
        descLabel.setStyle("-fx-text-fill: #e0e0e0;");

        Label pointsLabel = new Label("Required: " + reward.getRequiredPoints() + " points");
        pointsLabel.setStyle("-fx-text-fill: #b0b0b0; -fx-font-size: 12px;");

        Label categoryLabel = new Label("Category: " + reward.getCategory());
        categoryLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 11px;");

        Button redeemBtn = new Button("Redeem");
        redeemBtn.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black; -fx-cursor: hand;");

        if (currentUser.getTotalPoints() < reward.getRequiredPoints()) {
            redeemBtn.setDisable(true);
            redeemBtn.setText("Not Enough Points");
        }

        redeemBtn.setOnAction(e -> {
            if (currentUser.getTotalPoints() >= reward.getRequiredPoints()) {
                redeemReward(reward);
            }
        });

        card.getChildren().addAll(nameLabel, descLabel, categoryLabel, pointsLabel, redeemBtn);
        return card;
    }

    private void redeemReward(Reward reward) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Redemption");
        confirm.setHeaderText("Redeem " + reward.getName() + "?");
        confirm.setContentText("This will cost " + reward.getRequiredPoints() + " points.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    int newPoints = currentUser.getTotalPoints() - reward.getRequiredPoints();
                    UserDAO.updateUserPoints(currentUser.getId(), -reward.getRequiredPoints());
                    currentUser.setTotalPoints(newPoints);

                    availablePointsLabel.setText("Available Points: " + newPoints);
                    pointsLabel.setText("Points: " + newPoints);

                    String historyEntry = LocalDate.now() + " - Redeemed: " + reward.getName() +
                            " (-" + reward.getRequiredPoints() + " pts)\n";
                    rewardHistoryArea.appendText(historyEntry);

                    displayRewards(); // Refresh
                    showAlert("Success", "Reward redeemed successfully!");
                } catch (SQLException e) {
                    showAlert("Error", "Failed to redeem reward: " + e.getMessage());
                }
            }
        });
    }

    private void initializeCO2Tab() {
        ecoModeCombo.setItems(FXCollections.observableArrayList(
                "Walk", "Bike", "Bus", "Train", "Carpool", "Car"
        ));
        ecoModeCombo.setValue("Walk");

        loadTotalCO2();
    }

    private void loadTotalCO2() {
        try {
            double totalCO2 = CommuteDAO.getTotalCO2Saved(currentUser.getId());
            totalCO2Label.setText(String.format("Total CO₂ Saved: %.2f kg", totalCO2));

            double trees = CO2Calculator.calculateTreesEquivalent(totalCO2);
            treesEquivLabel.setText(String.format("Equivalent to %.2f trees planted! 🌳", trees));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCalculateCO2() {
        String distStr = ecoDistanceField.getText().trim();
        String mode = ecoModeCombo.getValue();

        if (distStr.isEmpty()) {
            ecoResultLabel.setText("Please enter distance");
            return;
        }

        try {
            double distance = Double.parseDouble(distStr);
            double co2Saved = CO2Calculator.calculateCO2Saved(mode, distance);
            int points = CO2Calculator.calculatePoints(mode, distance, co2Saved);

            ecoResultLabel.setText(String.format(
                    "CO₂ Saved: %.2f kg | Points: %d | Trees: %.3f 🌳",
                    co2Saved, points, CO2Calculator.calculateTreesEquivalent(co2Saved)
            ));
            ecoResultLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 14px; -fx-font-weight: bold;");
        } catch (NumberFormatException e) {
            ecoResultLabel.setText("Invalid distance value");
            ecoResultLabel.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    private void initializeLogTripTab() {
        logModeCombo.setItems(FXCollections.observableArrayList(
                "Walk", "Bike", "Bus", "Train", "Carpool", "Car"
        ));
        logModeCombo.setValue("Walk");
        logDatePicker.setValue(LocalDate.now());
    }

    @FXML
    private void onLogTrip() {
        String from = logFromField.getText().trim();
        String to = logToField.getText().trim();
        String mode = logModeCombo.getValue();
        String distStr = logDistanceField.getText().trim();
        String durStr = logDurationField.getText().trim();
        LocalDate date = logDatePicker.getValue();

        if (from.isEmpty() || to.isEmpty() || distStr.isEmpty() || durStr.isEmpty()) {
            logStatusLabel.setText("Please fill all fields");
            logStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        try {
            double distance = Double.parseDouble(distStr);
            int duration = Integer.parseInt(durStr);

            double co2Saved = CO2Calculator.calculateCO2Saved(mode, distance);
            int points = CO2Calculator.calculatePoints(mode, distance, co2Saved);

            Commute commute = new Commute();
            commute.setUserId(currentUser.getId());
            commute.setDate(date);
            commute.setFromLocation(from);
            commute.setToLocation(to);
            commute.setMode(mode);
            commute.setDistance(distance);
            commute.setDuration(duration);
            commute.setCo2Saved(co2Saved);
            commute.setPointsEarned(points);

            boolean success = CommuteDAO.addCommute(commute);
            if (success) {
                UserDAO.updateUserPoints(currentUser.getId(), points);
                currentUser.setTotalPoints(currentUser.getTotalPoints() + points);

                logStatusLabel.setText(String.format("Trip logged! Earned %d points, saved %.2f kg CO₂",
                        points, co2Saved));
                logStatusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");

                // Update displays
                pointsLabel.setText("Points: " + currentUser.getTotalPoints());
                loadRecentTrips();
                loadHistoryData();
                loadTotalCO2();
                loadChartData(); // NEW - Refresh charts

                // Clear fields
                logFromField.clear();
                logToField.clear();
                logDistanceField.clear();
                logDurationField.clear();
                logDatePicker.setValue(LocalDate.now());

                // Update challenges progress (simplified example)
                updateChallengesProgress(mode, distance);
            } else {
                logStatusLabel.setText("Failed to log trip");
                logStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
            }
        } catch (NumberFormatException e) {
            logStatusLabel.setText("Invalid distance or duration");
            logStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
        } catch (SQLException e) {
            logStatusLabel.setText("Database error: " + e.getMessage());
            logStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    private void updateChallengesProgress(String mode, double distance) {
        // Update challenge progress based on trip
        for (Challenge challenge : challenges) {
            if (challenge.getName().equals("Bike Champion") && mode.equalsIgnoreCase("Bike")) {
                challenge.setProgress(challenge.getProgress() + (int) distance);
            } else if (challenge.getName().equals("Public Transit Hero") &&
                    (mode.equalsIgnoreCase("Bus") || mode.equalsIgnoreCase("Train"))) {
                challenge.setProgress(challenge.getProgress() + 1);
            }
        }
        displayChallenges();
    }

    private void initializeHistoryTable() {
        dateCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getDate()));
        fromCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFromLocation()));
        toCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getToLocation()));
        modeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMode()));
        distanceCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getDistance()).asObject());
        durationCol.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getDuration()).asObject());
        co2Col.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getCo2Saved()).asObject());
        pointsCol.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getPointsEarned()).asObject());

        commuteList = FXCollections.observableArrayList();
        commuteTable.setItems(commuteList);
    }

    private void loadUserData() {
        loadHistoryData();
    }

    private void loadHistoryData() {
        try {
            List<Commute> commutes = CommuteDAO.getCommutesByUser(currentUser.getId());
            commuteList.clear();
            commuteList.addAll(commutes);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load commute history");
        }
    }

    // ========================================
    // NEW CHART METHODS
    // ========================================

    private void initializeCharts() {
        loadChartData();
    }

    private void loadChartData() {
        try {
            List<Commute> commutes = CommuteDAO.getCommutesByUser(currentUser.getId());

            updateStatistics(commutes);
            loadModeChart(commutes);
            loadCO2Chart(commutes);
            loadDistanceChart(commutes);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateStatistics(List<Commute> commutes) {
        int totalTrips = commutes.size();
        double totalDistance = commutes.stream().mapToDouble(Commute::getDistance).sum();
        int totalPoints = commutes.stream().mapToInt(Commute::getPointsEarned).sum();

        totalTripsLabel.setText(String.valueOf(totalTrips));
        totalDistanceLabel.setText(String.format("%.1f km", totalDistance));
        totalPointsEarnedLabel.setText(String.valueOf(totalPoints));
    }

    private void loadModeChart(List<Commute> commutes) {
        Map<String, Integer> modeCount = new HashMap<>();

        for (Commute c : commutes) {
            modeCount.put(c.getMode(), modeCount.getOrDefault(c.getMode(), 0) + 1);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Trips");

        Map<String, String> modeEmojis = new HashMap<>();
        modeEmojis.put("Walk", "🚶 Walk");
        modeEmojis.put("Bike", "🚴 Bike");
        modeEmojis.put("Bus", "🚌 Bus");
        modeEmojis.put("Train", "🚆 Train");
        modeEmojis.put("Carpool", "🚗 Carpool");
        modeEmojis.put("Car", "🚙 Car");

        for (Map.Entry<String, Integer> entry : modeCount.entrySet()) {
            String modeName = modeEmojis.getOrDefault(entry.getKey(), entry.getKey());
            series.getData().add(new XYChart.Data<>(modeName, entry.getValue()));
        }

        modeChart.getData().clear();
        modeChart.getData().add(series);
        modeChart.setLegendVisible(false);
    }

    private void loadCO2Chart(List<Commute> commutes) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("CO₂ Saved");

        commutes.sort((a, b) -> a.getDate().compareTo(b.getDate()));

        double cumulativeCO2 = 0;
        for (Commute c : commutes) {
            cumulativeCO2 += c.getCo2Saved();
            series.getData().add(new XYChart.Data<>(
                    c.getDate().toString(),
                    cumulativeCO2
            ));
        }

        co2Chart.getData().clear();
        co2Chart.getData().add(series);
    }

    private void loadDistanceChart(List<Commute> commutes) {
        Map<String, Double> distanceByMode = new HashMap<>();

        for (Commute c : commutes) {
            distanceByMode.put(c.getMode(),
                    distanceByMode.getOrDefault(c.getMode(), 0.0) + c.getDistance());
        }

        Map<String, String> modeEmojis = new HashMap<>();
        modeEmojis.put("Walk", "🚶 Walk");
        modeEmojis.put("Bike", "🚴 Bike");
        modeEmojis.put("Bus", "🚌 Bus");
        modeEmojis.put("Train", "🚆 Train");
        modeEmojis.put("Carpool", "🚗 Carpool");
        modeEmojis.put("Car", "🚙 Car");

        distanceChart.getData().clear();
        for (Map.Entry<String, Double> entry : distanceByMode.entrySet()) {
            String modeName = modeEmojis.getOrDefault(entry.getKey(), entry.getKey());
            PieChart.Data data = new PieChart.Data(
                    modeName + " (" + String.format("%.1f", entry.getValue()) + " km)",
                    entry.getValue()
            );
            distanceChart.getData().add(data);
        }
    }

    // ========================================
    // END CHART METHODS
    // ========================================

    @FXML
    private void onLogout() {
        SessionManager.clearSession();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/greencommute/fxml/login.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/com/greencommute/styles/style.css").toExternalForm());
            javafx.stage.Stage stage = (javafx.stage.Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}