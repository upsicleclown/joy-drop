/*
    JoyDrop
    A falling game full of fun :D
    Dodge birds and collect coins!
    by Ruxandra Stanciu and Adrian Sinn
*/
import java.applet.Applet;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.*;
import java.applet.AudioClip;
import java.awt.image.BufferedImage;
public class CPTFinal extends Applet implements MouseListener, KeyListener, ActionListener
{
    /*NOTE FOR MR AUGUSTINE:
	    We sincerely apologize for the sheer amount of global variables but,
      keeping in mind the ever-recalled nature of the paint method, we found
      it to be nearly impossible to declare/initialize variables without
      having the values reset everytime when they were intialized at the
      beginning of a method. The variable would reset to the initialized value
      everytime the method was called rather than increase
      steadily or based on other factors like we needed it to. Or, it would say
      the dreaded "this variable cannot be accessed here without having been
      definitely assigned a value" This having been said, all variables declared
      and/or initialized globally were carefully studied and it was concluded
      that these variables would be either extremely inconvenient or impossible
      to use when declared locally.
					  Will Never Do This In Non-Applet Projects,
								 Ruxandra and Adrian*/
    //double buffering objects
    Graphics buffGraph;
    BufferedImage offscreen;
    //image declaration
    Image arrowKeys, title;
    //button declaration
    Button[] levelButtons = new Button [5];
    Button play, stop;
    //audioclip delaraction
    AudioClip music, coinSound, birdSound, loseSound, winSound, goodCoinSound, badCoinSound, goodPowerUp, badPowerUp;
    //important screen and gameplay variable declaration + initializiation
    int timesReach, levelOnScreen = 0, dy = 3, timesRun = 0, previousLevel = 1, drawUnlocked = -1, totalCoins = 0, levelBlockCounter, levelCongratulations;
    boolean drawMenuAgain = false, hitTrampThisLevel = false, levelBlock = false;
    boolean[] hitTramp = {false, false, false, false, false};
    boolean[] levelAlreadyUnlocked = {false, false, false, false};
    //boolean[] levelAlreadyUnlocked = {true, true, true, true};
    //boolean[] hitTramp = {true, true, true, true, true};
    //monster variable declaration
    int xMoveMonster = 475, yMoveMonster = 75;
    Color monsterBody = new Color (218, 112, 214), monsterYellow = new Color (242, 219, 73);
    //gameplay item variable declaration:
    //background
    int[] xMoveCloud, yMoveCloud;
    Color blueBackground = new Color (135, 206, 250);
    //coins
    int[] xMoveCoin = new int [5], yMoveCoin = new int [5];
    Color backCoin = new Color (236, 214, 10), frontCoin = new Color (236, 250, 16);
    int coinCounter;
    boolean[] coinHit = new boolean [5];
    //birds
    int xBird, yBird, dXBirdL = 10, dXBirdR = -10, sideSpawn, birdColorCounter = 0, birdPlace = 0, birdHit = 0, birdSlower = 0;
    Color blueBird = new Color (30, 144, 255);
    int xBigUglyBird = -500, yBigUglyBird = (int) (250 * Math.random () + 50), dxBigUglyBird = 50;
    //trampoline
    int xTrampoline = 400, yTrampoline = 1000;
    Color grass = new Color (50, 205, 50);
    //powerups
    int yPowerUp, xPowerUp, randomPower = 0, powerUpTimer = 0, dCoin = 1, dSpeed = 0, dSpeedBird = 0, lengthPowerUp = 0;
    boolean[] powerHitSpecific = new boolean [7];
    boolean powerUpHit = false, invincibility = false;
    Color redCoin = new Color (220, 0, 100), blueCoin = new Color (0, 100, 200);
    //menu and other decorative item variables
    int xHeart = 875, yHeart = 65;
    int xSmallCoin = 410;
    int powerFar = 0;
    //various font declarations
    Font key = new Font ("Comic Sans MS", Font.BOLD, 25);
    Font doubleCoinFont = new Font ("Comic Sans MS", Font.BOLD, 10);
    Font price = new Font ("Comic Sans MS", Font.BOLD, 20);
    int[] xLock, xBeakL, yBeakL, xBeakR, yBeakR;
    public void init ()
    {
	//initialization of objects used for double buffering
	offscreen = new BufferedImage (1000, 800, BufferedImage.TYPE_INT_ARGB);
	buffGraph = offscreen.getGraphics ();
	//initialization of audioclips
	music = getAudioClip (getCodeBase (), "audio1.wav");
	coinSound = getAudioClip (getCodeBase (), "coinSound.wav");
	birdSound = getAudioClip (getCodeBase (), "birdSound.wav");
	loseSound = getAudioClip (getCodeBase (), "loseSound.wav");
	winSound = getAudioClip (getCodeBase (), "winSound.wav");
	goodCoinSound = getAudioClip (getCodeBase (), "goodCoinSound.wav");
	badCoinSound = getAudioClip (getCodeBase (), "badCoinSound.wav");
	goodPowerUp = getAudioClip (getCodeBase (), "goodPowerUp.wav");
	badPowerUp = getAudioClip (getCodeBase (), "badPowerUp.wav");
	this.setLayout (null);
	addKeyListener (this);
	setFocusable (true);
	buttonsInit ();
	//initialization of images
	arrowKeys = getImage (getCodeBase (), "arrowKeys.jpg");
	title = getImage (getCodeBase (), "joyDrop.jpg");
    } //end of init method


    //initializes buttons
    public void buttonsInit ()
    {
	//level buttons
	for (int i = 0 ; i < 5 ; i++)
	{
	    levelButtons [i] = new Button ("Level " + (i + 1));
	    levelButtons [i].setName ("btnL" + (i + 1));
	    levelButtons [i].setBounds (260 + (i * 100), 350, 80, 50);
	    add (levelButtons [i]);
	    levelButtons [i].addMouseListener (this);
	}
	//music buttons
	play = new Button ("Play Music");
	play.setBounds (50, 50, 80, 50);
	add (play);
	play.addActionListener (this);
	stop = new Button ("Stop Music");
	stop.setBounds (150, 50, 80, 50);
	add (stop);
	stop.addActionListener (this);
    } //end of buttonsInit method


    //removes buttons
    public void buttonsRemove (int level)
    {
	//levelOnscreen is set everytime the buttons are removed
	//therefore they are combined into one
	levelOnScreen = level;
	previousLevel = levelOnScreen;
	remove (stop);
	remove (play);
	for (int i = 0 ; i < 5 ; i++)
	{
	    remove (levelButtons [i]);
	}
    } //end of buttonsRemove method


    //resets necessary variables so that the player can play the next game
    public void reset ()
    {
	timesRun = 0;
	birdHit = 0;
	randomPower = 0;
	xMoveMonster = 450;
	yMoveMonster = 75;
	dy = 3;
	dXBirdL = 10;
	dXBirdR = -10;
	coinCounter = 0;
	yPowerUp = (int) (500 * Math.random () + 2000);
	for (int i = 0 ; i < 7 ; i++)
	{
	    powerHitSpecific [i] = false;
	}
	xTrampoline = (int) (((6 * Math.random ()) * 100) + 200);
	yTrampoline = 1000;
	hitTrampThisLevel = false;
	powerFar = 0;
    } //end of reset method


