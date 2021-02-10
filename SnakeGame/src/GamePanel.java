import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener{

	static final int SCREEN_WIDTH  = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE	   = 30;
	static final int GAME_UNITS	   = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	static final int DELAY         = 75;// the higher this number, the slower the game

	final int x[] = new int[GAME_UNITS]; // all of the x coordinates of the snake
	final int y[] = new int[GAME_UNITS]; // all of the y coordinates of the snake
	int bodyParts = 6; //Snake will start with 6 parts
	int applesEaten;
	int appleX;			// x coordinate of apple spawned
	int appleY;			// y coordinate of apple spawned
	char direction = 'R'; // [R = right; L = left; U = up; D = down]
	boolean running = false; // game starts as off
	Timer timer;
	Random random;



	GamePanel() {
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));	//Sets up the screen
		this.setBackground(Color.black);									//Sets background to black		
		this.setFocusable(true);											//Makes it grab focus on window creation
		this.addKeyListener(new MyKeyAdapter());							//Allows for user input

		newGame();
	}

	public void newGame() {

		// Upon starting the game, we want to make sure we have all the elements that are required to play in play
		// Call the newApple method to generate a new apple. 
		newApple();
		running = true; // the game is now live
		timer = new Timer(DELAY, this); // Pass in the desired time delay: this is because we are using the ActionListener
		timer.start(); // start the clock
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g) {
		
		if (running) { //Only draw the snake game itself if the game is running
			
			//To make things easier to see in this game, we are going to draw a grid
			for(int i = 0; i < SCREEN_WIDTH/ UNIT_SIZE; i++) {
				g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);  // Draw vertical lines for every unit^2
			}
			for(int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
				g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE); // Draw horizontal lines for every unit^2
			}
	
			g.setColor(Color.RED); //apple color
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE); // draws a red circle at x y point 
	
	
			//Now we need to actually draw the snake
			for(int i = 0; i < bodyParts; i++) {
				if(i == 0) { // If it is the head of the snake
					g.setColor(Color.GREEN);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				} else {
					g.setColor(new Color(45, 180, 0)); // Custom shade of green
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			
			//Draw the current Score
			g.setColor(Color.RED);
			g.setFont(new Font("Ink Free", Font.BOLD, 35));
			FontMetrics metrics = getFontMetrics(g.getFont()); //This lines up the font with the center
			
			String score = "Score: " + applesEaten;
			g.drawString(score, 
					(SCREEN_WIDTH - metrics.stringWidth(score)) / 2, // Center the x coordinate
					g.getFont().getSize());		 // Set the score just below the top of the screen
			
		} else {
			gameOver(g);
		}
	}

	public void newApple() {

		// Generate new coordinates every time this method is called
		appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;


	}

	public void move() {

		//First we make a loop to iterate through all the body parts of the snake
		for (int i = bodyParts; i > 0; i--) {
			x[i] = x[i - 1]; // shift all the coordinates of this array over by one spot
			y[i] = y[i - 1];
		}

		switch(direction) {
		case 'U': // If going up, move the position of the y coordinate -gridSize
			y[0] = y[0] - UNIT_SIZE;
			break;

		case 'D': // If going down, add gridSize to y coordinate
			y[0] = y[0] + UNIT_SIZE; 
			break;

		case 'L': // If going left, - gridSize to x coordinate
			x[0] = x[0] - UNIT_SIZE; 
			break;

		case 'R': // If going right, + gridSize to x coordinate
			x[0] = x[0] + UNIT_SIZE; 
			break;
		}
	}

	public void checkApple() {
		
		//check the coordinates of the apple to that of the snake: if they match, body++, generate new apple
		if ((x[0] == appleX) && (y[0] == appleY)) {
			bodyParts++;
			applesEaten++;
			newApple();
		}
	}

	public void checkCollisions() {

		//First check to see if the snake has run into its own body
		for(int i = bodyParts; i > 0; i--) {
			if ((x[0] == x[i]) && (y[0] == y[i])) { //If this is the case, we have a collision
				running = false; // stop the game
			}
		}

		//Next make sure the snake is not causing a game over by touching the border
		//check left border
		if (x[0] < 0)				{ running = false; }
		//check right border
		if (x[0] > SCREEN_WIDTH) 	{ running = false; }
		//check upper border
		if (y[0] < 0)				{ running = false; }
		//check lower border
		if (y[0] > SCREEN_HEIGHT) 	{ running = false; }

		if (!running) { timer.stop(); }
	}

	public void gameOver(Graphics g) {
		
		// Write "GAME OVER"
		g.setColor(Color.RED);
		g.setFont(new Font("Ink Free", Font.BOLD, 75));
		FontMetrics metrics = getFontMetrics(g.getFont()); //This lines up the font with the center
		g.drawString("GAME OVER", 
				(SCREEN_WIDTH - metrics.stringWidth("GAME OVER")) / 2, // Center the x coordinate
				SCREEN_HEIGHT / 2);									   // Center the y coordinate
		
		
		g.setFont(new Font("Ink Free", Font.BOLD, 45));
		metrics = getFontMetrics(g.getFont()); //This lines up the font with the center
		String score = "" + applesEaten;
		g.drawString(score, 
				(SCREEN_WIDTH - metrics.stringWidth(score)) / 2, // Center the x coordinate
				(SCREEN_HEIGHT / 2) + g.getFont().getSize());		 // Set the score just below the top of the screen
	}




	@Override
	public void actionPerformed(ActionEvent e) { 

		if (running) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint(); // If the game is no longer running, reset the canvas
	}

	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if (direction != 'R') { direction = 'L'; } //Only 90^ turns allowed
				break;
			case KeyEvent.VK_RIGHT:
				if (direction != 'L') { direction = 'R'; } 
				break;
			case KeyEvent.VK_UP:
				if (direction != 'D') { direction = 'U'; } 
				break;
			case KeyEvent.VK_DOWN:
				if (direction != 'U') { direction = 'D'; } 
				break;
			}
		}
	}
}




























