package Game;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import GameFrame.GFrame;
import GameFrame.GFrame.GCanvas;
import GameFrame.GFrame.GWindow;


@SuppressWarnings("serial")
class Window extends JFrame implements GWindow{
	
	protected GFrame parent;
	
	JPanel panel1 = new JPanel();
	public		JMenuBar menuBar = new JMenuBar();
	JMenu SettingsMenu = new JMenu("Settings");
	protected	JMenuItem FullScreenMenuItem = new JMenuItem("FullScreen");

    class MyActionListener implements ActionListener{
    	@Override
    	public void actionPerformed(ActionEvent e) {
            JMenuItem source = (JMenuItem)(e.getSource());
    		if(source == FullScreenMenuItem){
    			parent.fullScreen(true);
    		}
    	}
    }
    
    public void addCanvas(GCanvas in){
    	add(in);
    }

   public Window(){
    	JFrame.class.getConstructors();
    	SettingsMenu.add(FullScreenMenuItem);
		menuBar.add(SettingsMenu);
		setJMenuBar(menuBar);
		FullScreenMenuItem.addActionListener(new MyActionListener());
    }
	
	public void setParent(GFrame inFrame) {
		parent = inFrame;
	}
	
}