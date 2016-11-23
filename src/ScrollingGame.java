//import java.awt.Color;
import java.awt.event.KeyEvent;

import java.util.Random;

public class ScrollingGame

{
	/* set to false to use your code, true will run in DEMO mode (using DemoGame.class) */
	private static final boolean DEMO = false;

	/* instance variables */
	private int timerClicks;	// used to space animation and key presses
	private long msElapsed;		// game clock: number of ms since start of game
	private int pauseTime;		// to control speed of game

	private GameWindow gw;		// graphical window to display game (the VIEW)
	private int hdim = 10;		// height of window (default = 5)
	private int wdim = 20;		// width of window (default = 10)
	private int urow = 0;		// row where user is located (default = 0)
	
	private int points = 0;
	private int life;
	private int maxPoints;
	
	
	private Location userLocation = new Location(urow,0); //init user position
	private PlayerPosition[] cellLocations = new PlayerPosition[50];
	//private int cellIndex = 0;
	
	private String[] enemyArray = {"PinkGhost.gif", "RedGhost.gif", "OrangeGhost.gif", "GreenGhost.gif"};
	private String[] getArray = {"PmanEats.gif", "apple.gif", "Grapes.gif", "Cherry.gif", "Peach.gif", "Strawberry.gif"};
	
	
	
	
	/* default constructor; just uses default values for game attributes above */
	public ScrollingGame() {
		init();
	}

	/* alternate constructor; allows specification of the window dimensions and user starting row */
	public ScrollingGame(int hdim, int wdim, int urow) {
		this.hdim = hdim;
		this.wdim = wdim;
		this.urow = urow;
		init();
	}

	/* finish setting up the game state */
	private void init()
	{
		/* initialize the game window

				NOTE: look at the various constructors to see what you can do!
				For example:
					gw = new GameWindow(hdim, wdim, Color.MAGENTA);
		*/
		gw = new GameWindow(hdim, wdim);
		String level = gw.showInputDialog("Easy or Hard?? Make a choice!: " + " 'e' or 'h' " );
		
		
		
		if (level.equals("e")) {
			life = 10;
			maxPoints = 100;
			pauseTime = 100;
		}
		else if (level.equals("h")) {
			life = 5;
			maxPoints = 300;
			pauseTime= 50;
		}
		
		else { //if the user does not input "h" or "e"
			init();
		}
		/* initialize other aspects of game state */
		
		// animation settings
		timerClicks = 0;
		msElapsed = 0;
		

		// initial user position
		
		gw.setImage(userLocation, "PacManEater.gif");
		
	}

	public void play()
	{
		/* main game loop */
		while (!isGameOver())							// if the game isn't over...
		{
			gw.pause(pauseTime);						// pause for some time (helps with animation)
			msElapsed += pauseTime;						// count the total amount of time elapsed
			timerClicks++;								// increment the timer count

			handleKeyPress();							// update state based on user key press

			if (timerClicks % 3 == 0)					// if it's the third timer tick...
			{
				scrollLeft();							// scroll the game left by one column
				populateRightEdge();					// fill in the right column
			}
			updateTitle();								// update the title of the window
		}
	}

	/* responds to key presses by the user */
	private void handleKeyPress()
	{
		int key = gw.checkLastKeyPressed();
		//boolean paused = false;
	    /* use Java constant names for key presses
	       https://docs.oracle.com/javase/8/docs/api/java/awt/event/KeyEvent.html */

	    // Q for quit
		if (key == KeyEvent.VK_Q){
			System.exit(0);
		}
		if (key == KeyEvent.VK_UP){
			//pacman moves up 
			if (urow > 0) {
				gw.setImage(userLocation, null); //erasing the user technically
				urow --;
				userLocation = new Location(urow,0); //set new location of the user
				for(int i=0; i<cellLocations.length;i++) {
					if (cellLocations[i] != null) {
						if (userLocation.equals(cellLocations[i].getLoc())) {
							collide(cellLocations[i]);
							isGameOver();
							cellLocations[i] = null;
							//System.out.println("this works");
							gw.setImage(userLocation,"PacManEater.gif");
							
					}
				}
				}
				gw.setImage(userLocation,"PacManEater.gif");
				
			}
		}
		if (key == KeyEvent.VK_DOWN) {
			//pacman moves down
			if (urow < hdim-1) { /// hdim-1 because hdim is technically out of the window. 
				//System.out.print("DFA");// testing  
				gw.setImage(userLocation, null);  //erase the previous image of user. 
				urow++;
				userLocation = new Location(urow, 0); //set the new location of user
				for(int i=0; i<cellLocations.length;i++) {
					if (cellLocations[i] != null) {
						if (userLocation.equals(cellLocations[i].getLoc())) {
							collide(cellLocations[i]);
							//System.out.println("yes it works");
							cellLocations[i] = null;
							isGameOver();
							gw.setImage(userLocation,"PacManEater.gif");
					}
				}
				gw.setImage(userLocation,"PacManEater.gif");
				
			}
		}
		}
		if (key == KeyEvent.VK_P) {
			while(gw.checkLastKeyPressed() != KeyEvent.VK_P) {
				gw.pause(pauseTime);
				
			}
			
		}
		
		if (key==KeyEvent.VK_COMMA) {
			if(pauseTime>0) {
				pauseTime += 5;
			}
		}
		if (key==KeyEvent.VK_PERIOD){
			if(pauseTime>0) {
				pauseTime -= 5;
			}
		}
		
    	/* use the 'T' key to help you with implementing speed up/slow down/pause
    	   this prints out a debugging message */
		if (key == KeyEvent.VK_T) {
			boolean interval = (timerClicks % 3 == 0);
			System.out.println("pauseTime " + pauseTime + " msElapsed reset " + msElapsed 
				+ " interval " + interval);
		}
	}

