/*				created on: 2009.05.24				*/
/**				@author Rudolf Horváth				*/

package GameFrame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.imageio.ImageIO;


public abstract class MovieClip{
	
	public    String name;
	public int x, y, w, h, a = 100, depth, sizeOfChilds = 0;
	protected GFrame frame;
	protected Area HitShape = new Area();
	protected Image img;
	protected MovieClip parent;
	protected boolean mouseOn = false;
	public    List<MovieClip> childs = 	Collections.synchronizedList(new LinkedList<MovieClip>());
	protected List<Graphic> graphics =  Collections.synchronizedList(new LinkedList<Graphic>());
	
	protected Register register;
	
	public  Function onWheelMoved 		= new Function();
	public  Function onRollOut 			= new Function();
	public  Function onClick			= new Function();
	public  Function onRelease 			= new Function();
	public  Function onReleaseOutside	= new Function();
	public  Function onPress 			= new Function();
	public  Function onRollOver 		= new Function();
	
	public  Function onKeyPressed	= new Function();
	public  Function onKeyReleased	= new Function();
	public  Function onKeyTyped		= new Function();
	
	public 	Function paint = new Function(); 
	public 	Function onEnterFrame = new Function();

	protected final class Graphic{
		public Shape body;
		public ShapeDraw drawer;
		public double x, y, w, h;
		
		public boolean hit(Graphic in){
			Area A = new Area();
			if(drawer.OutLine)
				A.add(new Area(drawer.stroke.createStrokedShape(body)));
			if(drawer.fill)
				A.add(new Area(body));

			Area B = new Area();
			if(in.drawer.OutLine)
				B.add(new Area(in.drawer.stroke.createStrokedShape(in.body)));
			if(in.drawer.fill)
				B.add(new Area(in.body));
			
			A.intersect(B);
			return !A.equals(new Area());
		}
		
		
		public void setX(double in){
			translate(in-x, 0);
			x=in;
		}
		
		public void setY(double in){
			translate(0, in-y);
			y=in;
		}
		
		protected void translate(double deltaX, double deltaY){
			transform(AffineTransform.getTranslateInstance(deltaX,
	                 deltaY));
		}
		
		protected void transform(AffineTransform at){
	         body = at.createTransformedShape(body);
		}
		
		public void draw(Graphics2D g2){
			drawer.draw(g2, body);
		}
		
		public void remove(){
			removeGraf(this);
		}
		
		public Graphic(Shape inS, ShapeDraw inD){
			body = inS;
			Rectangle2D tmp = inS.getBounds2D();
			x = tmp.getMinX();
			y = tmp.getMinY();
			w = tmp.getWidth();
			h = tmp.getHeight();
			drawer = (inD == null)?new ShapeDraw():inD;
			addGraf(this);
		}

		
		public Area getHitArea() {
			return drawer.getHitArea(body);
		}
	}
	
//============================================================================
/**									Events									*/

	protected final void onEnterFrame	(){onEnterFrame.method();};
	
	protected final void paint(Graphics2D g2){
		synchronized (graphics) {
			ListIterator<Graphic> it = graphics.listIterator();
			while(it.hasNext()){
				it.next().draw(g2);
			}
		}
		paint.method(g2);
		
	}
	
	public class Function{
		public void method(){}
		public void method(Graphics2D g2){}
		public void method(MouseEvent e){}
		public void method(MouseWheelEvent e){}
		public void method(KeyEvent e){}
		public void method(MovieClip mc){}
	}

	protected final class Register{
		
		private MovieClip target;

		public void registerOnEnterFrame(){
			synchronized (frame.onEnterFrameListeners) {
				frame.onEnterFrameListeners.add(target);
			}
		}
		
		public void resignOnEnterFrame(){
			synchronized (frame.onEnterFrameListeners) {
				frame.onEnterFrameListeners.remove(target);
			}
		}
		
