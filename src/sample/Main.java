package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    private static final Pattern NUM_PATTERN = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = new GridPane();
        primaryStage.setTitle("计算器---wei制作");
        Scene scene = new Scene(root, 390, 330);
        primaryStage.setScene(scene);
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        scene.setRoot(gridPane);
        String[][] buttons = new String[][]{
                {"C", "<", "(", ")"},
                {"+", "7", "8", "9"},
                {"-", "4", "5", "6"},
                {"*", "1", "2", "3"},
                {"/", ".", "0", "="},
        };
        StringBuilder inPutText = new StringBuilder();
        Pane inPane = new HBox();
        inPane.setStyle("-fx-border-color: #000;-fx-border-radius: 2%;-fx-min-height: 50px;");
        Label inLabel = new Label(inPutText.toString());
        inLabel.setStyle("-fx-font-size: 18;-fx-min-width: 390;-fx-min-height: 40");
        inPane.getChildren().add(inLabel);
        gridPane.add(inPane, 0, 0, 4, 1);
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                Button button = new Button(buttons[i][j]);
                button.setMinWidth(90);
                button.setMinHeight(45);
                button.setOnAction(event -> {
                    Button btn = (Button) event.getSource();
                    String btnText = btn.getText();
                    switch (btnText) {
                        case "C":
                            inPutText.setLength(0);
                            break;
                        case "<":
                            if (inPutText.length() == 0) {
                                break;
                            }
                            inPutText.delete(inPutText.length() - 1, inPutText.length());
                            break;
                        case "=":
                            String niBoLanStr = niBoLan(inPutText.toString());
                            BigDecimal callNum = calNiBoLan(niBoLanStr);
                            inPutText.setLength(0);
                            inPutText.append(callNum.toString());
                            break;
                        default:
                            inPutText.append(btn.getText());
                            break;
                    }
                    inLabel.setText(inPutText.toString());
                });
                gridPane.add(button, j, i + 1);
            }
        }
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * 中缀表达式->逆波兰表达式
     *
     * @return
     */
    private String niBoLan(String src) {
        Stack<Character> stack = new Stack<>();
        StringBuilder sb = new StringBuilder();
        char tmp;
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (Character.isDigit(c) || '.' == c) {
                sb.append(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while ((tmp = stack.pop()) != '(') {
                    sb.append(" ");
                    sb.append(tmp);
                }
            } else {
                while (!stack.empty() && getPriority((tmp = stack.peek())) > getPriority(c)) {
                    sb.append(" ");
                    sb.append(tmp);
                    stack.pop();
                }
                sb.append(" ");
                stack.push(c);
            }
        }
        while (!stack.empty()) {
            sb.append(" ");
            sb.append(stack.pop());
        }
        return sb.toString();
    }

    private BigDecimal calNiBoLan(String str) {
        Stack<BigDecimal> numStack = new Stack<>();

        String[] srtArr = str.split(" ");

        for (int i = 0; i < srtArr.length; i++) {
            String c = srtArr[i].trim();
            Matcher matcher = NUM_PATTERN.matcher(c);
            if (matcher.find()) {
                numStack.push(new BigDecimal(c));
            } else {
                BigDecimal b = numStack.pop();
                BigDecimal a = numStack.pop();
                BigDecimal tmp;
                switch (c) {
                    case "+":
                        tmp = a.add(b);
                        break;
                    case "-":
                        tmp = a.subtract(b);
                        break;
                    case "*":
                        tmp = a.multiply(b);
                        break;
                    case "/":
                        tmp = a.divide(b);
                        break;
                    default:
                        tmp = BigDecimal.ZERO;
                        break;
                }
                numStack.push(tmp);
            }
        }
        return numStack.pop();
    }

    private int getPriority(Character op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return 0;
        }
    }
}