    /*repaint() calls to the update method which clears the screen and then calls paint()
    overriding the update method to stop clearing the screen: able to
    implement double buffering in order to stop game from flickering*/
    public void update (Graphics g)
    {
	paint (g);
    } //end of update method


    public void paint (Graphics g)
    {
	//draws everything in the animation method on a seperate graphics object
	//takes that image and pastes it onto the screen, on top of the previous
	animation (buffGraph);
	g.drawImage (offscreen, 0, 0, null);
    } //end of paint method


    public void animation (Graphics g)
    {
	//background colour declaration + init
	Color redBackground = new Color (220, 80, 100);
	Color pinkBackground = new Color (255, 160, 200);
	Color purpleBackground = new Color (178, 170, 250);
	Color orangeBackground = new Color (255, 160, 10);
	Color[] backgrounds = {blueBackground, orangeBackground, redBackground, pinkBackground, purpleBackground};
	//draws menu screen
	if (levelOnScreen == 0)
	{
	    totalCoins += coinCounter;
	    reset ();
	    g.setColor (Color.white);
	    g.fillRect (0, 0, 1000, 800);
	    g.drawImage (title, 100, 25, null);
	    //makes sure to redraw the buttons
	    if (drawMenuAgain)
	    {
		buttonsInit ();
		drawMenuAgain = false;
	    }
	    //draws locks, prices and total coins
	    g.setFont (price);
	    for (int i = 0 ; i < 4 ; i++)
	    {
		lock (g, i);
		g.drawString (15 + (i * 15) + "", xLock [i] - 10, 425);
		smallCoin (g);
		xSmallCoin += 100;
	    }
	    g.setFont (key);
	    g.setColor (backCoin);
	    g.drawString ("Coins: " + totalCoins, 775, 70);
	    xSmallCoin = 410;
	    //displays a popup if coins are insufficient
	    if (levelBlock)
	    {
		popUp (g);
	    }
	    else
	    {
		tutorial (g);
		g.drawImage (arrowKeys, 450, 570, null);
	    }
	} //end of menu screen
	//draws playing screen
	else if (levelOnScreen > 0)
	{
	    //gives the level a length
	    timesReach = levelOnScreen * 500;
	    //draws the screen if u have enough lives
	    if (birdHit < 3)
	    {
		//draws background
		g.setColor (backgrounds [levelOnScreen - 1]);
		g.fillRect (0, 0, 1000, 800);
		drawCloud (g);
		//draws the trampoline at the end of level
		if (timesRun > timesReach)
		{
		    drawTrampoline (g);
		    g.setColor (Color.red);
		    g.setFont (doubleCoinFont);
		    g.setColor (Color.red);
		    g.drawString ("WARNING:", 10, 50);
		    g.drawString ("YOU ARE ABOUT TO LAND.", 10, 75);
		    g.drawString ("YOUR UP AND DOWN KEYS ARE DISABLED.", 10, 100);
		    g.drawString ("NOW, MAKE SURE TO LAND ON THE TRAMPOLINE OR YOU LOSE!!", 10, 125);
		}
		else
		{
		    powerBar (g);
		}
		//draws a special enemy ;) for level 5
		if (levelOnScreen == 5 && timesRun > 2000 && timesRun < timesReach)
		{
		    drawBigUglyBird (g);
		}
		drawCoin (g);
		randomPowerUp (g);
		drawBird (g);
		g.setFont (key);
		//draws lives/coins/in-game data
		for (int i = 0 ; i < (3 - birdHit) ; i++)
		{
		    drawHeart (g);
		    xHeart += 25;
		}
		xHeart = 875;
		g.drawString ("LIVES: ", 775, 75);
		g.setColor (backCoin);
		g.drawString ("COINS: " + coinCounter, 775, 40);
		drawMonster (g);
	    }
	    else //youve lost all your lives
	    {
		levelOnScreen = -1;
	    }
	    delay (100);
	    timesRun++;
	    //changes the speed ever 100 times the animation method is called
	    if (timesRun % 100 == 0)
	    {
		dy++;
		dXBirdL += 2;
		dXBirdR -= 2;
	    }
	}
	//draws the you win/lose screen (when levelOnScreen = -1)
	else
	{
	    levelEnd (g);
	}
	repaint ();
    } //end of animation method


    //draws the you win/lose screen
    public void levelEnd (Graphics g)
    {
	Font congratulations = new Font ("Copperplate Gothic", Font.BOLD, 45);
	g.setColor (Color.white);
	g.fillRect (0, 0, 1000, 800);
	g.setColor (Color.black);
	g.setFont (key);
	g.drawRect (675, 460, 180, 100);
	g.drawRect (150, 460, 180, 100);
	g.drawString ("[space bar]", 700, 500);
	g.drawString ("Restart", 725, 535);
	g.drawString ("[escape]", 190, 500);
	g.drawString ("Menu", 205, 535);
	g.setFont (congratulations);
	if (hitTrampThisLevel)
	{
	    winSound.play ();
	    g.drawString ("You Win!", 420, 300);
	    g.setFont (key);
	    g.drawString ("Level " + previousLevel, 475, 350);
	}
	else
	{
	    loseSound.play ();
	    g.drawString ("You Lose", 420, 300);
	}
	xMoveMonster = 450;
	yMoveMonster = 450;
	drawMonster (g);
	g.setColor (Color.white); //erases parachute and arms
	g.fillRect (xMoveMonster - 1, yMoveMonster + 45, 10, 40); // draw left arm
	g.fillRect (xMoveMonster + 41, yMoveMonster + 45, 10, 40); // draw right arm
	g.drawLine (xMoveMonster - 1, yMoveMonster + 25, xMoveMonster - 1, yMoveMonster + 45);
	g.drawLine (xMoveMonster + 50, yMoveMonster + 20, xMoveMonster + 50, yMoveMonster + 45);
	g.fillRect (xMoveMonster - 26, yMoveMonster - 77, 102, 53);
	g.drawLine (xMoveMonster - 26, yMoveMonster - 25, xMoveMonster + 1, yMoveMonster + 13);
	g.drawLine (xMoveMonster + 74, yMoveMonster - 25, xMoveMonster + 51, yMoveMonster + 13);
	g.setColor (Color.black); //redraws arms
	g.drawRect (xMoveMonster, yMoveMonster - 19, 9, 36); // draw left arm
	g.drawRect (xMoveMonster + 41, yMoveMonster - 19, 9, 36); // draw right arm
	g.drawArc (xMoveMonster, yMoveMonster - 22, 9, 9, 0, 180); // draw left hand
	g.drawArc (xMoveMonster + 41, yMoveMonster - 22, 9, 9, 0, 180); // draw right hand
	g.setColor (monsterBody);
	g.fillRect (xMoveMonster + 1, yMoveMonster - 18, 8, 35); // fill left arm
	g.fillRect (xMoveMonster + 42, yMoveMonster - 18, 8, 35); // fill right arm
	g.fillArc (xMoveMonster, yMoveMonster - 21, 9, 8, 0, 180); // fill left hand
	g.fillArc (xMoveMonster + 42, yMoveMonster - 21, 9, 8, 0, 180); // fill right hand
	g.setColor (Color.black);
	g.setFont (key);
	g.drawString (coinCounter + "       Collected", 420, 400);
	g.setColor (backCoin);
	g.fillOval (460, 370, 41, 41);
	g.setColor (frontCoin);
	g.fillOval (470, 380, 20, 20);
	g.setColor (Color.black);
	g.setColor (backCoin);
	g.fillRect (477, 387, 5, 5);
	g.setColor (Color.black);
	g.drawRect (477, 387, 5, 5);
	g.drawOval (460, 370, 40, 40);
	g.drawOval (470, 380, 20, 20);
    } //end of levelEnd method


