import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Minesweeper implements ActionListener{

	// CUSTOMISABLE 
	private final int boardWidth = 30;
	private final int boardHeight = 16;
	private int mineAmount = 99;
	
	// Square size
	private final int squareSize = 30;
	// Reserve space at the top
	private final int topBarSize = 70;
	
	// Mines
	private int placedMines = 0;
	// Disable a mine if you click on it as the first click of the game
	boolean firstClick = true;
	boolean isGameOn = true;
	
	// Contains all of the JButtons - List of 30 JButtons, 16 times
	private JButton[][] ButtonTable = new JButton[boardHeight][boardWidth];
	// Whether each Button is a mine or not | 0 if empty | 1 if mine | 2 if discovered | 3 if Flagged 
	private int[][] ButtonProperties = new int[boardHeight][boardWidth];
	
	// UI Components
	private JFrame frame;
	private JPanel panel;
	
	private JButton restartButton;
	private JButton flagButton;
	boolean flagMode = false;
	
	public Minesweeper() {
		frame = new JFrame();
		panel = new JPanel();
		panel.setLayout(null);

		restartButton = new JButton("Restart");
		restartButton.setBounds(Math.round((boardWidth * squareSize)*7/8 - 40), Math.round(topBarSize/2 - 15)        ,80,30);
		restartButton.addActionListener(this);
		panel.add(restartButton);
		
		flagButton = new JButton("Flag Mode: Off");
		flagButton.setBounds(Math.round((boardWidth * squareSize)/8 - 60), Math.round(topBarSize/2 - 15)        ,120,30);
		flagButton.addActionListener(this);
		panel.add(flagButton);
		
		
		// Place all JButtons
		createBoard();
		
		// Place Mines
		if (mineAmount < boardWidth * boardHeight) {
			placeMines();
		} else { // Stops there being an infinite loop of placing mines
			mineAmount = (boardWidth * boardHeight) - 8;
			placeMines();
		}
		
		//printTable();
		//displayMines();

		frame.pack();
		Insets insets = frame.getInsets();
        int addedWidth = insets.left + insets.right;
        int addedHeight = insets.top + insets.bottom;
        
		frame.setSize(boardWidth * squareSize + addedWidth, boardHeight * squareSize + topBarSize + addedHeight); 
		frame.add(panel);  
		frame.setTitle("Minesweeper");  
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        frame.setVisible(true);  
	}
	
	
	// Build the board out of JButtons
	public void createBoard() {
		System.out.println(ButtonTable.length);
		System.out.println(ButtonTable[0].length);
	
		for (int i = 0; i<ButtonTable.length;i++) { // Height
			for (int x = 0; x < ButtonTable[i].length; x++) { // Width
				JButton button = new JButton("");
				button.setBounds(x*squareSize,i * squareSize + topBarSize,squareSize,squareSize);
				button.setMargin(new Insets(0, 0, 0, 0));
				button.setFont(new Font("Arial", Font.BOLD, Math.round( squareSize * 0.7f)  ));
				button.setBackground(new Color(220,220,220));
				button.addActionListener(this);
				panel.add(button);
				ButtonTable[i][x] = button;
			}
		}
	}
	
	// Places Mines in random positions
	public void placeMines() {
		Random rand = new Random();

		while (placedMines < mineAmount) {
			int randWidth = rand.nextInt(boardWidth);
			int randHeight = rand.nextInt(boardHeight);

			if (ButtonProperties[randHeight][randWidth] == 0) { // Empty
				ButtonProperties[randHeight][randWidth] = 1; // Place Mine
				placedMines++;
			}
		}
	}

	// Displays mines on screen
	public void displayMines() {
		for (int i = 0; i<ButtonTable.length;i++) { // Height
			for (int x = 0; x < ButtonTable[i].length; x++) { // Width
				if (ButtonProperties[i][x] == 1) {
					ButtonTable[i][x].setText("X");
					//ButtonTable[i][x].setBorder(null);
				}
			}
		}
	}


	// Prints mine locations to console
	public void printTable() {
		for (int i = 0; i<ButtonTable.length;i++) { // Height
			for (int x = 0; x < ButtonTable[i].length; x++) { // Width
				System.out.println(ButtonProperties[i][x]);
			}
		}
	}
	
	
	// Show on each tile how many mines are adjacent
	public void displayAdjacentNumber(int i, int x) {
		JButton clickedButton = ButtonTable[i][x];
		
		int buttonAdjacentMines = 0;
		JButton[] buttonsList = getAdjacentSquares(i,x);
		for (int index = 0; index < buttonsList.length; index++) {
			if (buttonsList[index] == null) {
				continue;
			}
			int xPos = buttonsList[index].getLocation().x;
			int yPos = buttonsList[index].getLocation().y;
			int xIndex = xPos/squareSize;
			int yIndex = (yPos-topBarSize)/squareSize;
			
			if (ButtonProperties[yIndex][xIndex] == 1) {
				buttonAdjacentMines++;
			}
			
		}
		
		// Display number and color
		clickedButton.setBackground(new Color(180,180,180));
		
		if (buttonAdjacentMines > 0) {
			clickedButton.setText(Integer.toString(buttonAdjacentMines));
			clickedButton.setForeground(getColour(buttonAdjacentMines));
		} else {
			// Blank Square, reveal surroundings + adjacent blank squares also reveal surroundings
			clickedButton.setForeground(Color.yellow);
			clickedButton.setText("");
			// Reveal adjacent squares
			for (int index = 0; index < buttonsList.length; index++) {
				if (buttonsList[index] == null) {
					continue;
				}
				int xPos = buttonsList[index].getLocation().x;
				int yPos = buttonsList[index].getLocation().y;
				int xIndex = xPos/squareSize;
				int yIndex = (yPos-topBarSize)/squareSize;
				
				if (ButtonProperties[yIndex][xIndex] == 2) {
					continue;
				}	
				
				ButtonProperties[yIndex][xIndex] = 2;
				displayAdjacentNumber(yIndex, xIndex);
			}
			
			
		}
	}
	
	
	// Get adjacent squares
	public JButton[] getAdjacentSquares(int i, int x) {
		JButton[] buttonsList = new JButton[8];
		int listIndex = 0;
		
		// Left Square
		if (x > 0) {
			buttonsList[listIndex] = ButtonTable[i][x-1];
			listIndex++;
		}
		// Top Left Square
		if (x > 0 && i > 0) {
			buttonsList[listIndex] = ButtonTable[i-1][x-1];
			listIndex++;
		}
		// Top Square
		if (i > 0) {
			buttonsList[listIndex] = ButtonTable[i-1][x];
			listIndex++;
		}
		// Top Right Square
		if (x < boardWidth-1 && i > 0) {
			buttonsList[listIndex] = ButtonTable[i-1][x+1];
			listIndex++;
		}
		// Right Square
		if (x < boardWidth-1) {
			buttonsList[listIndex] = ButtonTable[i][x+1];
			listIndex++;
		}
		// Bottom Right Square
		if (x < boardWidth-1 && i < boardHeight-1) {
			buttonsList[listIndex] = ButtonTable[i+1][x+1];
			listIndex++;
		}
		// Bottom Square
		if (i < boardHeight-1) {
			buttonsList[listIndex] = ButtonTable[i+1][x];
			listIndex++;
		}
		// Bottom Left Square
		if (x > 0 && i < boardHeight-1) {
			buttonsList[listIndex] = ButtonTable[i+1][x-1];
			listIndex++;
		}
		return buttonsList;
	}
	
	
	// Moves the bomb to somewhere else on the board
	public void shuffleBomb(int y, int x) {
		Random rand = new Random();
		
		JButton[] adjacentSquares = getAdjacentSquares(y,x);
		
		boolean hasMoved = false;
		while (hasMoved == false) {
			int randX = rand.nextInt(boardWidth);
			int randY = rand.nextInt(boardHeight);
			boolean allowed = true;
			// Check if new position is not adjacent to the origin
			for (int i = 0; i < adjacentSquares.length;i++) {
				if (adjacentSquares[i] != null) {
					int xPos = adjacentSquares[i].getLocation().x;
					int yPos = adjacentSquares[i].getLocation().y;
					
					int xIndex = xPos/squareSize;
					int yIndex = (yPos-topBarSize)/squareSize;
					if (yIndex == randY && xIndex == randX) {
						allowed = false;
					}
				}
			}
			
			
			if (allowed == true && ButtonProperties[randY][randX] != 1 && randY != y && randX != x) {
				ButtonProperties[randY][randX] = 1;
				ButtonProperties[y][x] = 0;
				hasMoved = true;
			}
			
			
		}
	}
	
	
	// Returns the colours for each number adjacent 
	public Color getColour(int num) {
		Color createdColour;
		if (num == 1) {
			createdColour = new Color(0,0,251);
			return createdColour;
		} else if (num == 2) {
			createdColour = new Color(1,126,0);
			return createdColour;
		} else if (num == 3) {
			createdColour = new Color(235,0,1);
			return createdColour;
		} else if (num == 4) {
			createdColour = new Color(0,1,128);
			return createdColour;
		} else if (num == 5) {
			createdColour = new Color(131,0,3);
			return createdColour;
		} else if (num == 6) {
			createdColour = new Color(0,128,128);
			return createdColour;
		} else if (num == 7) {
			createdColour = new Color(0,0,0);
			return createdColour;
		} else {
			createdColour = new Color(128,128,128);
			return createdColour;
		}
	}
	
	
	// Launch application
	public static void main(String[] args) {
		new Minesweeper();
	}

	
	
	
	
	// When a JButton is pressed
	@Override
	public void actionPerformed(ActionEvent e) {
		// Restart Board
		if (e.getActionCommand() == "Restart") {
			isGameOn = false;
			firstClick = true;
			placedMines = 0;
			ButtonProperties = new int[boardHeight][boardWidth];
			
			for (int i = 0; i<ButtonTable.length;i++) { // Height
				for (int x = 0; x < ButtonTable[i].length; x++) { // Width
					ButtonTable[i][x].setText("");
					ButtonTable[i][x].setForeground(new Color(0,0,0));
					ButtonTable[i][x].setBackground(new Color(220,220,220));
				}
			}
			placeMines();
			isGameOn = true;
		} else if (e.getActionCommand() == "Flag Mode: Off") {
			flagMode = true;
			JButton clickedButton = (JButton) e.getSource();
			clickedButton.setText("Flag Mode: On");
		} else if (e.getActionCommand() == "Flag Mode: On") {
			flagMode = false;
			JButton clickedButton = (JButton) e.getSource();
			clickedButton.setText("Flag Mode: Off");
			
			
		// Click on a square
		} else {		
			JButton clickedButton = (JButton) e.getSource();
			
			int xPos = clickedButton.getLocation().x;
			int yPos = clickedButton.getLocation().y;
			
			int xIndex = xPos/squareSize;
			int yIndex = (yPos-topBarSize)/squareSize;
			
			// Flagging
			if (flagMode == true) {
				if (ButtonTable[yIndex][xIndex].getText() == "F") {
					ButtonTable[yIndex][xIndex].setText("");
					ButtonTable[yIndex][xIndex].setBackground(new Color(220,220,220));
					//ButtonTable[yIndex][xIndex].setForeground(new Color(0,0,0));
				} else if (ButtonProperties[yIndex][xIndex] == 0 || ButtonProperties[yIndex][xIndex] == 1) {
					ButtonTable[yIndex][xIndex].setText("F");
					//ButtonTable[yIndex][xIndex].setForeground(new Color(255,0,0));
					ButtonTable[yIndex][xIndex].setBackground(new Color(255,255,255));
				}
			} else {
			// Clear bombs on first click
				if (firstClick == true) {
					firstClick = false;
					if (ButtonProperties[yIndex][xIndex] == 1) { // If the first click is a bomb
						shuffleBomb(yIndex,xIndex);
					}
					
					// Check if adjacent squares are a bomb, if so: shuffle it
					JButton[] adjacentSquares = getAdjacentSquares(yIndex,xIndex);
					for (int i = 0; i < adjacentSquares.length; i++) {
						if (adjacentSquares[i] == null) {
							continue;
						}
						int axPos = adjacentSquares[i].getLocation().x;
						int ayPos = adjacentSquares[i].getLocation().y;
						int axIndex = axPos/squareSize;
						int ayIndex = (ayPos-topBarSize)/squareSize;

						if (ButtonProperties[ayIndex][axIndex] == 1) {
							shuffleBomb(ayIndex, axIndex);
						}
					}
					
				}

				// Click on Square
				if (isGameOn == true) {
					if (ButtonProperties[yIndex][xIndex] == 0) {
						displayAdjacentNumber(yIndex,xIndex);
						ButtonProperties[yIndex][xIndex] = 2;
					} else if (ButtonProperties[yIndex][xIndex] == 1) {
						System.out.println("BOMB");
						isGameOn = false;
						displayMines();
					}
					// Get adjacent squares when click on a blank square
				}
			}		
		}
	}

}