# Advanced Java Calculator

A fully functional calculator built with Java Swing that provides a graphical user interface with advanced features including history tracking, memory functions, and scientific operations.

## Features

### Basic Operations
- **Arithmetic**: Addition (+), Subtraction (-), Multiplication (×), Division (÷)
- **Utility Functions**:
  - Clear (C) - Resets the calculator
  - Sign Change (±) - Toggles positive/negative
  - Percentage (%) - Converts number to percentage
  - Backspace (Back) - Removes the last entered digit
  - Square Root (√) - Calculates square root
  - Square (x^2) - Squares a number
  - Power (x^y) - Raises a number to a power

### Memory Functions
- **MC (Memory Clear)** - Clears the memory
- **MR (Memory Recall)** - Recalls the value stored in memory
- **MS (Memory Store)** - Stores the current display value in memory
- **M+ (Memory Add)** - Adds the current display value to memory
- **M- (Memory Subtract)** - Subtracts the current display value from memory
- Memory indicator shows the current memory value

### Scientific Mode
Toggle between Basic and Scientific modes using the "Scientific Mode" button.

**Trigonometric Functions** (angles in degrees):
- sin - Sine function
- cos - Cosine function
- tan - Tangent function
- asin - Inverse sine (arcsine)
- acos - Inverse cosine (arccosine)
- atan - Inverse tangent (arctangent)

**Logarithmic Functions**:
- log - Base 10 logarithm
- ln - Natural logarithm (base e)
- 10^x - 10 raised to the power of x
- e^x - e raised to the power of x

**Other Scientific Functions**:
- 1/x - Reciprocal (1 divided by x)
- x! - Factorial
- x^3 - Cube function
- pi - Mathematical constant π (3.14159...)
- e - Mathematical constant e (2.71828...)
- Rand - Generates a random number between 0 and 1

### History Panel
- **Calculation History**: Automatically tracks all calculations and results
- **Click to Recall**: Click any history entry to recall its result to the display
- **Clear History**: Button to clear all history entries
- **Auto-Limit**: Keeps the last 100 calculations

### Error Handling
- Division by zero prevention
- Domain error checking for functions (e.g., log of negative numbers)
- Invalid operation detection
- Clear error messages displayed to the user

## How to Compile and Run

### Using Command Line:

1. **Compile the program:**
   ```bash
   javac Calculator.java -encoding UTF-8
   ```

2. **Run the program:**
   ```bash
   java Calculator
   ```

### Using an IDE:

1. Open `Calculator.java` in your IDE (IntelliJ IDEA, Eclipse, NetBeans, etc.)
2. Run the `main` method

## Usage Guide

### Basic Calculations:
1. Click number buttons to enter numbers
2. Click operation buttons (+, -, ×, ÷) to select an operation
3. Enter the second number
4. Click "=" to get the result

### Using Memory:
1. Enter a number
2. Click "MS" to store it in memory
3. Perform other calculations
4. Click "MR" to recall the stored value
5. Use "M+" or "M-" to add/subtract from memory

### Using Scientific Functions:
1. Click "Scientific Mode" to enable scientific mode
2. Enter a number (for functions that need input)
3. Click the desired scientific function button
4. The result will appear immediately and be added to history

### Using History:
1. All calculations are automatically saved to history
2. Click any history entry to recall its result
3. Use "Clear History" to remove all entries

## Requirements

- Java JDK 8 or higher
- Java Swing (included in JDK)

## UI Layout

- **Left Side**: Main calculator interface with display and buttons
- **Right Side**: History panel showing previous calculations
- **Top**: Memory indicator showing current memory value
- **Mode Toggle**: Button to switch between Basic and Scientific modes
- **Scientific Panel**: Appears at the bottom when Scientific Mode is enabled

## Technical Details

- Built with Java Swing for cross-platform GUI
- Uses event-driven programming with ActionListener
- Implements proper error handling and input validation
- Supports both integer and floating-point calculations
- History limited to 100 entries to manage memory usage
- Scientific functions use Java's Math library
