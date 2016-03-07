package com.example.martianlander;


import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;


public class MainActivity extends Activity {
	
    private GameLoopView gameLoopView;
    private Button btnRestart;
    private Button btnExit;
    private Button btnLeft;
    private Button btnRight;
    private Button btnUp;

	/**
	 * @author Madushani 22/12/2014
	 * Assignment: 2 Martian Lander
	 * Code illustrates collision detection and models gravity using time.
	 */
    
	/** Called when the activity is first created. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);// set the content view or our widget lookups will fail
        gameLoopView = (GameLoopView)findViewById(R.id.gameLoopView);
        
        //Button Click listener for restart the game
        btnRestart = (Button)findViewById(R.id.btnRestart);
        btnRestart.setOnClickListener(new OnClickListener()
        {	
			@Override
			public void onClick(View v)
			{
				restartGame();
			}
		});
        
        //Button Click listener for exit the game
        btnExit = (Button)findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new OnClickListener()
        {
        	@Override
        	public void onClick(View v)
        	{
        		exitApplication();
        	}        	
        });
        
        //Button touch listener for right button
        btnRight = (Button)findViewById(R.id.btnRight);
        btnRight.setOnTouchListener(new OnTouchListener(){
        	@Override
        	public boolean onTouch(View v, MotionEvent event) 
        	{
        		if(event.getAction()==MotionEvent.ACTION_DOWN)
        		{
        			gameLoopView.rightMoveCraft();
        			if((gameLoopView.getFuel()==false)&&(gameLoopView.getGameover()==false))
        			{
        				gameLoopView.genSounds(R.raw.thruster);
        			}
        		}
        		if(event.getAction()==MotionEvent.ACTION_UP)
        		{
        			gameLoopView.rightMoveCraftOver();
        		}        		
        		return false;        		
        	}        	
        });  
        
        //Button touch listener for left button
        btnLeft = (Button)findViewById(R.id.btnLeft);
        btnLeft.setOnTouchListener(new OnTouchListener(){
        	@Override
        	public boolean onTouch(View v, MotionEvent event) 
        	{
        		if(event.getAction()==MotionEvent.ACTION_DOWN)
        		{
        			gameLoopView.leftMoveCraft();
        			if((gameLoopView.getFuel()==false)&&(gameLoopView.getGameover()==false))
        			{
        				gameLoopView.genSounds(R.raw.thruster);
        			}
        		}
        		if(event.getAction()==MotionEvent.ACTION_UP)
        		{
        			gameLoopView.leftMoveCraftOver();
        		}        		
        		return false;        		
        	}        	
        });
        
        //Button touch listener for up button
        btnUp = (Button)findViewById(R.id.btnUp);
        btnUp.setOnTouchListener(new OnTouchListener(){
        	@Override
        	public boolean onTouch(View v, MotionEvent event) 
        	{
        		if(event.getAction()==MotionEvent.ACTION_DOWN)
        		{
        			gameLoopView.upMoveCraft();
        			if((gameLoopView.getFuel()==false)&&(gameLoopView.getGameover()==false))
        			{
        				gameLoopView.genSounds(R.raw.thruster);
        			}
        		}
        		if(event.getAction()==MotionEvent.ACTION_UP)
        		{
        			gameLoopView.upMoveCraftOver();
        		}        		
        		return false;        		
        	}        	
        });
    }  
    
    //Restart the game
    public void restartGame()
    {    	
		gameLoopView.reset();
		gameLoopView.invalidate();
    }
    
    //Exit the application
    public void exitApplication()
    {
    	finish();
    	System.exit(0);    
    }    
}