	/* updates the game state to reflect adding in new cells in the right-most column */
	private void populateRightEdge() {
		for (int i=0; i<cellLocations.length; i++) {
			
			if (cellLocations[i] == null) { // look through for empty spaces and fill them up... 
				
				if (i%2==0) {
					int rnd = new Random().nextInt(enemyArray.length);
					cellLocations[i] = new PlayerPosition(enemyArray[rnd],(int)(Math.random() * hdim + 0),wdim-1);
					gw.setImage(cellLocations[i].getLoc(), cellLocations[i].getImageFileName());
				}
				else {
					int rnd = new Random().nextInt(getArray.length);
					cellLocations[i] = new PlayerPosition(getArray[rnd],(int)(Math.random() * hdim + 0),wdim-1);
					gw.setImage(cellLocations[i].getLoc(), cellLocations[i].getImageFileName());
				}
				break;
			}
			
		}
			
			
	}

	

	/* updates the game state to reflect scrolling left by one column */
	private void scrollLeft() {
	// shift all the cells to the left 
		for(int i = 0; i<cellLocations.length;i++){   //infinite loop for some reason...
			if (cellLocations[i] != null) {
				//System.out.print(cellLocations.length);
				
				gw.setImage(cellLocations[i].getLoc(), null); //delete image.
				//System.out.print("test
				//System.out.print(cellLocations[i].getCol());
				
				if(cellLocations[i].getCol()-1 != -1) {  //if the cell is not at the left edge 
					cellLocations[i].setCol(cellLocations[i].getCol()-1);  //update the col by -1
					if (cellLocations[i].getLoc().equals(userLocation)) { //case of collision with user 
						collide(cellLocations[i]);
						gw.setImage(userLocation,"PacManEater.gif");
						// if life ==0, GAME ENDS 
						cellLocations[i] = null;  
						isGameOver();

						
						//want to know what gif to put in
					} else {
						//System.out.print(cellLocations[i].getCol());
						gw.setImage(cellLocations[i].getLoc(), cellLocations[i].getImageFileName());
					}
				}
				else {
					cellLocations[i] = null; // the cell is outside of the grid make the location to null. 
				}
			} else {
				//System.out.println("BReAK TEST");
				//break;
		
			}
			
		}
	}
	
	private void collide(PlayerPosition pos) {
		String image = pos.getImageFileName();
		switch (image) {  // make sure to end the game if life is 0.
		case "GreenGhost.gif":
			//decrease life, decrease points...
			life --;
			points -= 2;
			System.out.print(life);
			break;
		case "RedGhost.gif":
			life--;
			points -= 5;
			System.out.print(life);
			break;
		case "OrangeGhost.gif":
			life--;
			points -= 4;
			System.out.print(life);
			break;
		case "PinkGhost.gif":
			life--;
			points -= 3;
			System.out.print(life);
			break;
			
		case "PmanEats.gif":
			//increase points
			points+= 3;
			break;
		case "apple.gif":
			//increase points
			points += 5;
			break;
		case "Cherry.gif":
			//increase points
			points += 5;
			break;
		case "Grapes.gif":
			//increase points
			points += 5;
			break;
		case "Peach.gif":
			//increase Points
			points += 5;
			break;
		case "Starwberry.gif":
			//increase points
			points += 5;
			break;
		}
	}

	/* returns the "score" of the game */
	private int getScore()
	{ 
		return points;
	}
	
	private int getLife(){
		return life;
	}

	/* updates the title bar of the game window */
	private void updateTitle()
	{
		// currently this displays the score
		gw.setTitle("Scrolling Game:  " + getScore() + " Life Remaining:  " + getLife());
	}

	/* returns true if the game is finished, false otherwise
	   this is used by play() to terminate the main game loop */
	public boolean isGameOver()
	{
		if (life<=0) {
			
			gw.showMessageDialog("You have no more lives! Your score:  " + getScore());
			String playAgain = gw.showInputDialog("You have no more lives! Your score:  " + getScore() + " Would you like to play again?? " + " 'y'/'n' " );
			if (playAgain.equals("y")) {
				init();
				return false;
			}
			else {
				gw.showMessageDialog("Thanks for playing!! Bye!");
				System.exit(1);
				return true;
			
		}
		}
		else if (points >= maxPoints) {
			
			String playAgain = gw.showInputDialog("Congratulations!! You WIN!!" + " Would you like to play again?? " + " 'y'/'n' " );
			if (playAgain.equals("y")) {
				init();
				return false;
			}
			else {
				gw.showMessageDialog("Thanks for playing!! Bye!");
				System.exit(1);
				return true;
			}
		}
		
		else{
			return false;
		}
	}

	/* main function that loads & starts the game */
	public static void main(String[] args) {
		if (DEMO) {
			System.out.println("Running the demo: DEMO = " + DEMO);

			/* initialized using the default constructor */
			(new ScrollingGame()).play();

			/* initialized to a given size & starting row using a different constructor */
			// (new DemoGame(10, 20, 4)).play();
		}

		else {
			System.out.println("Running the project code: DEMO = " + DEMO);

			(new ScrollingGame()).play();
			// (new ScrollingGame(10, 20, 4)).play();
		}

	}
}