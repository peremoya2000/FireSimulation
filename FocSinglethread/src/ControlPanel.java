
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ControlPanel extends JPanel{
	private byte throttle=40;
	private JSlider throttleSlider;
	public ControlPanel(Viewer v){
		this.setVisible(true);
		throttleSlider = new JSlider(1, 90);
		throttleSlider.setValue(throttle);
		v.setThrottle(throttle);
		throttleSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				throttle = (byte) throttleSlider.getValue();
				v.setThrottle(throttle);
			}
		});
		throttleSlider.setBounds(140,495,200, 70);
		this.add(throttleSlider);
	}
	public void myPaint() {
		this.paint(this.getGraphics());
	}
}
