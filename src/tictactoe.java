import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class tictactoe extends Application {

    enum Mode { SINGLE, TWO }
    private Stage primaryStage;
    private Scene homeScene;
    private Scene gameScene;
    private Button[][] cells = new Button[3][3];
    private char[][] board = new char[3][3];   // 'X', 'O', or '\0'
    private boolean xTurn = true;
    private Mode currentMode = Mode.TWO;
    private Label statusLabel = new Label();
    private Random rand = new Random();
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Tic Tac Toe - JavaFX");
        primaryStage.setResizable(true);
        createHomeScene();
        primaryStage.setScene(homeScene);
        primaryStage.show();
    }

    private void createHomeScene() {
        Label title = new Label("Tic Tac Toe");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 44));
        title.setTextFill(Color.web("#2E3440"));
        title.setEffect(new DropShadow(5, Color.gray(0.3)));
        Label subtitle = new Label("Play against friend or computer");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#4C566A"));
        Button btnSingle = new Button("Single Player");
        styleMainButton(btnSingle);
        btnSingle.setOnAction(e -> {
            currentMode = Mode.SINGLE;
            startGame();
        });
        Button btnTwo = new Button("Two Player");
        styleMainButton(btnTwo);
        btnTwo.setOnAction(e -> {
            currentMode = Mode.TWO;
            startGame();
        });
        Button btnRules = new Button("Rules");
        styleMainButton(btnRules);
        btnRules.setOnAction(e -> showRules());
        VBox box = new VBox(18, title, subtitle, btnSingle, btnTwo, btnRules);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        box.setBackground(new Background(new BackgroundFill(
                Color.web("#ECEFF4"), new CornerRadii(10), Insets.EMPTY
        )));
        box.setEffect(new DropShadow(10, Color.gray(0.25)));
        BorderPane root = new BorderPane();
        root.setCenter(box);
        root.setPadding(new Insets(20));
        root.setBackground(new Background(new BackgroundFill(
                Color.web("#D8DEE9"), CornerRadii.EMPTY, Insets.EMPTY
        )));
        homeScene = new Scene(root, 900, 600);
    }
    private void startGame() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setBackground(new Background(
                new BackgroundFill(Color.web("#ECEFF4"), CornerRadii.EMPTY, Insets.EMPTY)
        ));
        ToolBar toolBar = new ToolBar();
        toolBar.setStyle("-fx-background-color: linear-gradient(#E5E9F0, #D8DEE9); -fx-padding:8px;");
        Button homeBtn = new Button("Home");
        styleToolbarButton(homeBtn);
        homeBtn.setOnAction(e -> {
            primaryStage.setFullScreen(false);
            primaryStage.setScene(homeScene);
        });
        Button restartBtn = new Button("Restart");
        styleToolbarButton(restartBtn);
        restartBtn.setOnAction(e -> resetBoard());
        Button rulesBtn = new Button("Rules");
        styleToolbarButton(rulesBtn);
        rulesBtn.setOnAction(e -> showRules());
        toolBar.getItems().addAll(homeBtn, restartBtn, rulesBtn);
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(14));
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Button b = new Button("");
                b.setPrefSize(160, 160);
                b.setFont(Font.font("Arial", FontWeight.BOLD, 64));
                b.setStyle(cellStyleEmpty());
                final int rr = r, cc = c;
                b.setOnAction(e -> handleCellClick(rr, cc));
                b.setOnMouseEntered(e -> {
                    if (!b.isDisabled() && b.getText().isEmpty()) {
                        b.setStyle(cellStyleHover());
                    }
                });
                b.setOnMouseExited(e -> {
                    if (!b.isDisabled() && b.getText().isEmpty()) {
                        b.setStyle(cellStyleEmpty());
                    }
                });

                cells[r][c] = b;
                grid.add(b, c, r);
            }
        }
        statusLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 18));
        statusLabel.setPadding(new Insets(8));
        statusLabel.setTextFill(Color.web("#2E3440"));
        HBox statusBox = new HBox(statusLabel);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPadding(new Insets(10));
        statusBox.setStyle("-fx-background-color: rgba(46,52,64,0.05); -fx-background-radius:8;");
        root.setTop(toolBar);
        root.setCenter(grid);
        root.setBottom(statusBox);
        gameScene = new Scene(root, 900, 700);
        resetBoard();
        primaryStage.setScene(gameScene);
        primaryStage.setFullScreenExitHint("Press ESC to exit full screen");
        primaryStage.setFullScreen(true);
    }
    private void styleMainButton(Button b) {
        b.setPrefWidth(260);
        b.setPrefHeight(48);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        b.setTextFill(Color.WHITE);
        b.setStyle("-fx-background-color: linear-gradient(#5E81AC, #4C6A96); -fx-background-radius: 8;");
        b.setOnMouseEntered(e -> b.setStyle(
                "-fx-background-color: linear-gradient(#6B8CC7, #5676B0); -fx-background-radius:8;"
        ));
        b.setOnMouseExited(e -> b.setStyle(
                "-fx-background-color: linear-gradient(#5E81AC, #4C6A96); -fx-background-radius: 8;"
        ));
    }
    private void styleToolbarButton(Button b) {
        b.setFont(Font.font("Arial", 13));
        b.setStyle("-fx-background-color: transparent; -fx-text-fill:#2E3440;");
        b.setOnMouseEntered(e -> b.setStyle(
                "-fx-background-color: rgba(46,52,64,0.06); -fx-text-fill:#2E3440;"
        ));
        b.setOnMouseExited(e -> b.setStyle(
                "-fx-background-color: transparent; -fx-text-fill:#2E3440;"
        ));
    }
    private String cellStyleEmpty() {
        return "-fx-background-color: linear-gradient(#FFFFFF, #F4F6F8);" +
                "-fx-border-color: #D8DEE9; -fx-border-width: 2;" +
                "-fx-background-radius: 12; -fx-border-radius:12;";
    }
    private String cellStyleHover() {
        return "-fx-background-color: linear-gradient(#F7FBFF, #EEF6FF);" +
                "-fx-border-color: #A3BE8C; -fx-border-width: 2;" +
                "-fx-background-radius: 12; -fx-border-radius:12;";
    }
    private void resetBoard() {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++) {
                board[r][c] = '\0';
                cells[r][c].setText("");
                cells[r][c].setDisable(false);
                cells[r][c].setStyle(cellStyleEmpty());
            }
        xTurn = true;
        updateStatus();
    }
    private void handleCellClick(int r, int c) {
        if (board[r][c] != '\0') return;
        board[r][c] = xTurn ? 'X' : 'O';
        cells[r][c].setText(String.valueOf(board[r][c]));
        cells[r][c].setDisable(true);
        if (board[r][c] == 'X') {
            cells[r][c].setTextFill(Color.web("#BF616A"));
        } else {
            cells[r][c].setTextFill(Color.web("#5E81AC"));
        }
        cells[r][c].setStyle(cellStyleEmpty());
        if (checkGameOver()) return;
        xTurn = !xTurn;
        updateStatus();
        if (currentMode == Mode.SINGLE && !xTurn) {
            aiMove();
        }
    }
    private void aiMove() {
        int[] move = findBestMove('O');
        if (move == null) move = findBestMove('X');
        if (move == null) move = randomMove();
        if (move != null) {
            int r = move[0], c = move[1];
            board[r][c] = 'O';
            cells[r][c].setText("O");
            cells[r][c].setDisable(true);
            cells[r][c].setTextFill(Color.web("#5E81AC"));
            if (checkGameOver()) return;
            xTurn = true;
            updateStatus();
        }
    }
    private int[] findBestMove(char p) {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++) {
                if (board[r][c] == '\0') {
                    board[r][c] = p;
                    boolean win = isWinner(p);
                    board[r][c] = '\0';
                    if (win) return new int[]{r, c};
                }
            }
        return null;
    }
    private int[] randomMove() {
        List<int[]> list = new ArrayList<>();
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (board[r][c] == '\0') list.add(new int[]{r, c});
        if (list.isEmpty()) return null;
        return list.get(rand.nextInt(list.size()));
    }
    private boolean checkGameOver() {
        if (isWinner('X')) {
            showEnd("X wins!");
            return true;
        }
        if (isWinner('O')) {
            showEnd("O wins!");
            return true;
        }
        if (isBoardFull()) {
            showEnd("It's a draw!");
            return true;
        }
        return false;
    }
    private boolean isWinner(char p) {
        for (int r = 0; r < 3; r++)
            if (board[r][0] == p && board[r][1] == p && board[r][2] == p)
                return true;
        for (int c = 0; c < 3; c++)
            if (board[0][c] == p && board[1][c] == p && board[2][c] == p)
                return true;
        if (board[0][0] == p && board[1][1] == p && board[2][2] == p)
            return true;
        if (board[0][2] == p && board[1][1] == p && board[2][0] == p)
            return true;
        return false;
    }
    private boolean isBoardFull() {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (board[r][c] == '\0')
                    return false;
        return true;
    }
    private void showEnd(String msg) {
        statusLabel.setText(msg);
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                cells[r][c].setDisable(true);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(msg);
        alert.setContentText("Click Restart to play again or Home to go back.");
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }
    private void updateStatus() {
        if (isBoardFull()) {
            statusLabel.setText("Draw");
        } else {
            statusLabel.setText(
                    (xTurn ? "X" : "O") + "'s turn (" +
                            (currentMode == Mode.SINGLE && !xTurn ? "Computer" : "Human") + ")"
            );
        }
    }
    private void showRules() {

        String rules =
                "Tic Tac Toe Rules:\n\n" +
                        "- Players take turns placing X or O on the 3×3 grid.\n" +
                        "- The first player to get 3 in a row wins.\n" +
                        "- If all squares are filled with no winner, match is a draw.\n\n" +
                        "Single Player Mode:\n" +
                        "- Human is X, Computer is O.";
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Rules");
        a.setHeaderText("How to Play");
        a.setContentText(rules);
        a.initOwner(primaryStage);
        a.showAndWait();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
