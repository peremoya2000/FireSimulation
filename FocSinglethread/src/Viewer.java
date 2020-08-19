import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Viewer extends Canvas{
	private Fire mainFire;
	private BufferedImage background;
	private BufferStrategy bs;
	public Viewer() {
		try {
			this.background=ImageIO.read(new File("C:\\doom.jpg"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		this.mainFire = new Fire(background);
		this.setVisible(true);	
	}
	public void myPaint() {	
		this.myPaint(bs.getDrawGraphics(), mainFire);		
	}
	public void myPaint(Graphics g,Image img0) {
		g.drawImage(background,0,0,480,480,null);
		g.drawImage(img0,0,0,480,480,null);
		bs.show();
		g.dispose();
	}
	public void paint(Graphics g) {
		
	}
	public void play() {
		this.createBufferStrategy(2);
		bs=this.getBufferStrategy();
		while(true) {
			mainFire.update();
			myPaint();
		}
	}
	public void setThrottle(byte num) {
		mainFire.setThrottle(num);
	}
}
