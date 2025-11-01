import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Calculator extends JFrame implements ActionListener, KeyListener {
    private JTextField display;
    private JList<String> historyList;
    private DefaultListModel<String> historyModel;
    private JLabel memoryIndicator;
    private JLabel angleModeIndicator;
    private JPanel scientificPanel;
    private JPanel rightPanel;
    private JButton modeButton;
    private JButton historyToggleButton;
    private JButton angleModeButton;
    private JButton undoButton;
    private JButton redoButton;
    private JButton exportButton;
    private double firstNumber = 0;
    private String operator = "";
    private boolean isOperatorClicked = false;
    private double memory = 0;
    private boolean scientificMode = false;
    private boolean historyVisible = false;
    private boolean angleModeDegrees = true; // true = degrees, false = radians
    private List<String> calculationHistory = new ArrayList<>();
    private Stack<CalculatorState> undoStack = new Stack<>();
    private Stack<CalculatorState> redoStack = new Stack<>();
    
    // Inner class to store calculator state for undo/redo
    private static class CalculatorState {
        String displayValue;
        double firstNumber;
        String operator;
        boolean isOperatorClicked;
        double memory;
        
        CalculatorState(String display, double first, String op, boolean isOpClicked, double mem) {
            displayValue = display;
            firstNumber = first;
            operator = op;
            isOperatorClicked = isOpClicked;
            memory = mem;
        }
    }
    
    public Calculator() {
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Advanced Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(570, 700);  // Smaller width since history is hidden by default
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Modern color scheme
        Color bgColor = new Color(240, 240, 245);
        Color displayBg = new Color(45, 45, 50);
        
        // Create main display with modern styling
        display = new JTextField("0");
        display.setFont(new Font("Segoe UI", Font.BOLD, 42));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(displayBg);
        display.setForeground(new Color(255, 255, 255));
        display.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 65), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        display.setCaretColor(new Color(100, 150, 255));
        
        // Memory indicator with modern styling
        memoryIndicator = new JLabel("Memory: 0");
        memoryIndicator.setFont(new Font("Segoe UI", Font.BOLD, 13));
        memoryIndicator.setForeground(new Color(100, 150, 255));
        memoryIndicator.setBackground(new Color(230, 240, 255));
        memoryIndicator.setOpaque(true);
        memoryIndicator.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 255), 1),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        
        // Angle mode indicator with modern styling
        angleModeIndicator = new JLabel("Deg");
        angleModeIndicator.setFont(new Font("Segoe UI", Font.BOLD, 13));
        angleModeIndicator.setForeground(new Color(150, 100, 255));
        angleModeIndicator.setBackground(new Color(240, 230, 255));
        angleModeIndicator.setOpaque(true);
        angleModeIndicator.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 180, 255), 1),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        
        // Angle mode toggle button
        angleModeButton = new JButton("Deg/Rad");
        angleModeButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        angleModeButton.addActionListener(e -> toggleAngleMode());
        angleModeButton.setBackground(new Color(150, 100, 255));
        angleModeButton.setForeground(Color.WHITE);
        angleModeButton.setFocusPainted(false);
        angleModeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        angleModeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Undo button
        undoButton = new JButton("Undo");
        undoButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        undoButton.addActionListener(e -> undo());
        undoButton.setBackground(new Color(120, 180, 220));
        undoButton.setForeground(Color.WHITE);
        undoButton.setFocusPainted(false);
        undoButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        undoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Redo button
        redoButton = new JButton("Redo");
        redoButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        redoButton.addActionListener(e -> redo());
        redoButton.setBackground(new Color(120, 180, 220));
        redoButton.setForeground(Color.WHITE);
        redoButton.setFocusPainted(false);
        redoButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        redoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Export history button
        exportButton = new JButton("Export");
        exportButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        exportButton.addActionListener(e -> exportHistory());
        exportButton.setBackground(new Color(100, 200, 150));
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);
        exportButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        exportButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Mode toggle button with modern styling
        modeButton = new JButton("Scientific Mode");
        modeButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        modeButton.addActionListener(e -> toggleScientificMode());
        modeButton.setBackground(new Color(100, 150, 255));
        modeButton.setForeground(Color.WHITE);
        modeButton.setFocusPainted(false);
        modeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        modeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // History panel with modern styling
        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        historyList.setFont(new Font("Consolas", Font.PLAIN, 12));
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.setBackground(new Color(250, 250, 255));
        historyList.setSelectionBackground(new Color(100, 150, 255));
        historyList.setSelectionForeground(Color.WHITE);
        historyList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = historyList.getSelectedValue();
                if (selected != null && selected.contains(" = ")) {
                    String result = selected.split(" = ")[1];
                    display.setText(result);
                }
            }
        });
        JScrollPane historyScroll = new JScrollPane(historyList);
        historyScroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210), 1),
                "Calculation History",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(60, 60, 70)
            ),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        historyScroll.setPreferredSize(new Dimension(280, 450));
        historyScroll.setBackground(bgColor);
        
        // History toggle button with modern styling
        historyToggleButton = new JButton("Show History");
        historyToggleButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        historyToggleButton.addActionListener(e -> toggleHistoryPanel());
        historyToggleButton.setBackground(new Color(150, 200, 100));
        historyToggleButton.setForeground(Color.WHITE);
        historyToggleButton.setFocusPainted(false);
        historyToggleButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        historyToggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Clear history button with modern styling
        JButton clearHistoryButton = new JButton("Clear History");
        clearHistoryButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        clearHistoryButton.setBackground(new Color(255, 180, 180));
        clearHistoryButton.setForeground(new Color(150, 50, 50));
        clearHistoryButton.setFocusPainted(false);
        clearHistoryButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        clearHistoryButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearHistoryButton.addActionListener(e -> {
            historyModel.clear();
            calculationHistory.clear();
        });
        
        // Button panels
        JPanel mainButtonPanel = createMainButtonPanel();
        scientificPanel = createScientificPanel();
        scientificPanel.setVisible(false);
        
        // Control panel for mode and memory with better layout
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        controlPanel.setBackground(bgColor);
        controlPanel.add(modeButton);
        controlPanel.add(historyToggleButton);
        controlPanel.add(angleModeButton);
        controlPanel.add(angleModeIndicator);
        controlPanel.add(memoryIndicator);
        controlPanel.add(undoButton);
        controlPanel.add(redoButton);
        controlPanel.add(exportButton);
        controlPanel.add(clearHistoryButton);
        
        // Main layout with improved spacing
        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setBackground(bgColor);
        leftPanel.add(display, BorderLayout.NORTH);
        leftPanel.add(controlPanel, BorderLayout.CENTER);
        leftPanel.add(mainButtonPanel, BorderLayout.SOUTH);
        leftPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(bgColor);
        rightPanel.add(historyScroll, BorderLayout.CENTER);
        rightPanel.setBorder(new EmptyBorder(15, 0, 15, 15));
        rightPanel.setVisible(historyVisible);  // Hide history panel by default
        
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(bgColor);
        centerPanel.add(leftPanel, BorderLayout.CENTER);
        centerPanel.add(rightPanel, BorderLayout.EAST);
        centerPanel.add(scientificPanel, BorderLayout.SOUTH);
        
        setLayout(new BorderLayout());
        add(centerPanel, BorderLayout.CENTER);
        
        getContentPane().setBackground(bgColor);
        
        // Add keyboard listener
        setFocusable(true);
        addKeyListener(this);
        setFocusTraversalKeysEnabled(false);
    }
    
    private JPanel createMainButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 4, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(35, 35, 40));
        
        // Button labels - updated with memory buttons and modulo
        String[] buttons = {
            "C", "±", "%", "÷",
            "MC", "MR", "MS", "×",
            "M+", "M-", "√", "−",
            "7", "8", "9", "+",
            "4", "5", "6", "Mod",
            "1", "2", "3", "x^y",
            "0", ".", "=", "Back"
        };
        
        for (String text : buttons) {
            JButton button = createButton(text);
            panel.add(button);
        }
        
        return panel;
    }
    
    private JPanel createScientificPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 5, 10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210), 1),
                "Scientific Functions",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(60, 60, 70)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(new Color(35, 35, 40));
        
        String[] scientificButtons = {
            "sin", "cos", "tan", "log", "ln", "1/x",
            "asin", "acos", "atan", "10^x", "e^x", "x!",
            "n√x", "pi", "e", "Rand", "(", ")", "x^3"
        };
        
        for (String text : scientificButtons) {
            JButton button = createScientificButton(text);
            panel.add(button);
        }
        
        return panel;
    }
    
    private void toggleScientificMode() {
        scientificMode = !scientificMode;
        scientificPanel.setVisible(scientificMode);
        modeButton.setText(scientificMode ? "Basic Mode" : "Scientific Mode");
        modeButton.setBackground(scientificMode ? new Color(255, 150, 100) : new Color(100, 150, 255));
        pack();
        setSize(850, scientificMode ? 900 : 700);
    }
    
    private void toggleHistoryPanel() {
        historyVisible = !historyVisible;
        rightPanel.setVisible(historyVisible);
        historyToggleButton.setText(historyVisible ? "Hide History" : "Show History");
        historyToggleButton.setBackground(historyVisible ? new Color(200, 100, 100) : new Color(150, 200, 100));
        pack();
        setSize(historyVisible ? 850 : 570, scientificMode ? 900 : 700);
    }
    
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.addActionListener(this);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Modern color scheme with better contrast
        if (text.equals("C")) {
            button.setBackground(new Color(255, 80, 80));
            button.setForeground(Color.WHITE);
        } else if (text.equals("=")) {
            button.setBackground(new Color(100, 150, 255));
            button.setForeground(Color.WHITE);
        } else if (text.matches("[÷×+−]") || text.equals("Back")) {
            button.setBackground(new Color(255, 150, 50));
            button.setForeground(Color.WHITE);
        } else if (text.matches("[±%]")) {
            button.setBackground(new Color(200, 200, 210));
            button.setForeground(new Color(50, 50, 60));
        } else if (text.equals("Mod")) {
            button.setBackground(new Color(180, 140, 255));
            button.setForeground(Color.WHITE);
        } else if (text.matches("[MC|MR|MS|M\\+|M-]")) {
            button.setBackground(new Color(255, 200, 120));
            button.setForeground(new Color(120, 80, 40));
        } else if (text.matches("[√x\\^2x\\^y]")) {
            button.setBackground(new Color(180, 140, 255));
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(new Color(245, 245, 250));
            button.setForeground(new Color(30, 30, 35));
        }
        
        // Modern border with better depth
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));
        
        // Add hover effect simulation with better pressed state
        button.getModel().addChangeListener(e -> {
            ButtonModel model = (ButtonModel) e.getSource();
            if (model.isPressed()) {
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLoweredBevelBorder(),
                    BorderFactory.createEmptyBorder(10, 0, 10, 0)
                ));
            } else {
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(10, 0, 10, 0)
                ));
            }
        });
        
        return button;
    }
    
    private JButton createScientificButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.addActionListener(this);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBackground(new Color(150, 180, 255));
        button.setForeground(new Color(30, 30, 50));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(8, 0, 8, 0)
        ));
        
        // Add hover effect simulation
        button.getModel().addChangeListener(e -> {
            ButtonModel model = (ButtonModel) e.getSource();
            if (model.isPressed()) {
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLoweredBevelBorder(),
                    BorderFactory.createEmptyBorder(8, 0, 8, 0)
                ));
            } else {
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(8, 0, 8, 0)
                ));
            }
        });
        
        return button;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        if (command.matches("[0-9]")) {
            handleNumber(command);
        } else if (command.equals(".")) {
            handleDecimal();
        } else if (command.matches("[÷×+−]")) {
            handleOperator(command);
        } else if (command.equals("=")) {
            handleEquals();
        } else if (command.equals("C")) {
            handleClear();
        } else if (command.equals("±")) {
            handleSignChange();
        } else if (command.equals("%")) {
            saveState();
            handlePercentage();
        } else if (command.equals("Back")) {
            handleBackspace();
        } else if (command.equals("MC")) {
            handleMemoryClear();
        } else if (command.equals("MR")) {
            handleMemoryRecall();
        } else if (command.equals("MS")) {
            handleMemoryStore();
        } else if (command.equals("M+")) {
            handleMemoryAdd();
        } else if (command.equals("M-")) {
            handleMemorySubtract();
        } else if (command.equals("√")) {
            handleSquareRoot();
        } else if (command.equals("x^2")) {
            handleSquare();
        } else if (command.equals("x^y")) {
            handlePower();
        } else if (command.equals("Mod")) {
            handleModulo();
        } else if (command.equals("n√x")) {
            handleNthRoot();
        } else {
            handleScientificFunction(command);
        }
    }
    
    private void handleNumber(String number) {
        if (isOperatorClicked) {
            display.setText(number);
            isOperatorClicked = false;
        } else {
            String currentText = display.getText();
            if (currentText.equals("0") || isErrorState()) {
                display.setText(number);
            } else {
                display.setText(currentText + number);
            }
        }
    }
    
    private void handleDecimal() {
        String currentText = display.getText();
        if (isOperatorClicked || isErrorState()) {
            display.setText("0.");
            isOperatorClicked = false;
        } else if (!currentText.contains(".")) {
            display.setText(currentText + ".");
        }
    }
    
    private void handleOperator(String op) {
        if (!operator.isEmpty() && !isOperatorClicked) {
            handleEquals();
        }
        
        if (!isErrorState()) {
            firstNumber = Double.parseDouble(display.getText());
            operator = op;
            isOperatorClicked = true;
        }
    }
    
    private void handleEquals() {
        if (operator.isEmpty() || isErrorState()) {
            return;
        }
        
        saveState();
        try {
            double secondNumber = Double.parseDouble(display.getText());
            double result = 0;
            String expression = formatNumber(firstNumber) + " " + operator + " " + formatNumber(secondNumber);
            
            switch (operator) {
                case "+":
                    result = firstNumber + secondNumber;
                    break;
                case "−":
                    result = firstNumber - secondNumber;
                    break;
                case "×":
                    result = firstNumber * secondNumber;
                    break;
                case "÷":
                    if (secondNumber == 0) {
                        display.setText("Error: Division by zero");
                        operator = "";
                        isOperatorClicked = true;
                        return;
                    }
                    result = firstNumber / secondNumber;
                    break;
                case "%":
                    if (secondNumber == 0) {
                        display.setText("Error: Division by zero");
                        operator = "";
                        isOperatorClicked = true;
                        return;
                    }
                    result = firstNumber % secondNumber;
                    break;
            }
            
            addToHistory(expression + " = " + formatNumber(result));
            displayResult(result);
            operator = "";
            isOperatorClicked = true;
        } catch (NumberFormatException ex) {
            display.setText("Error");
            operator = "";
            isOperatorClicked = true;
        }
    }
    
    private void handleClear() {
        saveState();
        display.setText("0");
        firstNumber = 0;
        operator = "";
        isOperatorClicked = false;
    }
    
    private void handleSignChange() {
        String currentText = display.getText();
        if (!currentText.equals("0") && !isErrorState()) {
            if (currentText.startsWith("-")) {
                display.setText(currentText.substring(1));
            } else {
                display.setText("-" + currentText);
            }
        }
    }
    
    private void handlePercentage() {
        try {
            double value = Double.parseDouble(display.getText());
            value = value / 100;
            displayResult(value);
            isOperatorClicked = true;
        } catch (NumberFormatException ex) {
            display.setText("Error");
        }
    }
    
    private void handleBackspace() {
        String currentText = display.getText();
        if (!currentText.equals("0") && !isErrorState()) {
            if (currentText.length() > 1) {
                display.setText(currentText.substring(0, currentText.length() - 1));
            } else {
                display.setText("0");
            }
        }
    }
    
    // Memory functions
    private void handleMemoryClear() {
        memory = 0;
        updateMemoryIndicator();
    }
    
    private void handleMemoryRecall() {
        display.setText(formatNumber(memory));
        isOperatorClicked = true;
    }
    
    private void handleMemoryStore() {
        try {
            memory = Double.parseDouble(display.getText());
            updateMemoryIndicator();
        } catch (NumberFormatException ex) {
            display.setText("Error");
        }
    }
    
    private void handleMemoryAdd() {
        try {
            double value = Double.parseDouble(display.getText());
            memory += value;
            updateMemoryIndicator();
            addToHistory("M+ " + formatNumber(value) + " -> Memory = " + formatNumber(memory));
        } catch (NumberFormatException ex) {
            display.setText("Error");
        }
    }
    
    private void handleMemorySubtract() {
        try {
            double value = Double.parseDouble(display.getText());
            memory -= value;
            updateMemoryIndicator();
            addToHistory("M- " + formatNumber(value) + " -> Memory = " + formatNumber(memory));
        } catch (NumberFormatException ex) {
            display.setText("Error");
        }
    }
    
    private void updateMemoryIndicator() {
        memoryIndicator.setText("Memory: " + formatNumber(memory));
    }
    
    // Basic scientific functions
    private void handleSquareRoot() {
        saveState();
        try {
            double value = Double.parseDouble(display.getText());
            if (value < 0) {
                display.setText("Error: Invalid input");
                return;
            }
            double result = Math.sqrt(value);
            addToHistory("√(" + formatNumber(value) + ") = " + formatNumber(result));
            displayResult(result);
            isOperatorClicked = true;
        } catch (NumberFormatException ex) {
            display.setText("Error");
        }
    }
    
    private void handleSquare() {
        saveState();
        try {
            double value = Double.parseDouble(display.getText());
            double result = value * value;
            addToHistory(formatNumber(value) + "^2 = " + formatNumber(result));
            displayResult(result);
            isOperatorClicked = true;
        } catch (NumberFormatException ex) {
            display.setText("Error");
        }
    }
    
    private void handlePower() {
        if (operator.isEmpty()) {
            saveState();
            firstNumber = Double.parseDouble(display.getText());
            operator = "x^y";
            isOperatorClicked = true;
        } else if (operator.equals("x^y")) {
            try {
                double secondNumber = Double.parseDouble(display.getText());
                double result = Math.pow(firstNumber, secondNumber);
                if (Double.isNaN(result) || Double.isInfinite(result)) {
                    display.setText("Error: Invalid operation");
                    operator = "";
                    isOperatorClicked = true;
                    return;
                }
                addToHistory(formatNumber(firstNumber) + " ^ " + formatNumber(secondNumber) + " = " + formatNumber(result));
                displayResult(result);
                operator = "";
                isOperatorClicked = true;
            } catch (NumberFormatException ex) {
                display.setText("Error");
                operator = "";
                isOperatorClicked = true;
            }
        }
    }
    
    private void handleScientificFunction(String func) {
        if (isErrorState()) {
            return;
        }
        
        saveState();
        try {
            double value = Double.parseDouble(display.getText());
            double result = 0;
            String expression = "";
            
            switch (func) {
                case "sin":
                    result = angleModeDegrees ? Math.sin(Math.toRadians(value)) : Math.sin(value);
                    expression = "sin(" + formatNumber(value) + (angleModeDegrees ? "°" : " rad") + ")";
                    break;
                case "cos":
                    result = angleModeDegrees ? Math.cos(Math.toRadians(value)) : Math.cos(value);
                    expression = "cos(" + formatNumber(value) + (angleModeDegrees ? "°" : " rad") + ")";
                    break;
                case "tan":
                    result = angleModeDegrees ? Math.tan(Math.toRadians(value)) : Math.tan(value);
                    expression = "tan(" + formatNumber(value) + (angleModeDegrees ? "°" : " rad") + ")";
                    break;
                case "asin":
                    if (value < -1 || value > 1) {
                        display.setText("Error: Domain error");
                        return;
                    }
                    result = angleModeDegrees ? Math.toDegrees(Math.asin(value)) : Math.asin(value);
                    expression = "asin(" + formatNumber(value) + ")";
                    break;
                case "acos":
                    if (value < -1 || value > 1) {
                        display.setText("Error: Domain error");
                        return;
                    }
                    result = angleModeDegrees ? Math.toDegrees(Math.acos(value)) : Math.acos(value);
                    expression = "acos(" + formatNumber(value) + ")";
                    break;
                case "atan":
                    result = angleModeDegrees ? Math.toDegrees(Math.atan(value)) : Math.atan(value);
                    expression = "atan(" + formatNumber(value) + ")";
                    break;
                case "log":
                    if (value <= 0) {
                        display.setText("Error: Domain error");
                        return;
                    }
                    result = Math.log10(value);
                    expression = "log(" + formatNumber(value) + ")";
                    break;
                case "ln":
                    if (value <= 0) {
                        display.setText("Error: Domain error");
                        return;
                    }
                    result = Math.log(value);
                    expression = "ln(" + formatNumber(value) + ")";
                    break;
                case "10^x":
                    result = Math.pow(10, value);
                    expression = "10^(" + formatNumber(value) + ")";
                    break;
                case "e^x":
                    result = Math.exp(value);
                    expression = "e^(" + formatNumber(value) + ")";
                    break;
                case "1/x":
                    if (value == 0) {
                        display.setText("Error: Division by zero");
                        return;
                    }
                    result = 1 / value;
                    expression = "1/(" + formatNumber(value) + ")";
                    break;
                case "x!":
                    if (value < 0 || value != (long) value) {
                        display.setText("Error: Invalid input");
                        return;
                    }
                    result = factorial((long) value);
                    expression = formatNumber(value) + "!";
                    break;
                case "x^3":
                    result = value * value * value;
                    expression = formatNumber(value) + "^3";
                    break;
                case "pi":
                    result = Math.PI;
                    expression = "pi";
                    break;
                case "e":
                    result = Math.E;
                    expression = "e";
                    break;
                case "Rand":
                    result = Math.random();
                    expression = "Random";
                    break;
                case "n√x":
                    // This will be handled separately
                    return;
                default:
                    return;
            }
            
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                display.setText("Error: Invalid result");
                return;
            }
            
            addToHistory(expression + " = " + formatNumber(result));
            displayResult(result);
            isOperatorClicked = true;
        } catch (NumberFormatException ex) {
            display.setText("Error");
        }
    }
    
    private long factorial(long n) {
        if (n <= 1) {
            return 1;
        }
        return n * factorial(n - 1);
    }
    
    private void displayResult(double result) {
        if (result == (long) result) {
            display.setText(String.valueOf((long) result));
        } else {
            // Format to avoid scientific notation for small numbers
            if (Math.abs(result) < 1e10 && Math.abs(result) > 1e-4 || result == 0) {
                display.setText(String.format("%.10g", result).replaceFirst("\\.?0+$", ""));
            } else {
                display.setText(String.valueOf(result));
            }
        }
    }
    
    private String formatNumber(double num) {
        if (num == (long) num) {
            return String.valueOf((long) num);
        } else {
            return String.format("%.10g", num).replaceFirst("\\.?0+$", "");
        }
    }
    
    private void addToHistory(String entry) {
        calculationHistory.add(entry);
        historyModel.addElement(entry);
        // Keep only last 100 entries
        if (historyModel.size() > 100) {
            historyModel.remove(0);
            calculationHistory.remove(0);
        }
    }
    
    private boolean isErrorState() {
        String text = display.getText();
        return text.equals("Error") || text.startsWith("Error:");
    }
    
    // Modulo operation handler
    private void handleModulo() {
        saveState();
        if (!operator.isEmpty() && !isOperatorClicked) {
            handleEquals();
        }
        
        if (!isErrorState()) {
            firstNumber = Double.parseDouble(display.getText());
            operator = "%";
            isOperatorClicked = true;
        }
    }
    
    // Nth root handler
    private void handleNthRoot() {
        saveState();
        if (operator.isEmpty()) {
            try {
                double n = Double.parseDouble(display.getText());
                if (n == 0) {
                    display.setText("Error: Cannot take 0th root");
                    return;
                }
                firstNumber = n;
                operator = "n√x";
                isOperatorClicked = true;
                display.setText("Enter x:");
            } catch (NumberFormatException ex) {
                display.setText("Error");
            }
        } else if (operator.equals("n√x")) {
            try {
                double x = Double.parseDouble(display.getText());
                if (x < 0 && firstNumber % 2 == 0) {
                    display.setText("Error: Negative root of even degree");
                    operator = "";
                    isOperatorClicked = true;
                    return;
                }
                if (x == 0 && firstNumber < 0) {
                    display.setText("Error: Invalid operation");
                    operator = "";
                    isOperatorClicked = true;
                    return;
                }
                double result = Math.pow(Math.abs(x), 1.0 / firstNumber);
                if (x < 0 && firstNumber % 2 != 0) {
                    result = -result;
                }
                if (Double.isNaN(result) || Double.isInfinite(result)) {
                    display.setText("Error: Invalid operation");
                    operator = "";
                    isOperatorClicked = true;
                    return;
                }
                addToHistory(firstNumber + "√(" + formatNumber(x) + ") = " + formatNumber(result));
                displayResult(result);
                operator = "";
                isOperatorClicked = true;
            } catch (NumberFormatException ex) {
                display.setText("Error");
                operator = "";
                isOperatorClicked = true;
            }
        }
    }
    
    // Angle mode toggle
    private void toggleAngleMode() {
        angleModeDegrees = !angleModeDegrees;
        angleModeIndicator.setText(angleModeDegrees ? "Deg" : "Rad");
        angleModeIndicator.setBackground(angleModeDegrees ? new Color(240, 230, 255) : new Color(230, 240, 255));
    }
    
    // Save state for undo/redo
    private void saveState() {
        CalculatorState state = new CalculatorState(
            display.getText(),
            firstNumber,
            operator,
            isOperatorClicked,
            memory
        );
        undoStack.push(state);
        if (undoStack.size() > 50) {
            undoStack.remove(0);
        }
        redoStack.clear(); // Clear redo when new operation is performed
    }
    
    // Restore state from CalculatorState
    private void restoreState(CalculatorState state) {
        display.setText(state.displayValue);
        firstNumber = state.firstNumber;
        operator = state.operator;
        isOperatorClicked = state.isOperatorClicked;
        memory = state.memory;
        updateMemoryIndicator();
    }
    
    // Undo operation
    private void undo() {
        if (!undoStack.isEmpty()) {
            CalculatorState currentState = new CalculatorState(
                display.getText(),
                firstNumber,
                operator,
                isOperatorClicked,
                memory
            );
            redoStack.push(currentState);
            if (redoStack.size() > 50) {
                redoStack.remove(0);
            }
            
            CalculatorState previousState = undoStack.pop();
            restoreState(previousState);
        }
    }
    
    // Redo operation
    private void redo() {
        if (!redoStack.isEmpty()) {
            CalculatorState currentState = new CalculatorState(
                display.getText(),
                firstNumber,
                operator,
                isOperatorClicked,
                memory
            );
            undoStack.push(currentState);
            if (undoStack.size() > 50) {
                undoStack.remove(0);
            }
            
            CalculatorState nextState = redoStack.pop();
            restoreState(nextState);
        }
    }
    
    // Export history to file
    private void exportHistory() {
        if (calculationHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No history to export.", "Export History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Calculation History");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String defaultFileName = "calculator_history_" + dateFormat.format(new Date()) + ".txt";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".txt")) {
                filePath += ".txt";
                fileToSave = new File(filePath);
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileToSave))) {
                writer.println("========================================");
                writer.println("Calculator History Export");
                writer.println("Generated: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                writer.println("Total Entries: " + calculationHistory.size());
                writer.println("========================================");
                writer.println();
                
                for (int i = 0; i < calculationHistory.size(); i++) {
                    writer.println((i + 1) + ". " + calculationHistory.get(i));
                }
                
                writer.println();
                writer.println("========================================");
                writer.println("End of History");
                writer.println("========================================");
                
                JOptionPane.showMessageDialog(this, 
                    "History exported successfully to:\n" + filePath, 
                    "Export Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting history:\n" + ex.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Keyboard listener methods
    @Override
    public void keyTyped(KeyEvent e) {
        char keyChar = e.getKeyChar();
        
        if (keyChar >= '0' && keyChar <= '9') {
            handleNumber(String.valueOf(keyChar));
        } else if (keyChar == '.') {
            handleDecimal();
        } else if (keyChar == '+') {
            handleOperator("+");
        } else if (keyChar == '-') {
            handleOperator("−");
        } else if (keyChar == '*') {
            handleOperator("×");
        } else if (keyChar == '/') {
            handleOperator("÷");
        } else if (keyChar == '%') {
            handleModulo();
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        boolean ctrlPressed = e.isControlDown();
        
        if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_EQUALS) {
            handleEquals();
        } else if (keyCode == KeyEvent.VK_BACK_SPACE || keyCode == KeyEvent.VK_DELETE) {
            handleBackspace();
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            handleClear();
        } else if (ctrlPressed && keyCode == KeyEvent.VK_Z) {
            undo();
        } else if (ctrlPressed && keyCode == KeyEvent.VK_Y) {
            redo();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // Not used, but required by KeyListener interface
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
                UIManager.setLookAndFeel(lookAndFeel);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                // Use default look and feel if system L&F fails
                e.printStackTrace();
            }
            new Calculator().setVisible(true);
        });
    }
}
