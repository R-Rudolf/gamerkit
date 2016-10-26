package Game.movieClips;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import GameFrame.GFrame;
import GameFrame.MovieClip;
import GameFrame.ShapeDraw;


public class Snake extends MovieClip{
	
	static final int UP = 0, DOWN = 1, LEFT = 3, RIGHT = 4;
	int irany = RIGHT;
	int speed = 5, speedX = speed, speedY = 0;
	int x = 230, y = 200;
	int length = 180;
	Color szin1 = Color.blue;
	Color szin2 = Color.black;
	List<Body> body = Collections.synchronizedList(new LinkedList<Body>());
	ShapeDraw drawer = new ShapeDraw();
	
	void turn(int newIrany){
		if(ellentet(irany) == newIrany)
			return;
		irany = newIrany;
		
		speedX = speedX(irany);
		speedY = speedY(irany);
		synchronized (body) {
			body.add(new Body(this));
		}
	}
	
	int ellentet(int orient){
		switch(orient){
		case(UP):
			return DOWN;
		case(DOWN):
			return UP;
		case(RIGHT):
			return LEFT;
		case(LEFT):
			return RIGHT;
	}
	return 0;
	}
	
	int speedY(int orient){
		switch(orient){
			case(UP):
				return -speed;
			case(DOWN):
				return speed;
			case(RIGHT):
				return 0;
			case(LEFT):
				return 0;
		}
		return 0;
	}

	int speedX(int orient){
		switch(orient){
			case(UP):
				return 0;
			case(DOWN):
				return 0;
			case(RIGHT):
				return speed;
			case(LEFT):
				return -speed;
		}
		return 0;
	}
	
	void deleteBody(Body bd){
		synchronized (body) {
			body.remove(bd);
		}
	}
	
	class Body{
		
		Graphic graf;
		Line2D line;
		Snake parent;
		int orientation;
		
		public double getDistance(){
			return line.getP1().distance(line.getP2());
		}
		
		void grow(){
			Point2D endPoint = line.getP2();
			endPoint.setLocation(endPoint.getX() + parent.speedX(orientation), endPoint.getY() + parent.speedY(orientation));
			line.setLine(line.getP1(), endPoint);
		}
		
		void reduce(){
			double elotte = getDistance();
			Point2D startPoint = line.getP1();
			startPoint.setLocation(startPoint.getX() + parent.speedX(orientation), startPoint.getY() + parent.speedY(orientation));
			line.setLine(startPoint, line.getP2());
			if(Math.round(getDistance()) == 0 || getDistance()-elotte > 0){
				graf.remove();
				parent.deleteBody(this);
			}
		}
		
		Body(Snake in){
			parent = in;
			
			//line = new Line2D.Double(parent.x, parent.y, parent.x, parent.y);
			graf = new Graphic(new Line2D.Double(parent.x, parent.y, parent.x, parent.y), drawer);
			line = (Line2D) graf.body;
			orientation = parent.irany;
		}
	}
	
	public Snake(GFrame in, String name) {
		super(in, name);
		register.registerKeyPress();
		register.registerOnPress();
		register.registerOnEnterFrame();
		//drawer.color = szin;
		drawer.stroke = new BasicStroke(12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		drawer.OutlinePaint = new GradientPaint(new Point(0,0),
                szin1,
                new Point(24,36),
                szin2,
                true);
		
		synchronized (body) {
			body.add(new Body(this));
		}
		
		onPress = new Function(){
			public void method(MouseEvent e){
				szin1 = new Color((int)(Math.random()*250), (int)(Math.random()*250), (int)(Math.random()*250));
				szin2 = new Color((int)(Math.random()*250), (int)(Math.random()*250), (int)(Math.random()*250));
				drawer.OutlinePaint = new GradientPaint(new Point(0,0),
	                szin1,
	                new Point(24,36),
	                szin2,
	                true);
			}
		};
		
		onKeyPressed = new Function(){
			public void method(KeyEvent e){
				switch(e.getKeyCode()){
					case(KeyEvent.VK_ENTER):
						if(frame.displayMode == null)
							frame.fullScreen(true);
						else if(frame.displayMode.isFullScreen)
							frame.displayMode.end();
						else
							frame.displayMode.begin();
						break;
					case(KeyEvent.VK_UP):
						turn(UP);
						break;
					case(KeyEvent.VK_DOWN):
						turn(DOWN);
						break;
					case(KeyEvent.VK_RIGHT):
						turn(RIGHT);
						break;
					case(KeyEvent.VK_LEFT):
						turn(LEFT);
						break;
				}
			}
		};
		/*
		paint = new Function(){
			public void method(Graphics2D g2){
				
				g2.setColor(szin);
				g2.setStroke(new BasicStroke(12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

				synchronized (body) {
					ListIterator<Body> it = body.listIterator();
					while(it.hasNext()){
						g2.draw(it.next().line);
					}
				}
				
			}
		};
		*/
		onEnterFrame = new Function(){
			public void method(){
				x += speedX;
				y += speedY;
				double kul = length;

				synchronized (body) {
					ListIterator<Body> it = body.listIterator();
					while(it.hasNext()){
						kul -= Math.round(it.next().getDistance());
					}
					if(it.hasPrevious())
						it.previous().grow();
					
					if(kul < 0)
						body.get(0).reduce();
				}
			}
		};
	}
}