    //draws the direction/instruction screen
    public void tutorial (Graphics g)
    {
	//explains powerups
	g.setFont (doubleCoinFont);
	g.setColor (Color.black);
	g.drawString ("+5 Coins", 85, 550);
	powerUpCoin (g, blueCoin, blueBird, 90, 490);
	g.drawString ("-5 Coins", 215, 550);
	powerUpCoin (g, redCoin, Color.red, 215, 490);
	g.drawString ("Double Coins", 340, 550);
	doubleCoins (g, 350, 500);
	g.setColor (Color.black);
	g.drawString ("Double Speed", 465, 550);
	speedUp (g, 480, 500);
	g.setColor (Color.black);
	g.drawString ("Half Speed", 595, 550);
	slowDown (g, 605, 500);
	g.drawString ("Invincibility", 725, 550);
	invincible (g, 735, 500);
	g.setColor (Color.black);
	g.drawString ("Slow Birds", 855, 550);
	slowBirds (g, 860, 500);
	//instructions
	g.setColor (Color.black);
	g.setFont (price);
	g.drawString ("Use arrow keys to move around", 350, 675);
	g.drawString ("You won't be able to go down too far", 330, 710);
	g.setFont (key);
	g.setColor (monsterBody);
	g.drawString ("Directions:", 450, 480);
	g.drawRect (50, 450, 900, 300);
	g.drawRect (51, 451, 898, 298);
	g.drawRect (52, 452, 896, 296);
    } //end of tutorial method


    //draws the small coins for when you're buyiing a new level
    public void smallCoin (Graphics g)
    {
	int yCoin = 410;
	g.setColor (backCoin);
	g.fillOval (xSmallCoin, yCoin, 20, 20);
	g.setColor (frontCoin);
	g.fillOval (xSmallCoin + 5, yCoin + 5, 10, 10);
	g.setColor (Color.black);
	g.setColor (backCoin);
	g.fillRect (xSmallCoin + 9, yCoin + 9, 2, 2);
	g.setColor (Color.black);
	g.drawRect (xSmallCoin + 9, yCoin + 9, 2, 2);
	g.drawOval (xSmallCoin, yCoin, 20, 20);
	g.drawOval (xSmallCoin + 5, yCoin + 5, 10, 10);
    } //end of smallCoin method


    //draws the popUp for when the player does not have enought coins
    public void popUp (Graphics g)
    {
	g.setColor (Color.black);
	g.drawRect (300, 500, 400, 150);
	g.setFont (price);
	if (levelBlockCounter >= 75 && levelBlockCounter <= 150)
	{
	    g.setColor (Color.black);
	}
	else
	{
	    g.setColor (Color.red);
	}
	g.drawString ("You have insufficient funds to pay ", 325, 535);
	g.drawString ("for the next level replay the last", 325, 570);
	g.drawString ("level to get more coins", 374, 605);
	levelBlockCounter++;
	if (levelBlockCounter == 225)
	{
	    levelBlockCounter = 0;
	    levelBlock = false;
	}
    } // end of popUp method


    //draws the locks for the menu screen ASKADRIAN
    public void lock (Graphics g, int i)
    {
	int yLock = 265, yUnlock = 287, dyUnlock = 11;
	Color darkGrey = new Color (169, 169, 169);
	xLock = new int[]
	{
	    380, 480, 580, 680
	}
	;
	g.setColor (darkGrey);
	g.fillArc (xLock [i], yLock, 50, 60, 345, 210);
	g.setColor (Color.black);
	g.drawArc (xLock [i], yLock, 50, 60, 345, 210);
	g.drawArc (xLock [i] + 8, yLock + 7, 35, 48, 345, 210);
	g.setColor (Color.white);
	g.fillArc (xLock [i] + 8, yLock + 7, 35, 48, 345, 210);
	int[] xLockBody = {xLock [i] + 8, xLock [i] + 43, xLock [i] + 53, xLock [i] + 53, xLock [i] + 46, xLock [i] + 5,
	    xLock [i] - 2, xLock [i] - 2, xLock [i] + 8};
	int[] yLockBody = {yLock + 33, yLock + 33, yLock + 37, yLock + 75, yLock + 79, yLock + 79, yLock + 75,
	    yLock + 37, yLock + 33};
	g.setColor (backCoin);
	g.fillPolygon (xLockBody, yLockBody, 8);
	g.setColor (Color.black);
	g.drawLine (xLock [i] + 8, yLock + 33, xLock [i] + 43, yLock + 33); // top
	g.drawLine (xLock [i] - 2, yLock + 37, xLock [i] + 8, yLock + 33); // top left
	g.drawLine (xLock [i] + 43, yLock + 33, xLock [i] + 53, yLock + 37); // top right
	g.drawLine (xLock [i] - 2, yLock + 37, xLock [i] - 2, yLock + 75); // left
	g.drawLine (xLock [i] + 53, yLock + 37, xLock [i] + 53, yLock + 75); // right
	g.drawLine (xLock [i] + 53, yLock + 75, xLock [i] + 46, yLock + 79); // right lower
	g.drawLine (xLock [i] - 2, yLock + 75, xLock [i] + 6, yLock + 79); // left lower
	g.drawLine (xLock [i] + 5, yLock + 79, xLock [i] + 46, yLock + 79); // bottom
	g.fillOval (xLock [i] + 20, yLock + 48, 10, 10);
	int[] xKey = {xLock [i] + 22, xLock [i] + 28, xLock [i] + 31, xLock [i] + 19};
	int[] yKey = {yLock + 57, yLock + 57, yLock + 63, yLock + 63};
	g.fillPolygon (xKey, yKey, 4);
	if (drawUnlocked >= i)
	{
	    g.setColor (Color.white);
	    g.fillRect (xLock [i] + 42, yUnlock, 9, dyUnlock);
	    g.setColor (Color.black);
	    g.drawLine (xLock [i] + 42, yUnlock, xLock [i] + 50, yUnlock);
	}
    } //end of lock method


    //powerBar shows you how much of the level you have completed
    public void powerBar (Graphics g)
    {
	g.setColor (Color.white);
	g.fillRect (275, 15, 101, 25);
	g.setColor (Color.black);
	g.drawRect (275, 15, 101, 25);
	g.setColor (monsterBody);
	g.fillRect (276, 16, powerFar, 24);
	g.drawString ("LEVEL PROGRESS:", 25, 40);
	if (timesRun % (timesReach / 100.0) == 0)
	{
	    powerFar++;
	}
	g.setFont (price);
	g.setColor (Color.black);
	g.drawString (powerFar + "%", 310, 36);
    } //end of powerBar method


