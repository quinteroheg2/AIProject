package com.jerry.aiproject.gameobjects;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.jerry.aiproject.core.*;
import com.jerry.aiproject.utils.*;
import com.jerry.aiproject.aialgorithms.Node;

/**
 * This class defines the GameObject Player.
 * The player is controlled by KeyInput and
 * can interact with both weapons and enemies.
 * @author Jerry
 */
public class Player extends GameObject implements Movement {

    int delX, delY; //Object's movement variables.
	//Walk images without weapons. 
	private BufferedImage[] walkDown, walkUp, walkRight, walkLeft;
	//Animation objects for the animations.
	private Animation downAnim, upAnim, rightAnim, leftAnim; 
	public int health; //Player health.
	public int stepCount = 0; //Determines the step in the path. 
	
	//Booleans needed to fix the Sticky Keys problem, solution from Java-Gaming.org.
	private boolean isUp, isDown, isRight, isLeft;

	public Player(int xPos, int yPos) {
		super(xPos, yPos, GameObjectType.PLAYER);
		
		init();
	}

	/**
	 * This method initializes all 
	 * player variables. 
	 */
	@Override
	public void init() {
		initialImage = SpriteLoader.loadImage(1, 1, 32, 48);
		loadAnimation();
		downAnim = new Animation(walkDown, 7);
		upAnim = new Animation(walkUp, 7);
		rightAnim = new Animation(walkRight, 7);
		leftAnim = new Animation(walkLeft, 7);
		
		health = 50; //FOR SIA
	}

	/**
	 * This method updates any player
	 * related variables. It is called
	 * in the main game loop. 
	 */
	@Override
	public void update() {
		checkCollisions();
		setX(getX() + getDelX());
		setY(getY() + getDelY());
		
		/*Implemented sticky key bug fix.*/
		//Up/down movement.
		if(isUp)
		{
			setDelY(-2);
			upAnim.runAnimation(); //Start the animation updates. 
			initialImage = walkUp[0]; //Facing a new direction, set the initialImage.
		}
		else if(isDown)
		{
			setDelY(2);
			downAnim.runAnimation();
			initialImage = walkDown[0];
		}
		else
			setDelY(0);
		//For right/left movement.
		if(isRight)
		{
			setDelX(2);
			rightAnim.runAnimation();
			initialImage = walkRight[0];
		}
		else if(isLeft)
		{
			setDelX(-2);
			leftAnim.runAnimation();
			initialImage = walkLeft[0];
		}
		else
			setDelX(0);
	}
	
	/**
	 * This method checks for any collisions
	 * The player might have with the wall, 
	 * or other game objects. 
	 */
	public void checkCollisions() {
		if(getX() >= Game.WIDTH - initialImage.getWidth()/2)
			setX(Game.WIDTH - initialImage.getWidth()/2);
		else if(getX() <= 0 - initialImage.getWidth()/6)
			setX(0 - initialImage.getWidth()/6);
		
		if(getY() >= Game.HEIGHT - initialImage.getHeight())
			setY(Game.HEIGHT - initialImage.getHeight());
		else if(getY() <= 0 + initialImage.getHeight()/6)
			setY(0 + initialImage.getHeight()/6);
	}

	/**
	 * This method handles the drawings
	 *  of the player, it is called in 
	 *  the main game loop.
	 */
	@Override
	public void render(Graphics g) {
		//Drawing Images: image, X-Position, Y-Position, width, height, ImageObserver. 
		if(getDelY() == 0)
			g.drawImage(initialImage, getX(), getY(), 32, 48, null);
		
		//Fixes double-drawing Animation issues.
		if(getDelY() > 0 && isDown && !isLeft && !isRight || isDown && isRight || isDown && isLeft)
			downAnim.drawAnimation(g, getX(), getY(), 32, 48);
		else if(getDelY() < 0 && isUp && !isLeft &&!isRight || isUp && isRight || isUp && isLeft)
			upAnim.drawAnimation(g, getX(), getY(), 32, 48);
		
		if(getDelX() > 0 && isRight && !isUp && !isDown)
			rightAnim.drawAnimation(g, getX(), getY(), 32, 48);
		else if(getDelX() < 0 && isLeft && !isUp && !isDown)
			leftAnim.drawAnimation(g, getX(), getY(), 32, 48);
		
		//DRAW HEALTH BAR
		if(health <= 50)
			g.setColor(Color.RED);
		else
			g.setColor(Color.GREEN);
		g.fillRect(Game.WIDTH - 250, 10, health, 25);
		g.setColor(Color.WHITE);
		g.drawRect(Game.WIDTH - 250, 10, 200, 25);
		
		//DEBUG TOOL
		//g.setColor(Color.RED);
		//g.drawRect(getBounds().x, getBounds().y, getBounds().width, getBounds().height);
	}

