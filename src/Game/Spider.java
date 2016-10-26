package Game;
/*				created on: 2009.05.24				*/
/**				@author Rudolf Horváth				*/

import java.awt.RenderingHints;

import Game.movieClips.*;
import GameFrame.*;


public class Spider extends GFrame{
	
    Spider(/*Window in*/){
		super(/*in*/);
		//in.setParent(this);
		
		//addMC(new Snake(this, "snake"));
		//addMC(new golyo(this, "golyo"));
		addMC(new teszt(this, "teszt"));
	    renderingHints.put(RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);
	}

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		//Spider main = new Spider(new Window());
		Spider main = new Spider();
	}
}