    //draws the hearts for the lives
    public void drawHeart (Graphics g)
    {
	int[] xHearts = new int[]
	{
	    xHeart, xHeart + 16, xHeart + 8
	}
	;
	int[] yHearts = new int[]
	{
	    yHeart, yHeart, yHeart + 12
	}
	;
	g.setColor (Color.red);
	g.fillPolygon (xHearts, yHearts, 3);
	g.fillArc (xHeart, yHeart - 6, 10, 12, 0, 180);
	g.fillArc (xHeart + 7, yHeart - 6, 10, 12, 0, 180);
    } //end of drawHeart method


    //checks if the item and the monster intersect
    public boolean hitbox (int objectX, int objectY, int objectWidth, int objectHeight)
    {
	int bigWidth = 60, bigHeight = 75, bigX = xMoveMonster, bigY = yMoveMonster; //assumes the monster is the largest
	int smallWidth = objectWidth, smallHeight = objectHeight, smallX = objectX, smallY = objectY; //assums the other object is smaller
	if (objectWidth > bigWidth) //in case the object is the bigUglyBird
	{
	    bigWidth = objectWidth;
	    bigHeight = objectHeight;
	    bigX = objectX;
	    bigY = objectY;
	    smallWidth = 60;
	    smallHeight = 75;
	    smallX = xMoveMonster;
	    smallY = yMoveMonster;
	}
	boolean hitboxEntity = (bigX < smallX && smallX < bigX + bigWidth ||
		bigX < smallX + smallWidth && smallX + smallWidth < bigX + bigWidth)
	    &&
	    (bigY < smallY && smallY < bigY + bigHeight ||
		bigY < smallY + smallHeight && smallY + smallHeight < bigY + bigHeight);
	return hitboxEntity;
    } //end of hitbox method


    //draws and moves monster
    public void drawMonster (Graphics g)
    {
	// outline of monster
	g.setColor (Color.black);
	g.drawRect (xMoveMonster - 1, yMoveMonster + 24, 9, 36); // draw left arm
	g.drawRect (xMoveMonster + 41, yMoveMonster + 24, 9, 36); // draw right arm
	g.drawArc (xMoveMonster - 1, yMoveMonster + 54, 9, 9, 180, 180); // draw left hand
	g.drawArc (xMoveMonster + 41, yMoveMonster + 54, 9, 9, 180, 180); // draw right hand
	g.drawRect (xMoveMonster + 14, yMoveMonster + 39, 9, 26); // draw left leg
	g.drawRect (xMoveMonster + 24, yMoveMonster + 39, 9, 26); // draw right leg
	g.drawOval (xMoveMonster + 9, yMoveMonster + 59, 16, 11); // draw left foot
	g.drawOval (xMoveMonster + 24, yMoveMonster + 59, 16, 11); // draw right foot
	// fill monster
	g.setColor (monsterBody);
	g.fillOval (xMoveMonster, yMoveMonster, 51, 51); // fill body
	if (levelOnScreen >= 0)
	{
	    g.fillRect (xMoveMonster, yMoveMonster + 25, 8, 35); // fill left arm
	    g.fillRect (xMoveMonster + 42, yMoveMonster + 25, 8, 35); // fill right arm
	    g.fillArc (xMoveMonster, yMoveMonster + 55, 8, 8, 180, 180); // fill left hand
	    g.fillArc (xMoveMonster + 42, yMoveMonster + 55, 8, 8, 180, 180); // fill right hand
	}
	else //makes it so that the you win/lose screen draws the monster differently than during gameplay
	{
	    g.fillRect (xMoveMonster + 1, yMoveMonster - 18, 8, 35); // fill left arm
	    g.fillRect (xMoveMonster + 42, yMoveMonster - 18, 8, 35); // fill right arm
	    g.fillArc (xMoveMonster, yMoveMonster - 21, 9, 8, 0, 180); // fill left hand
	    g.fillArc (xMoveMonster + 42, yMoveMonster - 21, 9, 8, 0, 180); // fill right hand
	}
	g.fillRect (xMoveMonster + 15, yMoveMonster + 40, 8, 25); // fill left leg
	g.fillRect (xMoveMonster + 25, yMoveMonster + 40, 8, 25); // fill right leg
	// fill feet and parachute
	g.setColor (monsterYellow);
	g.fillOval (xMoveMonster + 10, yMoveMonster + 60, 15, 10); // fill left foot
	g.fillOval (xMoveMonster + 25, yMoveMonster + 60, 15, 10); // fill right foot
	// fill eye and mouth
	g.setColor (Color.white);
	g.fillOval (xMoveMonster + 13, yMoveMonster + 10, 25, 25); // fill eye
	g.fillArc (xMoveMonster + 20, yMoveMonster + 33, 10, 10, 180, 180); // fill mouth
	// outline the rest
	g.setColor (Color.black);
	g.drawOval (xMoveMonster, yMoveMonster, 51, 51); // outline body
	g.fillOval (xMoveMonster + 18, yMoveMonster + 18, 15, 15); // fill pupil
	g.drawOval (xMoveMonster + 12, yMoveMonster + 11, 25, 25); // draw eye
	g.drawArc (xMoveMonster + 19, yMoveMonster + 32, 11, 11, 180, 180); // draw mouth
	g.drawArc (xMoveMonster - 26, yMoveMonster - 76, 101, 101, 0, 180); // draw parachute arc
	g.drawLine (xMoveMonster - 26, yMoveMonster - 25, xMoveMonster + 74, yMoveMonster - 25); // draw parachute bottom
	g.drawLine (xMoveMonster - 26, yMoveMonster - 25, xMoveMonster + 1, yMoveMonster + 13); // draw left parachute
	// string
	g.drawLine (xMoveMonster + 74, yMoveMonster - 25, xMoveMonster + 51, yMoveMonster + 13); // draw right parachute
	// string
	g.setColor (monsterYellow);
	g.fillArc (xMoveMonster - 25, yMoveMonster - 75, 100, 100, 0, 180); // fill parachute
	// draw eye twinkle
	g.setColor (Color.white);
	g.fillOval (xMoveMonster + 20, yMoveMonster + 20, 5, 5);
    } //end of drawMonster method


    //draws and moves clouds
    public void drawCloud (Graphics g)
    {
	if (timesRun == 0)
	{
	    xMoveCloud = new int[]
	    {
		150, 850, 400, 250, 750, 100, 850, 400, 300, 600, 350, 200, 700
	    }
	    ;
	    yMoveCloud = new int[]
	    {
		150, 100, 150, 200, 250, 300, 400, 350, 450, 500, 550, 600, 650
	    }
	    ;
	}
	g.setColor (Color.white);
	for (int i = 0 ; i < 13 ; i++)
	{
	    g.fillOval (xMoveCloud [i], yMoveCloud [i], 100, 30);
	    g.fillArc (xMoveCloud [i] + 25, yMoveCloud [i] - 10, 50, 30, 0, 180);
	    yMoveCloud [i] -= (dy - 2 + dSpeed);
	    yMoveCloud [i] -= (dy - 2);
	    if ((dy - 2 < -3 && dSpeed <= -3) || (dy - 2 > 0 && dSpeed > 0))
	    {
		yMoveCloud [i] -= dSpeed;
	    }
	    if (yMoveCloud [i] < -10 && timesRun < timesReach)
	    {
		yMoveCloud [i] = 810;
	    }
	}
    } //end of drawCloud method