		public void registerOnPress(){
			synchronized (frame.onPressListeners) {
				frame.onPressListeners.add(target);
			}
			synchronized (frame.onHitListeners) {
				frame.onHitListeners.add(target);
			}
		}

		public void registerOnRelease(){
			synchronized (frame.onReleaseListeners) {
				frame.onReleaseListeners.add(target);
			}
			synchronized (frame.onHitListeners) {
				frame.onHitListeners.add(target);
			}
		}

		public void registerOnClick(){
			synchronized (frame.onClickListeners) {
				frame.onClickListeners.add(target);
			}
			synchronized (frame.onHitListeners) {
				frame.onHitListeners.add(target);
			}
		}

		public void registerOnReleaseOutside(){
			synchronized (frame.onReleaseOutsideListeners) {
				frame.onReleaseOutsideListeners.add(target);
			}
			synchronized (frame.onHitListeners) {
				frame.onHitListeners.add(target);
			}
		}

		public void registerOnMouseMoved(){
			synchronized (frame.onMouseMovedListeners) {
				frame.onMouseMovedListeners.add(target);
			}
			synchronized (frame.onHitListeners) {
				frame.onHitListeners.add(target);
			}
		}
		
		public void resignOnPress(){
			synchronized (frame.onPressListeners) {
				frame.onPressListeners.remove(target);
			}
			if(!frame.onClickListeners.contains(target) && !frame.onMouseMovedListeners.contains(target) && 
					!frame.onPressListeners.contains(target) && !frame.onReleaseListeners.contains(target) &&
					!frame.onReleaseOutsideListeners.contains(target)){	// ha nem figyel egér eseményeket
				synchronized (frame.onHitListeners) {
					frame.onHitListeners.remove(target);
				}
			}
		}
		
		public void resignOnRelease(){
			synchronized (frame.onReleaseListeners) {
				frame.onReleaseListeners.remove(target);
			}
			if(!frame.onClickListeners.contains(target) && !frame.onMouseMovedListeners.contains(target) && 
					!frame.onPressListeners.contains(target) && !frame.onReleaseListeners.contains(target) &&
					!frame.onReleaseOutsideListeners.contains(target)){	// ha nem figyel egér eseményeket
				synchronized (frame.onHitListeners) {
					frame.onHitListeners.remove(target);
				}
			}
		}

		public void resignOnClick(){
			synchronized (frame.onClickListeners) {
				frame.onClickListeners.remove(target);
			}
			if(!frame.onClickListeners.contains(target) && !frame.onMouseMovedListeners.contains(target) && 
					!frame.onPressListeners.contains(target) && !frame.onReleaseListeners.contains(target) &&
					!frame.onReleaseOutsideListeners.contains(target)){	// ha nem figyel egér eseményeket
				synchronized (frame.onHitListeners) {
					frame.onHitListeners.remove(target);
				}
			}
		}

		public void resignOnReleaseOutside(){
			synchronized (frame.onReleaseOutsideListeners) {
				frame.onReleaseOutsideListeners.remove(target);
			}
			if(!frame.onClickListeners.contains(target) && !frame.onMouseMovedListeners.contains(target) && 
					!frame.onPressListeners.contains(target) && !frame.onReleaseListeners.contains(target) &&
					!frame.onReleaseOutsideListeners.contains(target)){	// ha nem figyel egér eseményeket
				synchronized (frame.onHitListeners) {
					frame.onHitListeners.remove(target);
				}
			}
		}

		public void resignOnMouseMoved(){
			synchronized (frame.onMouseMovedListeners) {
				frame.onMouseMovedListeners.remove(target);
			}
			if(!frame.onClickListeners.contains(target) && !frame.onMouseMovedListeners.contains(target) && 
					!frame.onPressListeners.contains(target) && !frame.onReleaseListeners.contains(target) &&
					!frame.onReleaseOutsideListeners.contains(target)){	// ha nem figyel egér eseményeket
				synchronized (frame.onHitListeners) {
					frame.onHitListeners.remove(target);
				}
			}
		}
		