	/**
	 * This method loads all the animation images
	 * from the sprite sheet for the player animation.
	 */
	@Override
	public void loadAnimation() {
		walkDown = new BufferedImage[]{
				SpriteLoader.loadImage(1, 1, 32, 48),
				SpriteLoader.loadImage(2, 1, 32, 48),
				SpriteLoader.loadImage(3, 1, 32, 48),
				SpriteLoader.loadImage(4, 1, 32, 48)
		};
		walkUp = new BufferedImage[] {
				SpriteLoader.loadImage(1, 4, 32, 48),
				SpriteLoader.loadImage(2, 4, 32, 48),
				SpriteLoader.loadImage(3, 4, 32, 48),
				SpriteLoader.loadImage(4, 4, 32, 48)
		};
		walkRight = new BufferedImage[] {
				SpriteLoader.loadImage(1, 3, 32, 48),
				SpriteLoader.loadImage(2, 3, 32, 48),
				SpriteLoader.loadImage(3, 3, 32, 48),
				SpriteLoader.loadImage(4, 3, 32, 48)
		};
		walkLeft = new BufferedImage[] {
				SpriteLoader.loadImage(1, 2, 32, 48),
				SpriteLoader.loadImage(2, 2, 32, 48),
				SpriteLoader.loadImage(3, 2, 32, 48),
				SpriteLoader.loadImage(4, 2, 32, 48)
		};
	}

	/**
	 * This method is used to determine collisions
	 * between the player and other GameObjects. 
	 */
	@Override
	public Rectangle getBounds() {
		return new Rectangle(getX() + 5, getY() + 5, initialImage.getWidth() - 9, initialImage.getHeight() - 5);
	}
	
