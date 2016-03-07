package com.example.martianlander;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameLoopView extends SurfaceView implements Runnable, SurfaceHolder.Callback, OnTouchListener {
	
	/**
	 * @author Madushani 22/14/2014
	 * Assignment : 2 Martian Lander 
	 * Code illustrates collision detection and models gravity using time.
	 */
	
	public static final double INITIAL_TIME = 0;
	private static final int REFRESH_RATE = 20;
	private static final int GRAVITY = 1;
	private static MediaPlayer soundPlayer; //sounds effects

	private float speedX = 0; //Speed of the craft X axis
	private float speedY = 0; //Speed of the craft Y axis
	private float accelX = 1; //Acceleration in the X axis when left or right thruster is fired.
	private float accelY = 3; //Acceleration in the Y axis when pushing, it should be bigger than the gravity.
		
	private int terrain = 0; //3 design of the terrain
	private int score = 0; //Score of the game
	private BitmapShader bmpShader;	
	private Context mpContext;	
	
	private boolean collision = false; //Flag of collision detection
	private boolean win = false; //Flag of the game win
	private boolean gameover = false; // Show the game is over
	private boolean endFuel=false;	//Flag of end of fuel of the craft
	
	private boolean upPressed = false;
	private boolean leftPressed = false;
	private boolean rightPressed = false;	

	private boolean bottomLeft = false;//Flag to show if the left bottom of the craft has contacted the ground.
	private boolean bottomRight = false; //Flag to show if the right bottom of the craft has contacted the ground.
		
	private Thread main;	
	
	private Paint paint = new Paint();	
	private Paint marsPaint = new Paint();
	private Paint textPaint = new Paint();
	
	//Bitmap images get the from the resources folder
	Bitmap mars = BitmapFactory.decodeResource(getResources(), R.drawable.mars);
	Bitmap airCraft = BitmapFactory.decodeResource(getResources(),R.drawable.craftmain);
	Bitmap thruster = BitmapFactory.decodeResource(getResources(), R.drawable.thruster);
	Bitmap main_flame = BitmapFactory.decodeResource(getResources(), R.drawable.main_flame);
	Bitmap blastCraft = BitmapFactory.decodeResource(getResources(), R.drawable.explosion);
	
	//Key points of Terrain 1
	int xcor1[] = { 0, 120, 110, 115, 273, 275, 280, 290, 355, 356, 360, 365, 460, 462, 476, 480, 500, 695, 700, 747, 800, 825, 900, 967, 1025, 1076, 1167, 1200, 1280, 1280, 0, 0 };
	int ycor1[] = { 616, 540, 635, 650, 650, 590, 565, 550, 580, 580, 626, 665, 670, 623, 535, 535, 495, 495, 450, 490,517, 537, 490,  475, 500, 474, 500, 465, 600, 750, 750, 616 };
	
	//Key points of Terrain 2
	int xcor2[] = { 0, 400, 480, 490, 615, 620, 650, 660, 810, 820, 850, 900, 1050, 1100, 1280, 1280, 0, 0};
	int ycor2[] = { 616, 500, 600, 610, 610, 615, 550, 450, 450, 560, 626, 675, 600, 670, 630, 750, 750, 616 };
		
	//Key points of Terrain 3
	int xcor3[] = { 0, 250, 500, 750, 800, 945, 950, 1050, 1280, 1280, 0, 0 };
	int ycor3[] = { 616, 500, 660, 450, 540, 540, 420, 600, 500, 750, 750, 616};
	
	float x, y;
	int width = 0;
	int height = 0;	
	
	//tX and tY are used to calculate the incremental of the speed in X and Y direction.
	double tX = INITIAL_TIME;
	double tY = INITIAL_TIME;
	
	//Fuel bar details
	//Fuel bar right end
	private float fuelBarRightEnd = 0; 
	double fuel = 300;	
	
	Path path;
	public GameLoopView(Context context) {
		super(context);
		mpContext = context;
		init();
	}

	public GameLoopView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mpContext = context;
		init();
	}

	public GameLoopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mpContext = context;
		init();
	}

	public void init()
	{			
		setOnTouchListener(this);	
		getHolder().addCallback(this);
	}	
	
	//Setting mars Terrain by selecting random numbers
	public void setTerrain()
	{
		path = new Path();		
		int selectedValue = terrain; //Avoiding start in same terrain style
		while(terrain==selectedValue)//Generate a random number of the terrain 1 to 3.
		{
			terrain=(int)(3* Math.random())+1;
		}
		if(terrain==1){
		for (int i = 0; i < xcor1.length; i++) {
			path.lineTo(xcor1[i]*width/1280, ycor1[i]*height/800);
		}
		}
		if(terrain==2){
		for (int i = 0; i < xcor2.length; i++) {
			path.lineTo(xcor2[i]*width/1280, ycor2[i]*height/800);
		}
		}
		if(terrain==3){
		for (int i = 0; i < xcor3.length; i++) {
			path.lineTo(xcor3[i]*width/1280, ycor3[i]*height/800);
		}
		}
	}	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);		
		width = w;
		height = h;
		x = width /2;	
	}

	public void run() {		
		setTerrain();
		fuelBarRightEnd=300*width/1280;
		while(true)
		{
			while (!gameover)
			{
				Canvas canvas = null;
				SurfaceHolder holder = getHolder();
				synchronized (holder) {
					canvas = holder.lockCanvas();	
					canvas.drawColor(Color.BLACK);
					
					//Draw mars ground
					bmpShader = new BitmapShader(mars,Shader.TileMode.REPEAT,Shader.TileMode.REPEAT);
			        
					textPaint.setColor(0xFFFFFFFF);  //Text is white color
					textPaint.setStyle(Paint.Style.FILL); 
					textPaint.setTextSize(50);
					
					marsPaint.setShader(bmpShader);
					canvas.drawPath(path,marsPaint);
					
					//Fuel consumption bar
					checkFuelStatus();
					paint.setColor(Color.GREEN);					
					canvas.drawRect(1000*width/1280, 140*height/800, fuelBarRightEnd, 150*height/800, paint) ;
					
					//Move air craft from Y axis
					craftCurrentPosition();	
					
					//Set speed of the craft X and Y axis
					setSpeed();					
					
					//Pass through opposite side of the game field
					passThroughGameField();
					
					canvas.drawText("Fuel: " + fuel+ "  ", 5*width/1280, 150*height/800, textPaint);				

					//Collision detection with the polygon ground and the air craft
					collisionDetection();
					
					if(collision==true)
					{
						if(win==true)
						{							
							genSounds(R.raw.won);
							canvas.drawBitmap(airCraft, x, y, null);
							canvas.drawText("Result: You win!", 400*width/1280, 350*height/800, textPaint);
							calScore();
							canvas.drawText("Score : "+score+" ", 400*width/1280, 150*height/800, textPaint);
						}
						else
						{
							genSounds(R.raw.crash);
							canvas.drawBitmap(blastCraft, x, y, null);
							canvas.drawText("Result: You lost!", 400*width/1280, 350*height/800, textPaint);
							calScore();
							canvas.drawText("Score : "+score+" ", 400*width/1280, 150*height/800, textPaint);
						}
						gameover = true;						
					}
					else
					{
						//Draw air craft and thrusters are not work when the fuel is empty
						canvas.drawBitmap(airCraft, x, y, null);
						if((leftPressed==true)&&(endFuel==false))
						{
							canvas.drawBitmap(thruster, x, y+airCraft.getHeight(), null);
						}
						if((rightPressed==true)&&(endFuel==false))
						{
							canvas.drawBitmap(thruster, (x+(airCraft.getWidth()-thruster.getWidth())),y+airCraft.getHeight(),null);
						}						
						if((upPressed==true)&&(endFuel==false))
						{
							canvas.drawBitmap(main_flame, (x+airCraft.getWidth()/2)-main_flame.getWidth()/2, y+airCraft.getHeight(), null);
						}
					}
				}
				try {
					Thread.sleep(REFRESH_RATE);
				} catch (Exception e) {
				}
	
				finally
				{
					if (canvas != null)
					{
						holder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}		
	}	

	//Check if the left bottom and the right bottom ground
	public boolean contains(int[] xcor, int[] ycor, double x0, double y0) {
		int crossings = 0;

		for (int i = 0; i < xcor.length - 1; i++) {
			int x1 = xcor[i]*width/1280;
			int x2 = xcor[i + 1]*width/1280;

			int y1 = ycor[i]*height/800;
			int y2 = ycor[i + 1]*height/800;

			int dy = y2 - y1;
			int dx = x2 - x1;

			double slope = 0;
			if (dx != 0) {
				slope = (double) dy / dx;
			}

			boolean cond1 = (x1 <= x0) && (x0 < x2); // is it in the range?
			boolean cond2 = (x2 <= x0) && (x0 < x1); // is it in the reverse
														// range?
			boolean above = (y0 < slope * (x0 - x1) + y1); // point slope y - y1

			if ((cond1 || cond2) && above) {
				crossings++;
			}
		}
		return (crossings % 2 != 0); // even or odd
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		main = new Thread(this);				
		if (main != null)
			main.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;		
		while (retry)
		{
			try
			{
				main.join();
				retry = false;
			}
			catch (InterruptedException e)
			{
				// try again shutting down the thread
			}
		}
	}

	//Change air craft according to finger touch on the screen
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return true;		
	}
	
	//Reset the game
	public void reset()
	{	
		stopSounds();//Stop currently playing sounds
		gameover = false;		
		x = width /2;
		y = 0;
		
		//Initial speed of the air craft.
		speedX = 0;
		speedY = 0;
		
		//Reset the fuel bar and fuel quantity
		fuelBarRightEnd = 300*width/1280;		
		fuel = 300;
		
		//Initialize the different flags to start from the beginning
		endFuel = false;
		collision = false;
		win = false;	
		bottomLeft = false;
		bottomRight = false;		
		
		init();
		setTerrain();
	}
	
	//Current position of the airCraft and move from top to down		
	public void craftCurrentPosition()
	{
		y = (int)y+ (int)(speedY*0.1);
		x = (int)x+ (int)(speedX*0.1);
		if((y-airCraft.getHeight())<0)
		{
			y=(airCraft.getHeight());
			tY = 0;
			speedY=0;
		}
	}
	
	//Set the speed in X direction depending on the fuel and the button condition**/
	public void setSpeedX()
	{
		if(endFuel == false)
		{
			if(leftPressed == true)
			{
				moveRightSpeed();					
			}			
			if(rightPressed == true)
			{
				moveLeftSpeed();					
			}
		}
	}
	
	//Set the current speed in Y axis depends on the condition of button pressing
	public void setSpeedY()
	{
		if((upPressed== true) &&(endFuel == false))
		{
			moveUpSpeed();					
		} 
		/**The acceleration in Y axis 
		should be GRAVITY.**/
		else
		{
			speedY+=GRAVITY*0.1;
		}
	}
	
	/**Calculate the speed in Y direction and the fuel consumption.
	 * because "GRAVITY-accelY" is the acceleration in Y
	 * axis, then use  "V=Vo+at" to get the current speed in Y axis in each loop **/
	public void moveUpSpeed()
	{
		tY+=0.1;
		speedY +=(float)((GRAVITY-accelY)*tY);
		fuel -= 1;
	
	}
	/**Calculate the speed in X axis and the fuel consumption.
	 * "tX+=0.1" each time, is to the passage of time,
	 * If player press the right button contiguously, many loops has passed, so the "tX" increase
	 * the value of the X direction would be smaller, it also can be minus, means moving left.**/
	public void moveLeftSpeed()
	{
		speedX += (float) ((-accelX)*tX);
		tX+=0.1;
		fuel -= 1;
	}
	
	/**Calculate the speed in X axis and the fuel consumption.
	 * "tX+=0.1" each time, is to the passage of time,
	 * If player press the right button contiguously, many loops has passed, so the "tX" increase
	 * the value of the X direction would be smaller, it also can be minus, means moving right.**/
	public void moveRightSpeed()
	{
		speedX += (float) (accelX*tX);
		tX+=0.1;
		fuel -= 1;
	}	
	
	//Set the speed in X axis and Y axis.
	public void setSpeed()
	{
		setSpeedX();
		setSpeedY();
	}
	
	//Collision Detection
	public void collisionDetection()
	{
		/**Check collision with the different terrain with polygon ground
		 * X axis and Y axis with air craft X and Y **/
		if(terrain==1)
		{
			//Setting the air craft parameters x and y is the left top corner of the craft.
			bottomLeft = contains(xcor1, ycor1, x, y+airCraft.getHeight());    
			bottomRight = contains(xcor1, ycor1, x+airCraft.getWidth(), y+airCraft.getHeight());
		}
		if(terrain==2)
		{
			bottomLeft = contains(xcor2,ycor2,x,y+airCraft.getHeight());
			bottomRight = contains(xcor2,ycor2,x+airCraft.getWidth(),y+airCraft.getHeight());
		}		
		if(terrain==3)
		{
			bottomLeft = contains(xcor3,ycor3,x,y+airCraft.getHeight());
			bottomRight = contains(xcor3,ycor3,x+airCraft.getWidth(),y+airCraft.getHeight());
		}
		//If any of the side collision with the ground is crashed the craft
		if(bottomLeft != bottomRight)
		{
			collision = true;
			win = false;
		}		
		//Both the side land on flat ground
		else if (bottomLeft && bottomRight)
		{
			genSounds(R.raw.landing);
			collision = true;
			win = true;
		}

		//Without any of collision
		else
		{
			collision = false;
			win = false;
		}	
	}
	
	/**Pass through to the opposite side of the game boundary instead of being blocked,
	 * This method is just for the 1280*800 screen.**/
	private boolean intersects(int[] xcor12, int[] ycor12, float x2, float f) {		
		return false;
	}

	//To run the air craft over the game field
	public void passThroughGameField()
	{
		if((x+airCraft.getWidth())<0)
		{
			x=width;
		}
		if(x>width)
		{
			x=-airCraft.getWidth();
		}		
	}
	
	//If right button clicked then the action be true
	public void rightMoveCraft()
	{		
		rightPressed = true;	
	}
	//Create right flag when user click on right button
	public void rightMoveCraftOver()
	{
		rightPressed = false;
		/**Initialize tX = 0 each time the right button is up,
		 * otherwise calculate of speed would be wrong.**/		
		tX = 0;	
	}
	
	//If left button clicked then the action be true
	public void leftMoveCraft()
	{		
		leftPressed = true;	
	}
	//Create left flag when user click on left button
	public void leftMoveCraftOver()
	{
		leftPressed = false;
		/**Initialize tX = 0 each time the left button is up,
		 * otherwise calculate of speed would be wrong.**/	
		tX = 0;	
	}
	
	//If up button clicked then the action be true
	public void upMoveCraft()
	{		
		upPressed = true;	
	}
	
	//Create up flag when user click on up button
	public void upMoveCraftOver()
	{
		upPressed = false;
		/**Initialize tY = 0 each time the up button is up,
		 * otherwise calculate of speed would be wrong.**/	
		tY = 0;	
	}
	//Return the game over flag
	public boolean getGameover()
	{
		return gameover;
	}
	
	//Check the fuel consumption and set the fuel Bar right end.**/
	public void checkFuelStatus()
	{
		fuelBarRightEnd = (float) (1000*width/1280+fuel*width/1280); //+ fuel*2*width/1280);
		if((fuelBarRightEnd<=(1000*width/1280))||(fuel<=0))
		{
			fuelBarRightEnd = 1000*width/1280;
			endFuel = true;			
		}		
	}
	
	//Return the fuel condition
	public boolean getFuel()
	{
		return endFuel;
	}
	
	//Calculate score
	public void calScore()
	{
		if(win == false)
		{
			score = 0;
		}
		if(win == true)
		{
			//score depend on the fuel consumption
			//and score multiply .5 with available fuel and add 10 for score
			score = (int)(10+fuel*0.5);
		}		
	}
	
	//Generating sound effects
	public void genSounds(int resID){
		
		/**Every time start media player to play 
		a new sound, stop current media player, then release it 
		and after that instantiate new instance to play the new sound.**/
		if((soundPlayer != null)&& (soundPlayer.isPlaying())){  
			soundPlayer.stop();  
			soundPlayer.release();  			  
		  }  
		//Initialize the media player
		soundPlayer = MediaPlayer.create(mpContext, resID);		
		soundPlayer.start();		 
	}
	
	//Stop sounds	
	public void stopSounds()
	{
		if(soundPlayer.isPlaying())
		{
			soundPlayer.stop();
		}			
	}	
}
