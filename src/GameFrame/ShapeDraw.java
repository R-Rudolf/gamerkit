package GameFrame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;


public class ShapeDraw extends Area{
	
	public boolean fill = true;
	public boolean OutLine = true;
	public Color color;
	public Stroke stroke;
	public Paint FillPaint;
	public Paint OutlinePaint;
	public AffineTransform trans;
	public Composite composite;
	
	public Area getHitArea(Shape in){
		Area hit = new Area();
		if(fill){
			hit.add(new Area(in));
		}
		if(OutLine){
			hit.add(new Area(stroke.createStrokedShape(in)));
		}
		return hit;
	}
	
	public void draw(Graphics2D g2, Shape in){
		if(stroke != null){
			Stroke defStroke = g2.getStroke();
			g2.setStroke(stroke);
			draw2(g2, in);
			g2.setStroke(defStroke);
		}else{
			draw2(g2, in);
		}
	}

	private void draw2(Graphics2D g2, Shape in) {
		if(trans != null){
			AffineTransform defTrans = g2.getTransform();
			g2.setTransform(trans);
			draw3(g2, in);
			g2.setTransform(defTrans);
		}else{
			draw3(g2, in);
		}
		
	}

	private void draw3(Graphics2D g2, Shape in) {
		if(color != null){
			Color defColor = g2.getColor();
			g2.setColor(color);
			draw4(g2, in);
			g2.setColor(defColor);
		}else{
			draw4(g2, in);
		}
		
	}

	private void draw4(Graphics2D g2, Shape in) {
		if(composite != null){
			Composite defComposite = g2.getComposite();
			g2.setComposite(composite);
			draw5(g2, in);
			g2.setComposite(defComposite);
		}else{
			draw5(g2, in);
		}
		
	}
	
	private void draw5(Graphics2D g2, Shape in) {
		if(fill){
			if(FillPaint != null){
				//System.out.println("FillPaint on");
				Paint defPaint = g2.getPaint();
				g2.setPaint(FillPaint);
				g2.fill(in);
				g2.setPaint(defPaint);
			}else{
				//System.out.println("FillPaint off");
				g2.fill(in);
			}
		}
		if(OutLine){
			if(OutlinePaint != null){
				//System.out.println("OutlinePaint on");
				Paint defPaint = g2.getPaint();
				g2.setPaint(OutlinePaint);
				g2.fill(stroke.createStrokedShape(in));
				g2.setPaint(defPaint);
			}else{
				//System.out.println("OutlinePaint off");
				g2.draw(in);
			}
		}
	}

	public ShapeDraw(){
	
	}
}