	/**
	 * This method is required by the 
	 * AIMovemnt interface, it will move
	 * the player along the path passed 
	 * into the method. 
	 * @param path the path generated by an AI algorithm.
	 * @return whethere th end of the path has been reached.  
	 */
	@Override
	public boolean moveAlongPath(TileMap map, Path path) {
		//Get the first step.
		Node step = path.getStep(stepCount);

			/* Check the rows. */
			if(getY() != map.getYCoord(step.getRow()))
				//		System.err.println(step != path.getStep(path.getLength() - 1));
				//		if(step != path.getStep(path.getLength() - 1))
			{
				//If the steps are below the player.
				if(getY() < map.getYCoord(step.getRow()) && !isUp)
				{
					//Go down, if the the column is also different, go diagonal.
					this.keyPressed(KeyEvent.VK_DOWN);

					if(getX() > map.getXCoord(step.getCol()))
						this.keyPressed(KeyEvent.VK_LEFT);
					else if(getX() < map.getXCoord(step.getCol()))
						this.keyPressed(KeyEvent.VK_RIGHT);

					//Let go of keys once the coordinates are the same (Pixel measurement). 
					if(getX() == map.getXCoord(step.getCol()))
					{
						this.keyReleased(KeyEvent.VK_LEFT);
						this.keyReleased(KeyEvent.VK_RIGHT);
					}
					if(getY() == map.getYCoord(step.getRow()))
					{
						this.keyReleased(KeyEvent.VK_DOWN);
					}

					//Let go of keys once the coordinates are the same (rows/columns measurement).
					if(map.getXCoord(step.getCol()) == getX() /*&& map.getYCoord(step.getRow()) == getY()*/)
					{
						//this.keyReleased(KeyEvent.VK_DOWN);
						this.keyReleased(KeyEvent.VK_LEFT);
						this.keyReleased(KeyEvent.VK_RIGHT);
						if(stepCount == path.getLength() - 1)
							stepCount = 0;
						stepCount++;
					}
				}
				//If the steps are above the player. 
				else if(getY() > map.getYCoord(step.getRow()) && !isDown)
				{
					this.keyPressed(KeyEvent.VK_UP);
					if(getX() > map.getXCoord(step.getCol()))
						this.keyPressed(KeyEvent.VK_LEFT);
					else if(getX() < map.getXCoord(step.getCol()))
						this.keyPressed(KeyEvent.VK_RIGHT);

					//Let go of keys once the coordinates are the same (Pixel measurement). 
					if(getX() == map.getXCoord(step.getCol()))
					{
						this.keyReleased(KeyEvent.VK_LEFT);
						this.keyReleased(KeyEvent.VK_RIGHT);
					}
					if(getY() == map.getYCoord(step.getRow()))
					{
						this.keyReleased(KeyEvent.VK_UP);
					}

					//Let go of keys once the coordinates are the same (rows/columns measurement).
					if(map.getXCoord(step.getCol()) == getX() /*&& map.getYCoord(step.getRow()) == getY()*/)
					{
						//this.keyReleased(KeyEvent.VK_DOWN);
						this.keyReleased(KeyEvent.VK_LEFT);
						this.keyReleased(KeyEvent.VK_RIGHT);
						if(stepCount == path.getLength() - 1)
							stepCount = 0;
						stepCount++;
					}
				}
			}
			/* Check the columns. */
			//		else if(getX() != map.getXCoord(step.getCol()))
			//		{
			//			if(getX() < map.getXCoord(step.getCol()) && !isLeft)
			//			{
			//				this.keyPressed(KeyEvent.VK_RIGHT);
			//				
			//				//Diagonal movements. 
			//				if(getY() > map.getYCoord(step.getRow()))
			//					this.keyPressed(KeyEvent.VK_DOWN);
			//				else if(getY() < map.getYCoord(step.getRow()))
			//					this.keyPressed(KeyEvent.VK_UP);
			//				
			//				if(getY() == map.getYCoord(step.getRow()))
			//				{
			//					this.keyReleased(KeyEvent.VK_DOWN);
			//					this.keyReleased(KeyEvent.VK_UP);
			//				}
			//				if(getX() == map.getXCoord(step.getCol()))
			//				{
			//					this.keyReleased(KeyEvent.VK_RIGHT);
			//				}
			//				
			//				//Let go of keys once the coordinates are the same (rows/columns measurement).
			//				if(map.getYCoord(step.getRow()) == getY())
			//				{
			//					this.keyReleased(KeyEvent.VK_DOWN);
			//					this.keyReleased(KeyEvent.VK_UP);
			//					if(stepCount == path.getLength() - 1)
			//						stepCount = 0;
			//					stepCount++;
			//				}
			//			}
			//		}

			//First step
			else if(step.getCol() == map.getCol(getX()) && step.getRow() == map.getRow(getY()))//If no moves can be made, just return. 
			{	
				if(stepCount == path.getLength() - 1)
					stepCount = 0;
				stepCount++;
				return false;
			}
		//}
		//Goal node has been reached. 
		/*if(getY() >= map.getYCoord(path.getStep(path.getLength() - 1).getRow()) && getX() >= map.getXCoord(path.getStep(path.getLength() - 1).getCol()))*/
		if(map.getRow(getY()) == path.getStep(path.getLength() - 1).getRow() && map.getCol(getX()) == path.getStep(path.getLength() - 1).getCol())
		{
			this.keyReleased(KeyEvent.VK_UP);
			this.keyReleased(KeyEvent.VK_DOWN);
			this.keyReleased(KeyEvent.VK_RIGHT);
			this.keyReleased(KeyEvent.VK_LEFT);
			stepCount = 0;
			return true;	
		}
		//DEBUG
		//		System.out.println("PlayCoords: " + map.getRow(getY()) + 
		//				"(" + isDown + ")(" + getY() + "), " + map.getCol(getX()) + "(" + isLeft + ") (" + getX() +")" +
		//				"\nPathCoords: " + step.getRow() + "(" + map.getYCoord(step.getRow()) + "), " + 
		//				step.getCol() + "(" + map.getXCoord(step.getCol()) + ") \n");
		return false;
	}

    /* Setters and getters required by the Movement Interface. */
    public void setDelX(int delX) {
        this.delX = delX;
    }

    public int getDelX() {
        return delX;
    }

    public void setDelY(int delY) {
        this.delY = delY;
    }

    public int getDelY() {
        return delY;
    }

	/**
	 * This method handles key input from
	 * The Game class's key listener. 
	 * @param e the KeyEvent.
	 */
	public void keyPressed(int key) {
		
		if(key == KeyEvent.VK_UP || key == KeyEvent.VK_W)
			isUp = true;
		if(key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S)
			isDown = true;
		if(key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D)
			isRight = true;	
		if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A)
			isLeft = true;
	}
	
	/**
	 * This method handles key input from
	 * The Game class's key listener. 
	 * @param e the KeyEvent.
	 */
	public void keyReleased(int key) {
		
		if(key == KeyEvent.VK_UP || key == KeyEvent.VK_W)
			isUp = false;
		if(key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S)
			isDown = false;
		if(key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D)
			isRight = false;
		if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A)
			isLeft = false;
	}
}