		public void registerKeyPress(){
			synchronized (frame.KeyPressListeners) {
				frame.KeyPressListeners.add(target);
			}
		}
		
		public void registerKeyRelease(){
			synchronized (frame.KeyReleaseListeners) {
				frame.KeyReleaseListeners.add(target);
			}
		}
		
		public void registerKeyType(){
			synchronized (frame.KeyTypeListeners) {
				frame.KeyTypeListeners.add(target);
			}
		}
		
		public void resignKeyType(){
			synchronized (frame.KeyTypeListeners) {
				frame.KeyTypeListeners.remove(target);
			}
		}

		public void resignKeyPress(){
			synchronized (frame.KeyPressListeners) {
				frame.KeyPressListeners.remove(target);
			}
		}
		
		public void regsignKeyRelease(){
			synchronized (frame.KeyReleaseListeners) {
				frame.KeyReleaseListeners.remove(target);
			}
		}
		
		public Register(MovieClip in){
			target = in;
		}
		
	}
	
//--------------------------------------------
/**				Mouse Events				*/
	protected final int getMouseX(){
		Point tmp = frame.canvas.getMousePosition();
		return (tmp == null)? frame.lastMouseX: tmp.x;
	}
	
	protected final int getMouseY(){
		Point tmp = frame.canvas.getMousePosition();
		return (tmp == null)? frame.lastMouseY: tmp.y;
	}
	
	protected final void mouseMoved(MouseEvent e){
		boolean isInside = HitShape.contains(e.getPoint());
		if(isInside && !mouseOn){
			mouseOn = true;
			onRollOver(e);
		}else if (mouseOn && !isInside){
			mouseOn = false;
			onRollOut(e);
		}
	}
	
	protected final void onRollOut		(MouseEvent e){onRollOut.method(e);}
	protected final void onClick			(MouseEvent e){onClick.method(e);}
	protected final void onRelease		(MouseEvent e){onRelease.method(e);}
	protected final void onReleaseOutside	(MouseEvent e){onReleaseOutside.method(e);}
	protected final void onPress			(MouseEvent e){onPress.method(e);}
	protected final void onRollOver		(MouseEvent e){onRollOver.method(e);}
	protected final void onWheelMoved		(MouseWheelEvent e){onWheelMoved.method(e);}
	
//--------------------------------------------
/**					Key Events				*/
	
	protected final void onKeyPressed		(KeyEvent e) {onKeyPressed.method(e);}
	protected final void onKeyReleased	(KeyEvent e) {onKeyReleased.method(e);}
	protected final void onKeyTyped		(KeyEvent e) {onKeyTyped.method(e);}
	

//============================================================================
/**								Konstruktors								*/
	
	public MovieClip(GFrame frame, MovieClip parent, String name){
		this.name = name;
		this.frame = frame;
		this.parent = parent;
		register = new Register(this);
	}
	
	public MovieClip(GFrame in, String name){
		this.name = name;
		register = new Register(this);
		frame = in;
		parent = frame.root;
	}
	
	public MovieClip(GFrame in, MovieClip parent, String name, File imgSrc){
		this.name = name;
		register = new Register(this);
		this.parent = parent;
		frame = in;
		try {
		    img = ImageIO.read(imgSrc);
		} catch (IOException e) {}
	}
	

//============================================================================
/**									Methods									*/
	
//--------------------------------------------
/**			Depth modifying methods			*/
	
	public final void moveDepth(int depth){
		parent.moveDepth(this, depth);
	}
	
	public final void upperThen(MovieClip mc){
		moveDepthTo(mc.depth);
	}
	
	public final void moveTop(){
		parent.moveDepth(this, parent.childs.size()-this.depth);
	}

