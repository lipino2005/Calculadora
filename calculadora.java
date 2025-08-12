import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Calculadora extends JFrame implements ActionListener {
    private final JTextField display = new JTextField("0");
    private BigDecimal operand = BigDecimal.ZERO;
    private String operator = "";
    private boolean startNewNumber = true;

    public Calculadora() {
        setTitle("Calculadora");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(320, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(6,6));

        display.setFont(new Font("SansSerif", Font.PLAIN, 28));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(display, BorderLayout.NORTH);

        String[] buttons = {
            "C", "±", "%", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "="
        };

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5,5,5,5);

        int row = 0, col = 0;
        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("SansSerif", Font.BOLD, 18));
            btn.addActionListener(this);

            gbc.gridx = col;
            gbc.gridy = row;
            gbc.gridwidth = text.equals("0") ? 2 : 1; // '0' spans two columns
            panel.add(btn, gbc);

            if (text.equals("0")) {
                col += 2;
            } else {
                col++;
            }
            if (col >= 4) {
                col = 0;
                row++;
            }
        }

        add(panel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Calculadora calc = new Calculadora();
            calc.setVisible(true);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = ((JButton)e.getSource()).getText();
        try {
            if ("0123456789".contains(cmd)) {
                numberPressed(cmd);
            } else if (cmd.equals(".")) {
                decimalPressed();
            } else if (cmd.equals("C")) {
                clearAll();
            } else if (cmd.equals("±")) {
                toggleSign();
            } else if (cmd.equals("%")) {
                percent();
            } else if (cmd.equals("=")) {
                computeResult();
            } else { // operators + - * /
                operatorPressed(cmd);
            }
        } catch (ArithmeticException ex) {
            display.setText("Erro");
            startNewNumber = true;
            operator = "";
            operand = BigDecimal.ZERO;
        }
    }

    private void numberPressed(String digit) {
        if (startNewNumber) {
            display.setText(digit);
            startNewNumber = false;
        } else {
            if (display.getText().equals("0")) display.setText(digit);
            else display.setText(display.getText() + digit);
        }
    }

    private void decimalPressed() {
        if (startNewNumber) {
            display.setText("0.");
            startNewNumber = false;
        } else if (!display.getText().contains(".")) {
            display.setText(display.getText() + ".");
        }
    }

    private void clearAll() {
        display.setText("0");
        operand = BigDecimal.ZERO;
        operator = "";
        startNewNumber = true;
    }

    private void toggleSign() {
        String txt = display.getText();
        if (txt.equals("0")) return;
        if (txt.startsWith("-")) display.setText(txt.substring(1));
        else display.setText("-" + txt);
    }

    private void percent() {
        BigDecimal val = new BigDecimal(display.getText());
        val = val.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP).stripTrailingZeros();
        display.setText(val.toPlainString());
        startNewNumber = true;
    }

    private void operatorPressed(String op) {
        if (!operator.isEmpty() && !startNewNumber) {
            computeResult();
        } else {
            operand = new BigDecimal(display.getText());
        }
        operator = op;
        startNewNumber = true;
    }

    private void computeResult() {
        if (operator.isEmpty()) return;
        BigDecimal current = new BigDecimal(display.getText());
        BigDecimal result = BigDecimal.ZERO;

        switch (operator) {
            case "+" -> result = operand.add(current);
            case "-" -> result = operand.subtract(current);
            case "*" -> result = operand.multiply(current);
            case "/" -> {
                if (current.compareTo(BigDecimal.ZERO) == 0) throw new ArithmeticException("Divisão por zero");
                result = operand.divide(current, 10, RoundingMode.HALF_UP);
            }
        }

        // cleanup result display
        result = result.stripTrailingZeros();
        display.setText(result.toPlainString());
        operand = result;
        operator = "";
        startNewNumber = true;
    }
}
