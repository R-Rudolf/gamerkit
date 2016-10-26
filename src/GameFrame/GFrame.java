/*				created on: 2009.05.24				*/
/**				@author Rudolf Horv?th				*/

package GameFrame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.JFrame;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class GFrame {
	
	public 	int screenWidth = 600, screenHeight = 450, framePerSec = 24, lastMouseX = 0, lastMouseY = 0;
	protected   GCanvas canvas = new GCanvas();
	private 	animate szal = new animate();
	protected   RootMC root = new RootMC(this, null, "root");
	public 		String title = "GameFrame";
	public		JFrame frame;
	protected	boolean mouseDown = false;
	public int lagg;
	
	protected	LinkedList<MovieClip> KeyPressListeners = new LinkedList<MovieClip>();
	protected	LinkedList<MovieClip> KeyReleaseListeners = new LinkedList<MovieClip>();
	protected	LinkedList<MovieClip> KeyTypeListeners = new LinkedList<MovieClip>();
	
	protected	LinkedList<MovieClip> onEnterFrameListeners = new LinkedList<MovieClip>();
	
	protected	LinkedList<MovieClip> onWheelListeners = new LinkedList<MovieClip>();
	protected	LinkedList<MovieClip> onClickListeners = new LinkedList<MovieClip>();
	protected	LinkedList<MovieClip> onPressListeners = new LinkedList<MovieClip>();
	protected	LinkedList<MovieClip> onReleaseListeners = new LinkedList<MovieClip>();
	protected	LinkedList<MovieClip> onReleaseOutsideListeners = new LinkedList<MovieClip>();
	
	protected	LinkedList<MovieClip> onMouseMovedListeners = new LinkedList<MovieClip>();
	protected	LinkedList<MovieClip> onHitListeners = new LinkedList<MovieClip>();
	
	protected	RenderingHints renderingHints = new RenderingHints(null, null);
	public		Dimension ScreenDimension = Toolkit.getDefaultToolkit().getScreenSize();
	
	protected 	GraphicsDevice device;
    public	 	MyDisplayMode displayMode;

    public interface GWindow{    	
    	public abstract void addCanvas(GCanvas in);
    } 
    
    public class MyFrame extends JFrame implements GWindow{
    	
    	public void addCanvas(GCanvas in){
    		this.add(in);
    	}
    	
    	protected MyFrame(){
    		super();
    		setTitle(title);
    		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		setBounds(ScreenDimension.width/2-screenWidth/2, ScreenDimension.height/2-screenHeight/2, screenWidth, screenHeight);
    		
    		add(canvas);
    		addKeyListener(new MyKeyListener());
    		setResizable(false);
    	}
    }
    
	public class MyDisplayMode{
		
		public  boolean isFullScreen = false;
	    private GraphicsDevice device;
	    private DisplayMode originalDM;
		
	    public boolean begin() {
	        isFullScreen = device.isFullScreenSupported();
	        if (isFullScreen) {
	            // Full-screen mode
	            frame.setVisible(false);
	            if(frame.getClass().getName().equals("GameFrame.GFrame$MyFrame")){
		            frame.dispose();
	            }
	            else{
		            frame.dispose();
	            }
	            
	            frame.setUndecorated(true);
	            device.setFullScreenWindow(frame);
	            frame.validate();
	            frame.setVisible(true);
	            canvas.refreshRes();
	            return true;
	        } else {
	        	return false;
	        }
	    }
	    
	    public void end(){
            device.setFullScreenWindow(null);
            isFullScreen = false;
            if(!originalDM.equals(device.getDisplayMode())){
            	device.setDisplayMode(originalDM);
            }
        	frame.dispose();
            frame.setUndecorated(false);
            frame.validate();
            frame.setVisible(true);
            canvas.refreshRes();
	    }
	    
	    public MyDisplayMode(GraphicsDevice device, GFrame inFrame) {
	        super();
	        this.device = device;
	        originalDM = device.getDisplayMode();
	    }
		
	}
	
	public boolean fullScreen(boolean full){
		if(displayMode == null){
	        GraphicsEnvironment env = GraphicsEnvironment.
	            getLocalGraphicsEnvironment();
	        GraphicsDevice[] devices = env.getScreenDevices();
	        // REMIND : Multi-monitor full-screen mode not yet supported
	        for (int i = 0; i < 1 /* devices.length */; i++) {
	        	displayMode = new MyDisplayMode(devices[i], this);
	        	if(!displayMode.isFullScreen)
	        		displayMode.begin();
	        	else
	        		displayMode.end();
	        }
	        return fullScreen(full);
		}else if(full){
			return displayMode.begin();
		}else{
			displayMode.end();
			return true;
		}
	}
	
 	protected boolean changeDisplayMode(){
 		device.getDisplayMode();
 		return true;
 	}
	
	protected MovieClip getMc(int i){
		return root.getChild(i);
	}
	
	protected MovieClip getMc(String name){
		return root.getChild(name);
	}
	
	public void addMC(MovieClip mc){
		root.addChild(mc);
	}
	
	protected void trace(Object o){
		System.out.println(o);
	}
	
	class MyMouseListener extends MouseAdapter{
		
		public void mouseExited(MouseEvent e) {
			super.mouseExited(e);
			lastMouseX = e.getX();
			lastMouseY = e.getY();
		}
		
		public void mousePressed(MouseEvent e) {
			mouseDown = true;
			synchronized (onPressListeners) {
				Iterator<MovieClip> it = onPressListeners.iterator();
				while(it.hasNext()){
					MovieClip akt = it.next();
					if(akt.HitShape.contains(e.getPoint())){
						akt.onPress(e);
					}
				}
			}
			super.mousePressed(e);
		}
		
		public void mouseClicked(MouseEvent e) {
			
			synchronized (onClickListeners) {
				Iterator<MovieClip> it = onClickListeners.iterator();
				while(it.hasNext()){
					MovieClip akt = it.next();
					if(akt.HitShape.contains(e.getPoint())){
						akt.onClick(e);
					}
				}
			}
			super.mouseClicked(e);
		}
		
		public void mouseReleased(MouseEvent e) {
			synchronized (onReleaseListeners) {
				Iterator<MovieClip> it = onReleaseListeners.iterator();
				while(it.hasNext()){
					MovieClip akt = it.next();
					if(akt.HitShape.contains(e.getPoint()))
						akt.onRelease(e);
				}
			}

			synchronized (onReleaseOutsideListeners) {
				Iterator<MovieClip> it = onReleaseOutsideListeners.iterator();
				while(it.hasNext()){
					MovieClip akt = it.next();
					if(!akt.HitShape.contains(e.getPoint()))
						akt.onReleaseOutside(e);
				}
			}
			mouseDown = false;
			super.mouseReleased(e);
		}
	}
	
	class MyMouseMotionListener extends MouseMotionAdapter{
		public void mouseMoved(MouseEvent e) {
			root.mouseMoved(e);
			super.mouseMoved(e);
		}
	}
	
	class MyMouseWheelListener implements MouseWheelListener{
		public void mouseWheelMoved(MouseWheelEvent e) {
			synchronized (onWheelListeners) {
				Iterator<MovieClip> it = onWheelListeners.iterator();
				while(it.hasNext()){
					it.next().onWheelMoved(e);
				}
			}
		}
	}

	class MyKeyListener implements KeyListener{
		
		public void keyPressed(KeyEvent e) {
			synchronized (KeyPressListeners) {
				Iterator<MovieClip> it = KeyPressListeners.iterator();
				while(it.hasNext()){
					it.next().onKeyPressed(e);
				}
			}
		}

		public void keyReleased(KeyEvent e) {
			synchronized (KeyReleaseListeners) {
				Iterator<MovieClip> it = KeyReleaseListeners.iterator();
				while(it.hasNext()){
					it.next().onKeyReleased(e);
				}
			}
		}

		public void keyTyped(KeyEvent e) {
			synchronized (KeyTypeListeners) {
				Iterator<MovieClip> it = KeyTypeListeners.iterator();
				while(it.hasNext()){
					it.next().onKeyTyped(e);
				}
			}
		}
	}
	
	public class GCanvas extends JPanel{
		
		public BufferedImage backbuffer = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR);
		public Graphics2D canvasImg = (Graphics2D)backbuffer.getGraphics();
		int width = 10, height = 10;
		LinkedList<ShapeDraw> toDraw;
		LinkedList<ShapeDraw> drawable;
		Area DrawClip = new Area();
		
		public void changeRes(int w, int h){
			if(w <= 0 || h <= 0)
				return;
			backbuffer = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
			canvasImg = (Graphics2D) backbuffer.getGraphics();
			this.width = w;
			this.height = h;
		}
		
		public void refreshRes(){
			this.changeRes(this.getWidth(), this.getHeight());
		}
		
		public void paintComponent(Graphics g){
			canvasImg.clearRect(0, 0, width, height);
			root.toPaint(canvasImg);
			//DrawClip = new Area();
			g.drawImage(backbuffer, 0, 0, this);
		}
	
		public GCanvas(){
			this.addMouseListener(new MyMouseListener());
			this.addMouseMotionListener(new MyMouseMotionListener());
			this.addMouseWheelListener(new MyMouseWheelListener());
			this.addKeyListener(new MyKeyListener());
		}
	}
	
	class animate extends Thread{
		long begin, end;
		private boolean threadSuspended;
		Thread call;
		
		
		public void pause(){
			threadSuspended = true;
		}
		
		public void start_stop(){
			if(threadSuspended){
				play();
			}else{
				pause();
			}
		}
		
		public void play(){
			threadSuspended = false;
            synchronized(this) {
            	this.notify();
            }
		}
		
		public void run(){
			threadSuspended = false;
			while(true){
				consol = "";
				begin = System.currentTimeMillis();
				synchronized (onEnterFrameListeners) {
					ListIterator<MovieClip> it = onEnterFrameListeners.listIterator();
					while(it.hasNext()){
						it.next().onEnterFrame();
					}
				}
				long stage1 = System.currentTimeMillis();
				consol += "onEnterFrames: "+(stage1-begin)+"\n";
				synchronized (onHitListeners) {
					ListIterator<MovieClip> it = onHitListeners.listIterator();
					while(it.hasNext()){
						it.next();//.resetHitArea();
					}
				}
				long stage2 = System.currentTimeMillis();
				consol += "HitTest: "+(stage2-stage1)+"\n";
				canvas.repaint();
				end = System.currentTimeMillis();
				consol += "ReDraw: "+(end-stage2);
				try{
					if(end-begin  >  1000/framePerSec){
						lagg = -1;
						//trace("lagg: "+(end-begin));
						//sleep(1);
						continue;
					}
					lagg = (int) (end-begin);//((end-begin) == 0)?1:(int) (((1000/framePerSec)-end+begin) / (1000/framePerSec));
					//trace("Total: "+(end-begin)+", estimated: "+(1000/framePerSec));
					//lagg *= 100;
					sleep(1000/framePerSec - (end-begin));
					if (threadSuspended) {
						//call.notify();
	                    synchronized(this) {
	                        while (threadSuspended)
	                            wait();
	                    }
	                }
				}catch(InterruptedException ex) {}
			}
	}
	
	protected class RootMC extends MovieClip{
		
		public RootMC(GFrame in, MovieClip parent, String name) {
			super(in, parent, name);
			x = y = 0;
			w = screenWidth;
			h = screenHeight;
			
			paint = new Function(){
				public void method(Graphics2D g2){
				    g2.setRenderingHints(renderingHints);
				    paint = new Function(){
				    	public void method(Graphics2D g2){
				    		g2.setColor(Color.white);
				    		g2.drawString(lagg+"", 50, 50);
				    	}
				    };
				}
			};
		}
	}
	
	public void initFrame(JFrame inFrame){
		inFrame.setTitle(title);
		inFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		inFrame.setBounds(ScreenDimension.width/2-screenWidth/2, ScreenDimension.height/2-screenHeight/2, screenWidth, screenHeight);
		
		((GWindow)inFrame).addCanvas(canvas);
		inFrame.addKeyListener(new MyKeyListener());
		inFrame.setResizable(false);
		inFrame.setVisible(true);
		canvas.refreshRes();
	}
	
	protected GFrame(){
		super();
		frame = new MyFrame();
		frame.setVisible(true);
		canvas.refreshRes();
		szal.start();
	}
	
	protected GFrame(GWindow inFrame){
		super();
		frame = (JFrame)inFrame;
		initFrame(frame);
		szal.start();
	}
}
