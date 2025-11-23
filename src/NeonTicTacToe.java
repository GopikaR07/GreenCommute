import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class NeonTicTacToe {

    private Button[][] board = new Button[3][3];
    private boolean playerX = true;
    private Label status;
    private BorderPane root;

    public Pane createContent() {
        root = new BorderPane();
        showWelcomeScreen();
        return root;
    }

    // ---------------------- WELCOME PAGE -------------------------
    private void showWelcomeScreen() {

        Label title = new Label("NEON TIC TAC TOE");
        title.setStyle(
                "-fx-font-size: 48px;"
                        + "-fx-text-fill: #00eaff;"
                        + "-fx-effect: dropshadow(three-pass-box, rgba(0,234,255,0.45), 20, 0.3, 0, 0);"
        );
        title.setPadding(new Insets(20));

        Button startBtn = new Button("START GAME");
        startBtn.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-border-color: #ff00ff;"
                        + "-fx-border-width: 3px;"
                        + "-fx-font-size: 24px;"
                        + "-fx-text-fill: #ff00ff;"
                        + "-fx-padding: 10 20;"
                        + "-fx-effect: dropshadow(three-pass-box, rgba(255,0,255,0.40), 25, 0.3, 0, 0);"
        );

        startBtn.setOnMouseEntered(e ->
                startBtn.setStyle(
                        "-fx-background-color: #ff00ff;"
                                + "-fx-text-fill: black;"
                                + "-fx-font-size: 24px;"
                                + "-fx-border-color: #ff00ff;"
                                + "-fx-border-width: 3px;"
                                + "-fx-padding: 10 20;"
                )
        );

        startBtn.setOnMouseExited(e ->
                startBtn.setStyle(
                        "-fx-background-color: transparent;"
                                + "-fx-border-color: #ff00ff;"
                                + "-fx-border-width: 3px;"
                                + "-fx-font-size: 24px;"
                                + "-fx-text-fill: #ff00ff;"
                                + "-fx-padding: 10 20;"
                                + "-fx-effect: dropshadow(three-pass-box, rgba(255,0,255,0.40), 25, 0.3, 0, 0);"
                )
        );

        startBtn.setOnAction(e -> showGameScreen());

        VBox welcome = new VBox(30, title, startBtn);
        welcome.setAlignment(Pos.CENTER);
        welcome.setStyle("-fx-background-color: black;");
        root.setCenter(welcome);
    }

    // ---------------------- GAME PAGE -------------------------
    private void showGameScreen() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setStyle("-fx-background-color: black;");

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Button btn = new Button();
                btn.setFont(Font.font("Verdana", 36));
                btn.setPrefSize(100, 100);
                btn.setStyle(
                        "-fx-background-color: transparent;"
                                + "-fx-border-color: #00ffea;"
                                + "-fx-border-width: 3px;"
                                + "-fx-text-fill: #00ffea;"
                                + "-fx-effect: dropshadow(three-pass-box, rgba(0,255,234,0.5), 20, 0.3, 0, 0);"
                );

                int rr = r, cc = c;
                btn.setOnAction(e -> handleMove(btn, rr, cc));

                board[r][c] = btn;
                grid.add(btn, c, r);
            }
        }

        status = new Label("Player X's Turn");
        status.setStyle(
                "-fx-font-size: 22px;"
                        + "-fx-text-fill: #ff00ff;"
                        + "-fx-effect: dropshadow(three-pass-box, rgba(255,0,255,0.5), 20, 0.3, 0, 0);"
        );

        Button playAgain = new Button("PLAY AGAIN");
        playAgain.setStyle(
                "-fx-background-color: #ff00ff;"
                        + "-fx-text-fill: black;"
                        + "-fx-font-size: 18px;"
                        + "-fx-padding: 8 20;"
                        + "-fx-background-radius: 8;"
        );

        playAgain.setOnAction(e -> resetGame());

        VBox gameLayout = new VBox(15, status, grid, playAgain);
        gameLayout.setAlignment(Pos.CENTER);
        gameLayout.setStyle("-fx-background-color: black;");
        root.setCenter(gameLayout);
    }

    // ---------------------- GAME LOGIC -------------------------
    private void handleMove(Button btn, int r, int c) {
        if (!btn.getText().isEmpty()) return;

        String mark = playerX ? "X" : "O";
        btn.setText(mark);

        if (checkWin(mark)) {
            status.setText("Winner: " + mark);
            highlightWinner(mark);
            disableBoard();
            return;
        }

        if (isBoardFull()) {
            status.setText("Match Draw!");
            return;
        }

        playerX = !playerX;
        status.setText("Player " + (playerX ? "X" : "O") + "'s Turn");
    }

    private void highlightWinner(String m) {
        for (Button[] row : board) {
            for (Button b : row) {
                if (b.getText().equals(m)) {
                    b.setStyle(
                            "-fx-background-color: #ff00ff;"
                                    + "-fx-text-fill: black;"
                                    + "-fx-font-size: 38px;"
                                    + "-fx-border-color: #ff00ff;"
                                    + "-fx-border-width: 3px;"
                    );
                }
            }
        }
    }

    private boolean checkWin(String m) {
        return rowWin(m) || colWin(m) || diagWin(m);
    }

    private boolean rowWin(String m) {
        for (int r = 0; r < 3; r++)
            if (board[r][0].getText().equals(m) &&
                    board[r][1].getText().equals(m) &&
                    board[r][2].getText().equals(m)) return true;
        return false;
    }

    private boolean colWin(String m) {
        for (int c = 0; c < 3; c++)
            if (board[0][c].getText().equals(m) &&
                    board[1][c].getText().equals(m) &&
                    board[2][c].getText().equals(m)) return true;
        return false;
    }

    private boolean diagWin(String m) {
        return (
                board[0][0].getText().equals(m) &&
                        board[1][1].getText().equals(m) &&
                        board[2][2].getText().equals(m)
        ) || (
                board[0][2].getText().equals(m) &&
                        board[1][1].getText().equals(m) &&
                        board[2][0].getText().equals(m)
        );
    }

    private boolean isBoardFull() {
        for (Button[] row : board)
            for (Button b : row)
                if (b.getText().isEmpty()) return false;
        return true;
    }

    private void disableBoard() {
        for (Button[] row : board)
            for (Button b : row) b.setDisable(true);
    }

    private void resetGame() {
        for (Button[] row : board) {
            for (Button b : row) {
                b.setText("");
                b.setDisable(false);
                b.setStyle(
                        "-fx-background-color: transparent;"
                                + "-fx-border-color: #00ffea;"
                                + "-fx-border-width: 3px;"
                                + "-fx-text-fill: #00ffea;"
                                + "-fx-effect: dropshadow(three-pass-box, rgba(0,255,234,0.5), 20, 0.3, 0, 0);"
                );
            }
        }

        playerX = true;
        status.setText("Player X's Turn");
    }
}
