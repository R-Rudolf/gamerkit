package Game.movieClips;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

import GameFrame.*;


public class golyo extends MovieClip{
	int deg = 1, r = 30, w = h = 90, x1 = frame.screenWidth/2 - w/2, y1 = frame.screenHeight/2 - h/2;
	double speed = Math.random()*10;
	Color clr = new Color((int)(Math.random()*250), (int)(Math.random()*250), (int)(Math.random()*250));
	
	Ellipse2D.Double graf = new Ellipse2D.Double(x, y, w, h);
	//Line2D.Double graf = new Line2D.Double(x1, 0, y1, 0);
	ShapeDraw drawer = new ShapeDraw();
	
	public golyo(GFrame in, String name) {
		super(in, name);
		
		register.registerOnEnterFrame();
		register.registerOnRelease();
		
		drawer.color = clr;
		drawer.fill = true;
		//drawer.stroke = new BasicStroke(12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		
		new Graphic(graf, drawer);
		
		onEnterFrame = new Function(){
			public void method(){
				deg += speed;
				graf.x = (int)(Math.sin(Math.toRadians(deg))*r*2) + x1;
				graf.y = (int)(Math.cos(Math.toRadians(deg))*r) + y1;
			}
		};
		
		onRelease = new Function(){
			public void method(MouseEvent e){
				drawer.color = new Color((int)(Math.random()*250), (int)(Math.random()*250), (int)(Math.random()*250));
				
				onRelease = new Function(){
					public void method(MouseEvent e){
						moveTop();
					}
				};
			}
		};

	}
}