	public final void moveDown(){
		parent.moveDepthTo(0);
	}
	
	public final void moveDepthTo(int depth){
		parent.moveDepth(this, 
				(this.depth > depth)?	(this.depth-depth)*-1:		depth-this.depth);
	}

	public final void moveDepth(MovieClip mc, int depth){
		if(mc.depth + depth > childs.size()-1)
			depth = childs.size()-1 - mc.depth;
		else if (mc.depth + depth < 0)
			depth = mc.depth*-1;
		
		childs.remove(mc.depth);
		childs.add(mc.depth+depth, mc);
		
		Iterator<MovieClip> it = childs.iterator();
		MovieClip akt = null;
		while(it.hasNext()){
			akt = it.next();
			if(depth < 0 && akt.depth < mc.depth && akt.depth >= mc.depth + depth)
				akt.depth++;
			if(depth > 0 && akt.depth > mc.depth && akt.depth <= mc.depth + depth)
				akt.depth--;
		}
		
		mc.depth += depth;
	}

	public final void swap(MovieClip mc){
		parent.swap(depth, mc.depth);
	}
	
	public final void swap(int i){
		parent.swap(depth, i);
	}
	
	public final void swap(MovieClip mc, MovieClip mc2){
		swap(mc.depth, mc2.depth);
	}
	
	public final void swap(int first, int second){
		if(first<0 || second < 0 || first > childs.size()-1 || second > childs.size()-1)
			return;
		
		MovieClip tmp = childs.get(first);
		
		childs.set(first, childs.get(second));
		childs.set(second, tmp);
		
		childs.get(first).depth = second;
		childs.get(second).depth = first;
	}
		

//--------------------------------------------
/**				Tracing methods				*/
	protected void trace(Object o){
		System.out.println(o);
	}
	
	public void traceChilds(){
		synchronized(childs) {
			Iterator<MovieClip> e = childs.iterator();
			while(e.hasNext()){
				trace(e.next().name);
			}
		}
	}
	
//--------------------------------------------
/**				main methods				*/
	public final MovieClip getChild(int depth){
		synchronized(childs) {
			return childs.get(depth);
		}
	}
	
	public final MovieClip getChild(String name){
		MovieClip akt = null;
		synchronized(childs) {
			Iterator<MovieClip> e = childs.iterator();
			while(e.hasNext()){
				akt = e.next();
				if(akt.name.equals(name))
					break;
			}
		}
		return (akt.name.equals(name))?akt:null;
	}
	
	public final boolean loadImage(File imgSrc){
		try {
		    img = ImageIO.read(imgSrc);
		} catch (IOException e) {return false;}
		return (img != null);
	}
	
	public final void addChild(MovieClip mc){
		try {
			if(childs.indexOf(mc) != -1)
				throw new Throwable("DuplicatedChildName");
		} catch (Throwable e) {
			e.printStackTrace();
			return;
		}
		
		this.childs.add(mc);
		sizeOfChilds ++;
		mc.depth = sizeOfChilds-1;
	}
	
	private final void removeGraf(Graphic in){
		graphics.remove(in);
	}  
	
	private final void addGraf(Graphic in){
		graphics.add(in);
	}
	
	protected final void resetHitArea(){
		HitShape = new Area();
		synchronized (graphics) {
			ListIterator<Graphic> it = graphics.listIterator();
			while(it.hasNext()){
				HitShape.add(it.next().getHitArea());
			}
		}
		synchronized (childs){
			Iterator<MovieClip> e = childs.iterator();
			while(e.hasNext()){
				MovieClip akt = e.next();
				akt.resetHitArea();
				HitShape.add(akt.HitShape);
			}
		}
	}
	
	public final void toPaint(Graphics2D g2){
		synchronized (childs) {
			Iterator<MovieClip> e = childs.iterator();
			while(e.hasNext()){
				e.next().paint(g2);
			}
		}
		paint(g2);
	}
}