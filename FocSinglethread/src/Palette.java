import java.awt.Color;
import java.util.ArrayList;

public class Palette {
	private ArrayList<Color> palette;
	private short[] positions;
	private Color[] keyColors;
	
	public Palette(Color a, Color b,Color c, Color d, Color e, short[]pos) {
		keyColors= new Color[5];
		fillColors(a,b,c,d,e);
		palette = new ArrayList<Color>();
		positions=pos;
		createPalette();
	}
	public Color getColor(int n){
		return palette.get(n);
	}
	public int getRed(int n){
		return palette.get(n).getRed();
	}
	public int getGreen(int n){
		return palette.get(n).getGreen();
	}
	public int getBlue(int n){
		return palette.get(n).getBlue();
	}
	public int getAlpha(int n){
		return palette.get(n).getAlpha();
	}
	public void setColor(Color c,byte n) {
		this.palette.set(n, c);
	}
	public void fillColors(Color a,Color b,Color c,Color d,Color e) {
		keyColors[0]=a;
		keyColors[1]=b;
		keyColors[2]=c;
		keyColors[3]=d;
		keyColors[4]=e;
	}
	public void createPalette() {
		short length=0;
		byte keyCol=0;
		Color col;
		float r=0,g=0,b=0,a=0;
		float rstep=0,gstep=0,bstep=0,astep=0,step=0;
		while(length<255) {
			if (length==positions[keyCol]) {
				r=(float) Math.ceil(r);
				g=(float) Math.ceil(g);
				b=(float) Math.ceil(b);
				a=(float) Math.ceil(a);
				step=positions[keyCol+1]-positions[keyCol];
				rstep=(keyColors[keyCol+1].getRed()-keyColors[keyCol].getRed())
						/step;
				gstep=(keyColors[keyCol+1].getGreen()-keyColors[keyCol].getGreen())
						/step;
				bstep=(keyColors[keyCol+1].getBlue()-keyColors[keyCol].getBlue())
						/step;
				astep=(keyColors[keyCol+1].getAlpha()-keyColors[keyCol].getAlpha())
						/step;
				palette.add(keyColors[keyCol]);
				++keyCol;
			}else {
				r+=rstep;
				g+=gstep;
				b+=bstep;
				a+=astep;
				col = new Color((int)Math.round(r), (int)Math.round(g), (int)Math.round(b),(int)Math.round(a));
				palette.add(col);
			}
			++length;
		}
		palette.add(keyColors[keyCol]);
	}
}
