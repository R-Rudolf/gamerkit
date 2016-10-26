package Game.movieClips;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import GameFrame.GFrame;
import GameFrame.ShapeDraw;

public class teszt extends GameFrame.MovieClip {

	ShapeDraw drawer = new ShapeDraw();
	Graphic vonal;
	Graphic negyzet;
	int tolY, tolX;
	
	
	public teszt(GFrame in, String name) {
		super(in, name);
		drawer.stroke = new BasicStroke(12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		drawer.color = Color.GREEN;
		vonal = new Graphic(new Ellipse2D.Double(10,90,90,10), drawer);
		negyzet = new Graphic(new Rectangle2D.Double(300, 300, 20, 20), drawer);
		
		register.registerOnPress();
		register.registerOnRelease();
		register.registerOnReleaseOutside();
		register.registerOnEnterFrame();
		
		onRelease = onReleaseOutside = new Function(){
			public void method(MouseEvent e){
				onEnterFrame = new Function(){
						public void method(){}
				};
			}
		};
		
		onPress = new Function(){
			public void method(MouseEvent e){
				if(!vonal.body.contains(e.getPoint()))
					return;
				
				tolX = (int) (vonal.x - getMouseX());
				tolY = (int) (vonal.y - getMouseY());
				
				onEnterFrame = new Function(){
					public void method(){
						vonal.setX(getMouseX()+tolX);
						vonal.setY(getMouseY()+tolY);
						/*
						if(vonal.hit(negyzet)){
							drawer.color = Color.GRAY;
						}else{
							drawer.color = Color.GREEN;
						}*/
					}
				};
			}
		};
	}
}