    //draws coins
    public void drawCoin (Graphics g)
    {
	if (timesRun == 0)
	{
	    for (int i = 0 ; i < 5 ; i++)
	    {
		xMoveCoin [i] = (int) ((800 * Math.random ()) + 100);
		yMoveCoin [i] = 820 + (i * 150);
		coinHit [i] = false;
	    }
	}
	for (int i = 0 ; i < 5 ; i++)
	{
	    if (!coinHit [i])   //if coin [i] hasn't been hit it will draw and check their hitboxes
	    {
		//hitbox check
		if (hitbox (xMoveCoin [i], yMoveCoin [i], 40, 40))
		{
		    coinHit [i] = true;
		    coinSound.play ();
		    coinCounter += dCoin;
		    break;
		}
		g.setColor (backCoin);
		g.fillOval (xMoveCoin [i], yMoveCoin [i], 41, 41);
		g.setColor (frontCoin);
		g.fillOval (xMoveCoin [i] + 10, yMoveCoin [i] + 10, 20, 20);
		g.setColor (Color.black);
		g.setColor (backCoin);
		g.fillRect (xMoveCoin [i] + 17, yMoveCoin [i] + 17, 5, 5);
		g.setColor (Color.black);
		g.drawRect (xMoveCoin [i] + 17, yMoveCoin [i] + 17, 5, 5);
		g.drawOval (xMoveCoin [i], yMoveCoin [i], 40, 40);
		g.drawOval (xMoveCoin [i] + 10, yMoveCoin [i] + 10, 20, 20);
	    }
	    yMoveCoin [i] -= dy;
	    //will continue to move the coin even if they haven't been drawn so that they will be timed
	    //for speed power up
	    if ((dy < -3 && dSpeed <= -3) || (dy > 0 && dSpeed > 0))
	    {
		yMoveCoin [i] -= dSpeed;
	    }
	    if (timesRun <= levelOnScreen * 500)
	    {
		//coin reset
		if (yMoveCoin [i] < -39)
		{
		    yMoveCoin [i] = 800;
		    xMoveCoin [i] = (int) ((800 * Math.random ()) + 100);
		    coinHit [i] = false;
		}
	    }
	} //end of coin for loop
    } //end of drawCoin method


    public void drawBird (Graphics g)
    {
	Color redBird = new Color (220, 20, 60), greenBird = new Color (50, 205, 50), pinkBird = new Color (255, 105, 180);
	Color purpleBird = new Color (128, 0, 128), orangeBird = new Color (255, 140, 0);
	Color[] birdColors = {redBird, greenBird, pinkBird, purpleBird, blueBird, orangeBird};
	//draws another bird from a random side when the previous bird has left
	if (birdPlace == 0 && timesRun < timesReach)
	{
	    yBird = yMoveMonster + (500 - timesRun / 5);
	    sideSpawn = (int) (2 * Math.random ());
	    if (sideSpawn == 1) // left side
	    {
		xBird = -55;
	    }
	    else if (sideSpawn == 0)
	    {
		xBird = 1045;
	    }
	    birdPlace = 1;
	}
	if (sideSpawn == 1) // left side
	{
	    drawBirdLeftSide (g, birdColors);
	}
	else // right side
	{
	    drawBirdRightSide (g, birdColors);
	}
	if (!invincibility)
	{
	    if (hitbox (xBird, yBird, 50, 40))
	    {
		birdPlace = 0;
		birdSound.play ();
		birdHit++;
		g.setColor (birdColors [(birdColorCounter + 1) % 6]);
	    }
	}
	if (birdSlower % 2 == 0)
	{
	    if (sideSpawn == 1)
	    {
		xBird += (dXBirdL - dSpeed);
	    }
	    else
	    {
		xBird += (dXBirdR - dSpeed);
	    }
	    yBird -= 10;
	}
	if (xBird < -650 || yBird < -50 || xBird > 1055)
	{
	    birdPlace = 0;
	    birdColorCounter++;
	}
    } //end of drawBird method


    //animates the bird coming from the left side
    public void drawBirdLeftSide (Graphics g, Color[] birdColors)
    {
	// beak
	xBeakL = new int[]
	{
	    xBird + 45, xBird + 45, xBird + 60
	}
	;
	yBeakL = new int[]
	{
	    yBird + 15, yBird + 25, yBird + 20
	}
	;
	g.setColor (Color.yellow);
	g.fillPolygon (xBeakL, yBeakL, 3);
	g.setColor (Color.black);
	g.drawPolygon (xBeakL, yBeakL, 3);
	// body
	g.setColor (birdColors [(birdColorCounter + 1) % 6]);
	g.fillOval (xBird, yBird, 50, 50);
	g.fillArc (xBird - 25, yBird, 75, 50, 180, 180);
	g.setColor (Color.black);
	g.drawArc (xBird, yBird, 50, 50, 0, 180);
	g.drawArc (xBird - 25, yBird, 75, 50, 180, 180);
	g.drawLine (xBird - 25, yBird + 25, xBird, yBird + 25);
	// eye
	g.setColor (Color.white);
	g.fillOval (xBird + 20, yBird + 10, 20, 20);
	g.setColor (Color.black);
	g.drawOval (xBird + 20, yBird + 10, 20, 20);
	g.fillOval (xBird + 30, yBird + 15, 5, 5);
    } //end of drawBirdLeftSide method


    //draws birds coming from the left side
    public void drawBirdRightSide (Graphics g, Color[] birdColors)
    {
	// beak
	xBeakR = new int[]
	{
	    xBird, xBird, xBird - 15
	}
	;
	yBeakR = new int[]
	{
	    yBird + 16, yBird + 25, yBird + 20
	}
	;
	g.setColor (Color.yellow);
	g.fillPolygon (xBeakR, yBeakR, 3);
	g.setColor (Color.black);
	g.drawPolygon (xBeakR, yBeakR, 3);
	// body & head
	g.setColor (birdColors [(birdColorCounter + 1) % 6]);
	g.fillOval (xBird, yBird, 50, 50);
	g.fillArc (xBird - 1, yBird, 75, 50, 180, 180);
	g.setColor (Color.black);
	g.drawArc (xBird, yBird, 50, 50, 0, 180);
	g.drawArc (xBird - 1, yBird, 75, 50, 180, 180);
	g.drawLine (xBird + 50, yBird + 25, xBird + 75, yBird + 25);
	// head
	g.setColor (Color.white);
	g.fillOval (xBird + 10, yBird + 10, 20, 20);
	g.setColor (Color.black);
	g.drawOval (xBird + 10, yBird + 10, 20, 20);
	g.fillOval (xBird + 15, yBird + 15, 5, 5);
    } //end of drawBirdRightSide Method


