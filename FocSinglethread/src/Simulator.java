import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;

public class Simulator extends JFrame{
	private Viewer v;
	private ControlPanel cp;
	public static void main(String[] args) {
		Simulator s = new Simulator();
	}
	public Simulator() {
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(490,600);//Espai de 70 abaix
		this.setResizable(false);
		
		this.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		this.v= new Viewer();
		v.setSize(490, 500);
		this.cp = new ControlPanel(v);
		cp.setSize(490,600);
		this.add(v,c);
		this.add(cp,c);
		
		cp.myPaint();
		v.play();
	}	

}
