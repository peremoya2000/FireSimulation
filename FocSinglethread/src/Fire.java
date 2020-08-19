import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Fire extends BufferedImage{
	//A mask used to place fire on certain parts of the image(borders)
	private BufferedImage convolutioned;
	private byte[] convolutionedpx;
	//Values from 0 to 255 representing heat
	private short[][] heatmap;
	//Color bytes for final image
	private byte[] pixels;
	//Palette objecte to translate a heat value to a color
	private Palette palette;
	private int height;
	private int width;
	private int size;
	//Probability of a new spark
	private byte throttle;
	
	public Fire(BufferedImage back) {
		super(500,500,BufferedImage.TYPE_4BYTE_ABGR);
		this.height=500;
		this.width=500;
		this.size=this.getData().getDataBuffer().getSize();
		this.heatmap=new short[height][width];
		this.pixels=new byte[size];
		for (int i=0; i<size; ++i) {
			pixels[i]=0;
		}
		this.getRaster().setDataElements(0, 0, width, height, pixels);	
		this.convolutioned=deepCopy(back,width,height);
		detectAreas();
		short[] pos= {0,80,105,150,255};
		this.palette= new Palette(
				new Color(0,0,0,1),
				new Color(255,60,0,180), 
				new Color(250,120,10,220),
				new Color(255,175,50,200), 
				new Color(255,255,150,255), 
				pos);
	}
	
	public void update() {
		placeHeat(this.throttle/9.5f);
		placeHeatBase(this.throttle);
		spreadHeat();
		short newT;
		for (int i=height-2; i>0; --i) {			
			for (int j=1; j<width-1; ++j) {	
				newT=(short) 
				((heatmap[i][j-1]*1.21f	//left
				+heatmap[i][j]			//self
				+heatmap[i][j+1]*1.2f	//right
				+heatmap[i+1][j-1]*0.55f//bottom left
				+heatmap[i+1][j]*1.48f	//bottom
				+heatmap[i+1][j+1]*0.55f)//bottom right
				/5.58f-0.26f);
				
				newT=(newT>255 ? 255 : newT);
				newT=(newT<0 ? 0 : newT);
				//if (newT>20) System.out.println("i:"+i+", j: "+j+", temp:"+newT);
				heatmap[i][j]=newT;
			}
		}
		tonemap();
	}
	
	public void placeHeat(float prob) {
		prob/=100;
		int pos;
		for (int i=0; i<height; ++i) {			
			for (int j=0; j<width; ++j) {
				pos=(i*width+j)*3;
				if((convolutionedpx[pos]>20)&& Math.random()<prob) {
					heatmap[i][j]=255;
				}else {
					heatmap[i][j]/=1.2f;
				}
			}
		}
	}
	
	public void placeHeatBase(float prob) {
		prob/=100;
		int h=height-50;
		for (int i=1; i<width; ++i) {			
			if(Math.random()<prob) {
				heatmap[h][i]=255;
			}else {
				heatmap[h][i]/=1.2;
			}
		}
	}
	
	public void spreadHeat() {
		for (int i=1; i<height-1; ++i) {			
			for (int j=1; j<width-2; ++j) {
				if(heatmap[i][j]>240) {
					--heatmap[i][j];
					if (heatmap[i][j-1]<200)heatmap[i][j-1]+=55;	
					if (heatmap[j][j+1]<200)heatmap[i][j+1]+=55;
				}
			}
		}
	}
	
	public void tonemap() {
		int newR,newG,newB,newA,pos;
		Color c;
		for (int i=0; i<height; ++i) {			
			for (int j=0; j<width; ++j) {
				pos=(i*width+j)*4;
				
				c = palette.getColor(heatmap[i][j]);
				newR=c.getRed();
				newG=c.getGreen();
				newB=c.getBlue();
				newA=c.getAlpha();
				

				newR=Math.min(255, newR);
				newG=Math.min(255, newG);
				newB=Math.min(255, newB);
				newR=Math.max(0, newR);
				newG=Math.max(0, newG);
				newB=Math.max(0, newB);
				
				pixels[pos]=(byte) newR;
				pixels[pos+1]=(byte) newG;
				pixels[pos+2]=(byte) newB;
				pixels[pos+3]=(byte) newA;
			}
		}
		this.getRaster().setDataElements(0, 0, width, height,pixels);
	}
	
	public void detectAreas() {
		int w=convolutioned.getWidth();
		int h=convolutioned.getHeight();
		convolutionedpx= new byte[size];
		byte[] pixels=copyBytes(convolutioned);
		byte[][] conv= {{-1,-1,-1},{0,0,0},{1,1,1}};
		int[][]trans= {{-w*3-3,-w*3,-w*3+3},{-3,0,3},{w*3-3,w*3,w*3+3}};
		int newR,newG,newB,pos;
		for (int i=3; i<h-3; ++i) {			
			for (int j=3; j<w-3; ++j) {
				pos=(i*w+j)*3;		
				newR=newG=newB=0;		
				for(byte k=0; k<3;++k) {
					for(byte l=0;l<3;++l) {
						newR+=Byte.toUnsignedInt(pixels[pos+trans[k][l]+2])*conv[k][l];
						newG+=Byte.toUnsignedInt(pixels[pos+trans[k][l]+1])*conv[k][l];
						newB+=Byte.toUnsignedInt(pixels[pos+trans[k][l]])*conv[k][l];
					}
				}
				newR=(newR/4>255 ? 255 : newR/4);
				newG=(newG/4>255 ? 255 : newG/4);
				newB=(newB/4>255 ? 255 : newB/4);
				newR=Math.max(0, newR);
				newG=Math.max(0, newG);
				newB=Math.max(0, newB);
				convolutionedpx[pos]=(byte) newR;
				convolutionedpx[pos+1]=(byte) newG;
				convolutionedpx[pos+2]=(byte) newB;	
			}
		}
		convolutioned.getRaster().setDataElements(0, 0, w, h, convolutionedpx);
	}
	
	public byte[] copyBytes(BufferedImage bi) {
		DataBufferByte db=(DataBufferByte)bi.getData().getDataBuffer();
		int size=db.getSize();
		byte[] newarray = new byte[size];	
		byte[] originalpx = db.getData();
		for (int i=0; i<size; ++i) {
			newarray[i]=originalpx[i];
		}
		return newarray;	
	}
	public BufferedImage deepCopy(BufferedImage bi, int wi, int he) {
		Image tmp = bi.getScaledInstance(wi, he, Image.SCALE_DEFAULT); 
		BufferedImage resized = new BufferedImage(wi,he,BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp,0,0,null);
		g2d.dispose();
		return resized;
	}
	public void setThrottle(byte num) {
		this.throttle=num;
	}
	
}