    //draws the nearly unbeatable bird (level 5)
    public void drawBigUglyBird (Graphics g)
    {
	int[] xBigUglyBirdBigBeak = {xBigUglyBird + 225, xBigUglyBird + 250, xBigUglyBird + 150};
	int[] yBigUglyBirdBigBeak = {yBigUglyBird + 240, yBigUglyBird + 290, yBigUglyBird + 315};
	if (hitbox (xBigUglyBird, yBigUglyBird, 380, 380) && !invincibility) //resets values if you are hit
	{
	    birdHit += 2; //-2 lives evertime you hit him
	    birdSound.play ();
	    xBigUglyBird = -1000;
	    dxBigUglyBird = 50;
	    yBigUglyBird = (int) (250 * Math.random () + 50);
	}
	else //draws if you didnt hit it
	{
	    g.setColor (Color.black);
	    g.drawRect (xBigUglyBird - 1, yBigUglyBird - 1, 401, 401);
	    g.drawArc (xBigUglyBird - 101, yBigUglyBird + 99, 601, 201, 180, 180);
	    g.drawLine (xBigUglyBird - 101, yBigUglyBird + 199, xBigUglyBird + 500, yBigUglyBird + 199);
	    g.setColor (monsterBody);
	    g.fillRect (xBigUglyBird, yBigUglyBird, 400, 400);
	    g.fillArc (xBigUglyBird - 100, yBigUglyBird + 100, 600, 200, 180, 180);
	    g.setColor (Color.white);
	    g.fillOval (xBigUglyBird + 75, yBigUglyBird + 125, 150, 150);
	    g.fillOval (xBigUglyBird + 225, yBigUglyBird + 150, 100, 100);
	    g.setColor (monsterYellow);
	    g.fillPolygon (xBigUglyBirdBigBeak, yBigUglyBirdBigBeak, 3);
	    g.fillRect (xBigUglyBird + 100, yBigUglyBird + 375, 50, 50);
	    g.fillRect (xBigUglyBird + 250, yBigUglyBird + 375, 50, 50);
	    g.setColor (Color.black);
	    g.drawOval (xBigUglyBird + 75, yBigUglyBird + 125, 150, 150);
	    g.drawOval (xBigUglyBird + 225, yBigUglyBird + 150, 100, 100);
	    g.fillOval (xBigUglyBird + 140, yBigUglyBird + 190, 20, 20);
	    g.fillOval (xBigUglyBird + 260, yBigUglyBird + 190, 20, 20);
	    g.drawPolygon (xBigUglyBirdBigBeak, yBigUglyBirdBigBeak, 3);
	    g.drawRect (xBigUglyBird + 100, yBigUglyBird + 375, 50, 50);
	    g.drawRect (xBigUglyBird + 250, yBigUglyBird + 375, 50, 50);
	    g.drawLine (xBigUglyBird + 125, yBigUglyBird + 375, xBigUglyBird + 125, yBigUglyBird + 425);
	    g.drawLine (xBigUglyBird + 275, yBigUglyBird + 375, xBigUglyBird + 275, yBigUglyBird + 425);
	    g.drawLine (xBigUglyBird + 150, yBigUglyBird + 100, xBigUglyBird + 200, yBigUglyBird + 130);
	    g.drawLine (xBigUglyBird + 150, yBigUglyBird + 101, xBigUglyBird + 200, yBigUglyBird + 131);
	    g.drawLine (xBigUglyBird + 225, yBigUglyBird + 150, xBigUglyBird + 275, yBigUglyBird + 120);
	    g.drawLine (xBigUglyBird + 225, yBigUglyBird + 151, xBigUglyBird + 275, yBigUglyBird + 121);
	}
	//moves it back and forth across the screen
	if (xBigUglyBird > 1600 || xBigUglyBird < -1000)
	{
	    dxBigUglyBird = -dxBigUglyBird;
	    yBigUglyBird = (int) (250 * Math.random () + 50);
	}
	xBigUglyBird += dxBigUglyBird;
    } //end of drawBigUglyBird method


    //draws the trampoline
    public void drawTrampoline (Graphics g)
    {
	int xTrampoline = 400;
	Color darkGrey = new Color (169, 169, 169);
	//checks whether or not, when the monster reaches the level of the trampoline, if they hit it
	if (yMoveMonster + 40 > yTrampoline)
	{
	    levelCongratulations = levelOnScreen;
	    levelOnScreen = -1;
	    if (xTrampoline < xMoveMonster && xMoveMonster < xTrampoline + 100)
	    {
		hitTrampThisLevel = true;
		hitTramp [previousLevel - 1] = true;
	    }
	}
	g.setColor (Color.black);
	g.drawLine (0, yTrampoline, 1000, yTrampoline);
	g.setColor (grass);
	g.fillRect (0, yTrampoline - 10, 1000, 800);
	// centre leg
	g.setColor (darkGrey);
	g.fillRect (xTrampoline + 100, yTrampoline + 80, 5, 20);
	g.setColor (Color.black);
	g.drawRect (xTrampoline + 100, yTrampoline + 80, 5, 20);
	int[] xBottom = {xTrampoline + 100, xTrampoline + 98, xTrampoline + 107, xTrampoline + 105};
	int[] yBottom = {yTrampoline + 100, yTrampoline + 106, yTrampoline + 106, yTrampoline + 100};
	g.fillPolygon (xBottom, yBottom, 4);
	// right leg
	g.setColor (darkGrey);
	g.fillRect (xTrampoline + 195, yTrampoline + 40, 5, 25);
	g.setColor (Color.black);
	g.drawRect (xTrampoline + 195, yTrampoline + 40, 5, 25);
	int[] xBottomR = {xTrampoline + 195, xTrampoline + 193, xTrampoline + 202, xTrampoline + 200};
	int[] yBottomSides = {yTrampoline + 65, yTrampoline + 71, yTrampoline + 71, yTrampoline + 65};
	g.fillPolygon (xBottomR, yBottomSides, 4);
	// left leg
	g.setColor (darkGrey);
	g.fillRect (xTrampoline, yTrampoline + 40, 5, 25);
	g.setColor (Color.black);
	g.drawRect (xTrampoline, yTrampoline + 40, 5, 25);
	int[] xBottomL = {xTrampoline, xTrampoline - 2, xTrampoline + 7, xTrampoline + 5};
	g.fillPolygon (xBottomL, yBottomSides, 4);
	// tramp
	g.setColor (Color.blue);
	g.fillOval (xTrampoline, yTrampoline, 200, 80);
	g.setColor (Color.black);
	g.drawOval (xTrampoline, yTrampoline, 200, 80);
	g.drawOval (xTrampoline + 20, yTrampoline + 12, 162, 55);
	g.setColor (Color.white);
	g.fillOval (xTrampoline + 20, yTrampoline + 12, 162, 55);
	//regulates the speed of the trampoline to go with that of the clouds
	if (yTrampoline >= (yMoveMonster + 40))
	{
	    yTrampoline -= (dy - 2);
	}
    } //end of drawTrampoline method


    // decides which powerUp to draw
    public void randomPowerUp (Graphics g)
    {
	Color blueCoin = new Color (0, 100, 200);
	Color redCoin = new Color (220, 0, 100);
	//generates random powerup
	if (randomPower == 0)
	{
	    randomPower = (int) (7 * Math.random () + 1);
	    xPowerUp = (int) (800 * Math.random () + 100);
	    yPowerUp = (int) (500 * Math.random () + 2000);
	}
	//checks if the monster hit the powerup and resets values accordingly
	if (hitbox (xPowerUp, yPowerUp, 40, 40))
	{
	    if (!powerUpHit)
	    {
		yPowerUp = -40;
		powerHitSpecific [randomPower - 1] = true;
		powerUpHit = true;
	    }
	}
	//assigns the randomly generated number to a powerup
	powerUpActions (g, powerHitSpecific);
	//only draws another power up when they aren't using another power up
	if (!powerUpHit)
	{
	    switch (randomPower)
	    {
		case 1:
		    powerUpCoin (g, blueCoin, blueBird, xPowerUp, yPowerUp);
		    //good coin +5
		    break;
		case 2:
		    powerUpCoin (g, redCoin, Color.red, xPowerUp, yPowerUp);
		    //bad coin -5
		    break;
		case 3:
		    doubleCoins (g, xPowerUp, yPowerUp);
		    break;
		case 4:
		    speedUp (g, xPowerUp, yPowerUp);
		    break;
		case 5:
		    slowDown (g, xPowerUp, yPowerUp);
		    break;
		case 6:
		    invincible (g, xPowerUp, yPowerUp);
		    break;
		case 7:
		    slowBirds (g, xPowerUp, yPowerUp);
		    break;
	    } //end of switch statement
	}
	yPowerUp -= (dy + 5 + dSpeed);
	if (timesRun <= levelOnScreen * 450)
	{
	    if (yPowerUp < -39)
	    {
		randomPower = 0;
		//reset
	    }
	}
    } //end of randomPowerup method


    //puts the randomly generated powerup into action
    public void powerUpActions (Graphics g, boolean[] powerHitSpec)
    {
	if (powerHitSpec [0]) //goodCoin
	{
	    //goodCoinSound.play ();
	    coinCounter += 5;
	    resetPowerUpValues (0, true, powerHitSpec);
	}
	else if (powerHitSpec [1]) //badCoin
	{
	    //badCoinSound.play ();
	    if (coinCounter >= 5)
	    {
		coinCounter -= 5;
	    }
	    else
	    {
		coinCounter = 0;
	    }
	    resetPowerUpValues (1, true, powerHitSpec);
	}
	else if (powerHitSpec [2]) //doubleCoins
	{
	    powerUpLength (g, timesRun, 100, "Double Coins");
	    if (powerUpTimer == 0)
	    {
		//goodPowerUp.play ();
		dCoin = 2;
	    }
	    else if (powerUpTimer == 100)
	    {
		dCoin = 1;
		resetPowerUpValues (2, false, powerHitSpec);
	    }
	    powerUpTimer++;
	}
	else if (powerHitSpec [3]) //speedUp
	{
	    
	    powerUpLength (g, timesRun, 100, "Double Speed");
	    if (powerUpTimer == 0)
	    {
		//badPowerUp.play ();
		dSpeed = (int) (dXBirdL * 1.2);
	    }
	    else if (powerUpTimer == 100)
	    {
		dSpeed = 0;
		resetPowerUpValues (3, false, powerHitSpec);
	    }
	    powerUpTimer++;
	}
	else if (powerHitSpec [4]) //slowDown
	{
	    
	    powerUpLength (g, timesRun, 100, "Half Speed");
	    if (powerUpTimer == 0)
	    {
		//goodPowerUp.play ();
		dSpeed = (int) (dXBirdL * -0.4);
	    }
	    else if (powerUpTimer == 100)
	    {
		dSpeed = 0;
		resetPowerUpValues (4, false, powerHitSpec);
	    }
	    powerUpTimer++;
	}
	else if (powerHitSpec [5]) //invincible
	{
	    
	    powerUpLength (g, timesRun, 100, "Invincibility");
	    if (powerUpTimer == 0)
	    {
		//goodPowerUp.play ();
		invincibility = true;
	    }
	    else if (powerUpTimer == 100)
	    {
		invincibility = false;
		resetPowerUpValues (5, false, powerHitSpec);
	    }
	    powerUpTimer++;
	}
	else if (powerHitSpec [6]) //slowBirds
	{
	    
	    powerUpLength (g, timesRun, 75, "Slow Birds");
	    if (powerUpTimer < 75)
	    {
		//goodPowerUp.play ();
		birdSlower++;
	    }
	    else if (powerUpTimer == 75)
	    {
		birdSlower = 0;
		resetPowerUpValues (6, false, powerHitSpec);
	    }
	    powerUpTimer++;
	}
    } //end of the powerUpActions method


    //resets the powerUp values
    public void resetPowerUpValues (int powerHitSpecIndex, boolean coin, boolean[] powerHitSpec)
    {
	if (!coin)
	{
	    powerUpTimer = -1; //decides how long the effect lasts
	}
	lengthPowerUp = 0;
	randomPower = 0;
	powerUpHit = false;
	powerHitSpec [powerHitSpecIndex] = false;
    } //end of resetPowerUpValues


    public void powerUpLength (Graphics g, int timesRun, int maxLength, String powerUpName)
    {
	if (lengthPowerUp == 0)
	{
	    lengthPowerUp = timesRun;
	}
	timesRun -= lengthPowerUp;
	g.setColor (Color.white);
	g.setFont (key);
	g.drawString (powerUpName + "", 780, 110);
	g.fillRect (799, 124, maxLength + 2, 26);
	g.setColor (Color.black);
	g.drawRect (799, 124, maxLength + 2, 26);
	g.setColor (monsterYellow);
	g.fillRect (800, 125, timesRun, 25);
    } //end of powerUpLength method


    //draws goodCoin (+5) or badCoin (-5)
    public void powerUpCoin (Graphics g, Color color, Color colorA, int xPowerUp, int yPowerUp)
    {
	g.setColor (color);
	g.fillOval (xPowerUp, yPowerUp, 41, 41);
	g.setColor (colorA);
	g.fillOval (xPowerUp + 10, yPowerUp + 10, 20, 20);
	g.setColor (color);
	g.fillRect (xPowerUp + 17, yPowerUp + 17, 5, 5);
	g.setColor (Color.black);
	g.drawRect (xPowerUp + 17, yPowerUp + 17, 5, 5);
	g.drawOval (xPowerUp, yPowerUp, 40, 40);
	g.drawOval (xPowerUp + 10, yPowerUp + 10, 20, 20);
    } //end of powerUpCoin method


    //draws the x2 coin icon
    public void doubleCoins (Graphics g, int xPowerUp, int yPowerUp)
    {
	g.setColor (blueBird);
	g.fillRect (xPowerUp, yPowerUp, 30, 30);
	g.setColor (Color.white);
	g.drawRect (xPowerUp, yPowerUp, 30, 30);
	g.setColor (monsterYellow);
	g.fillOval (xPowerUp + 5, yPowerUp + 10, 10, 10);
	g.setFont (doubleCoinFont);
	g.drawString ("   x2", xPowerUp + 5, yPowerUp + 18);
    } //end of doubleCoins method


    //draws the speed up icon
    public void speedUp (Graphics g, int xPowerUp, int yPowerUp)
    {
	int[] xTip = {xPowerUp + 12, xPowerUp + 16, xPowerUp + 20};
	int[] yTip = {yPowerUp + 22, yPowerUp + 26, yPowerUp + 22};
	g.setColor (Color.red);
	g.fillRect (xPowerUp, yPowerUp, 30, 30);
	g.setColor (Color.black);
	g.drawRect (xPowerUp, yPowerUp, 30, 30);
	g.setColor (Color.orange);
	g.fillArc (xPowerUp + 12, yPowerUp + 1, 8, 8, 180, 180);
	g.setColor (blueBird);
	g.fillArc (xPowerUp + 6, yPowerUp + 5, 20, 10, 180, 180);
	g.setColor (monsterYellow);
	g.fillOval (xPowerUp + 10, yPowerUp + 10, 12, 15);
	g.setColor (blueBird);
	g.fillPolygon (xTip, yTip, 3);
	g.fillOval (xPowerUp + 14, yPowerUp + 15, 4, 4);
    } //end of speedUp method


    //draws the slow down icon
    public void slowDown (Graphics g, int xPowerUp, int yPowerUp)
    {
	g.setColor (blueBird);
	g.fillRect (xPowerUp, yPowerUp, 30, 30);
	g.setColor (Color.white);
	g.drawRect (xPowerUp, yPowerUp, 30, 30);
	g.setColor (monsterYellow);
	g.fillOval (xPowerUp + 2, yPowerUp + 5, 20, 20);
	g.fillOval (xPowerUp + 12, yPowerUp + 17, 15, 8);
	g.drawLine (xPowerUp + 24, yPowerUp + 18, xPowerUp + 24, yPowerUp + 13);
	g.setColor (Color.black);
	g.drawArc (xPowerUp + 5, yPowerUp + 10, 15, 10, 180, 180);
	g.drawArc (xPowerUp + 5, yPowerUp + 10, 10, 10, 0, 180);
	g.drawArc (xPowerUp + 10, yPowerUp + 13, 5, 3, 180, 180);
    } //end of slowDown method


    //draws the invincible shield icon
    public void invincible (Graphics g, int xPowerUp, int yPowerUp)
    {
	g.setColor (blueBird);
	g.fillRect (xPowerUp, yPowerUp, 30, 30);
	g.setColor (Color.white);
	g.drawRect (xPowerUp, yPowerUp, 30, 30);
	g.setColor (monsterYellow);
	g.fillRect (xPowerUp + 11, yPowerUp + 8, 10, 10);
	g.fillArc (xPowerUp + 11, yPowerUp + 13, 10, 10, 180, 180);
	g.setColor (Color.red);
	g.drawLine (xPowerUp + 11, yPowerUp + 12, xPowerUp + 20, yPowerUp + 17);
	g.drawLine (xPowerUp + 11, yPowerUp + 13, xPowerUp + 20, yPowerUp + 18);
	g.fillRect (xPowerUp + 11, yPowerUp + 8, 10, 3);
    } //end of invincible method


    //draws the slow birds icon
    public void slowBirds (Graphics g, int xPowerUp, int yPowerUp)
    {
	Color nestBrown = new Color (139, 69, 19);
	g.setColor (blueBird);
	g.fillRect (xPowerUp, yPowerUp, 30, 30);
	g.setColor (nestBrown);
	g.fillOval (xPowerUp + 5, yPowerUp + 10, 20, 15);
	g.setColor (Color.white);
	g.drawRect (xPowerUp, yPowerUp, 30, 30);
	g.fillOval (xPowerUp + 10, yPowerUp + 5, 10, 15);
	g.setColor (nestBrown);
	g.drawArc (xPowerUp + 10, yPowerUp + 15, 10, 4, 180, 180);
	g.drawArc (xPowerUp + 11, yPowerUp + 14, 9, 4, 180, 180);
    } //end of slowBirds method


    //creates a delay
    public void delay (int time)
    {
	try
	{
	    Thread.sleep (time);
	}
	catch (InterruptedException e)
	{
	}
    } //end of delay method


    //checks if the level buttons are clicked on
    public void mousePressed (MouseEvent e)
    {
	//checks if the player has hit the trampoline in the previous level OR
	//has already unlocked the level
	if ("btnL1".equals (((Button) e.getSource ()).getName ()))
	{
	    buttonsRemove (1);
	}
	else if ("btnL2".equals (((Button) e.getSource ()).getName ()))
	{
	    if (hitTramp [0])
	    {
		if (levelAlreadyUnlocked [0])
		{
		    buttonsRemove (2);
		}
		else if (totalCoins >= 15)
		{
		    drawUnlocked++;
		    totalCoins -= 15;
		    levelAlreadyUnlocked [0] = true;
		    buttonsRemove (2);
		}
	    }
	    else
	    {
		levelBlock = true;
	    }
	}
	else if ("btnL3".equals (((Button) e.getSource ()).getName ()))
	{
	    if (hitTramp [1])
	    {
		if (levelAlreadyUnlocked [1])
		{
		    buttonsRemove (3);
		}
		else if (totalCoins >= 30)
		{
		    drawUnlocked++;
		    totalCoins -= 30;
		    levelAlreadyUnlocked [1] = true;
		    buttonsRemove (3);
		}
	    }
	    else
	    {
		levelBlock = true;
	    }
	}
	else if ("btnL4".equals (((Button) e.getSource ()).getName ()))
	{
	    if (hitTramp [2])
	    {
		if (levelAlreadyUnlocked [2])
		{
		    buttonsRemove (4);
		}
		else if (totalCoins >= 45)
		{
		    drawUnlocked++;
		    totalCoins -= 45;
		    levelAlreadyUnlocked [2] = true;
		    buttonsRemove (4);
		}
	    }
	    else
	    {
		levelBlock = true;
	    }
	}
	else if ("btnL5".equals (((Button) e.getSource ()).getName ()))
	{
	    if (hitTramp [3])
	    {
		if (levelAlreadyUnlocked [3])
		{
		    levelOnScreen = 5;
		    buttonsRemove (5);
		}
		else if (totalCoins >= 60)
		{
		    drawUnlocked++;
		    totalCoins -= 60;
		    levelAlreadyUnlocked [3] = true;
		    buttonsRemove (5);
		}
	    }
	    else
	    {
		levelBlock = true;
	    }
	}
	repaint ();
    } // end of mousePressed method


    //checks which buttons are pressed
    public void keyPressed (KeyEvent ks)
    {
	//moves monster
	if (ks.getKeyCode () == 37 && xMoveMonster > 25) //left
	{
	    xMoveMonster -= 15;
	}
	else if (ks.getKeyCode () == 39 && xMoveMonster < 925) //right
	{
	    xMoveMonster += 15;
	}
	else if (ks.getKeyCode () == 38 && yMoveMonster > 0 && timesRun < timesReach) //up (until the trampoline appears)
	{
	    yMoveMonster -= 15;
	}
	else if (ks.getKeyCode () == 40 && yMoveMonster < 650 & timesRun < timesReach) //down (until the trampoline appears)
	{
	    yMoveMonster += 15;
	}
	//restarts same level
	if (ks.getKeyCode () == 32 && levelOnScreen == -1)
	{
	    reset ();
	    levelOnScreen = previousLevel;
	}
	//takes player backto the menu screen
	if (ks.getKeyCode () == 27 && levelOnScreen == -1)
	{
	    levelOnScreen = 0;
	    drawMenuAgain = true;
	}
	repaint ();
    }


    //plays game music
    public void actionPerformed (ActionEvent ae)
    {
	Button source = (Button) ae.getSource ();
	if (source.getLabel () == "Play Music")
	{
	    music.loop ();
	}
	else if (source.getLabel () == "Stop Music")
	{
	    music.stop ();
	}
    } //end of actionPerformed method


    public void mouseClicked (MouseEvent e)
    {
    }


    public void mouseReleased (MouseEvent e)
    {
    }


    public void mouseEntered (MouseEvent e)
    {
    }


    public void mouseExited (MouseEvent e)
    {
    }


    public void keyReleased (KeyEvent ks)
    {
    }


    public void keyTyped (KeyEvent ks)
    {
    }
}
