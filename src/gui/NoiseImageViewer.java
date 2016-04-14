package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.sound.sampled.*;
import javax.sound.sampled.AudioFileFormat.Type;

import sun.audio.AudioPlayer;
import java.util.Random;

public class NoiseImageViewer {

    JPanel gui;
    /** Displays the image. */
    JLabel imageCanvas;
    static int width=1440;
    static int height=900;
    static AudioInputStream as = null;
    static AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, 192000, 16, 2, 32, 25, false);
    static SourceDataLine sdl;
    static ByteArrayOutputStream baos;
    static Clip clip;
    static File soundfile = new File("D:\\workspace\\sound.wav");
    static int itr = 0;
    static int selection=1;
    private static ActionListener animate;
    static AudioInputStream din = null;
    static byte[] audiobytes = null;
    static int bytesperframe;
    static File wavfile = new File("D:\\workspace\\test2.wav");
    
   private static final String MOVE_UP = "move up";
   private static final String MOVE_DOWN = "move down";
   private static final String MOVE_RIGHT = "move right";

    /** Set the image as icon of the image canvas (display it). */
    public void setImage(Image image) {
        imageCanvas.setIcon(new ImageIcon(image));
    }

    public void initComponents() {
        if (gui==null) { 
            gui = new JPanel(new BorderLayout());
            gui.setBorder(new EmptyBorder(5,5,5,5));
            imageCanvas = new JLabel();

            JPanel imageCenter = new JPanel(new GridBagLayout());
            imageCenter.add(imageCanvas);
            
            JScrollPane imageScroll = new JScrollPane(imageCenter);
            imageScroll.setPreferredSize(new Dimension(width+20,height+20));
            gui.add(imageScroll, BorderLayout.CENTER);
            
            gui.getInputMap().put(KeyStroke.getKeyStroke("UP"), MOVE_UP);
            gui.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), MOVE_DOWN);
            gui.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), MOVE_RIGHT);
            
            gui.getActionMap().put(MOVE_UP, new IncreaseSelectionAction());
            gui.getActionMap().put(MOVE_DOWN, new DecreaseSelectionAction());
            gui.getActionMap().put(MOVE_RIGHT, new IncrementFrameAction());
            
            
            
        }
        
//        Path dir = Paths.get("D:\\workspace\\");
//        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*")) {
//            System.out.println(dir.toString());
//        	for (Path file : stream) {
//                System.out.println(file);
//            }
//        } catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
		try {
			AudioInputStream in= AudioSystem.getAudioInputStream(wavfile);
			
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false);
			din = AudioSystem.getAudioInputStream(decodedFormat, in);
			bytesperframe = din.getFormat().getFrameSize();
			if (bytesperframe == AudioSystem.NOT_SPECIFIED){
				bytesperframe = 1;
			}
			int numbytes = 1024*bytesperframe;
			audiobytes = new byte[numbytes];
			
			
			
			
			
			
		} catch (UnsupportedAudioFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        
    }
    
  

    public Container getGui() {
        initComponents();
        return gui;
    }

    public static Image getRandomImage(Random random) {
        int w = width;
        int h = height;
        BufferedImage bi = new BufferedImage(
                w,h,BufferedImage.TYPE_3BYTE_BGR);

               
        //System.out.println(bi.getHeight()+" height "+bi.getWidth()+" width");
        //System.out.println(Integer.toHexString(bi.getRGB(0, 0))+" is pixel at 1,1");
        
        int r,x,y; // red component 0...255
		int g =0; // green component 0...255
		int b =0; // blue component 0...255
		int a =0; // blue component 0...255
		int col;// = (a << 24) | (r << 16) | (g << 8) | b;
		//System.out.println(Integer.toHexString(col)+" is col");
        


		int A = random.nextInt(width)+1;
		int B = random.nextInt(height)+1;
		r =random.nextInt(random.nextInt(256)+1); // red component 0...255
		g =random.nextInt(random.nextInt(256)+1); // green component 0...255
		b =random.nextInt(random.nextInt(256)+1); // blue component 0...255
		a = random.nextInt(random.nextInt(256)+1);//random.nextInt(random.nextInt(256)+1); // blue component 0...255
		col = (a << 24) | (r << 16) | (g << 8) | b;
		int col2 = 0;
		//int col2 = (111 <<24) | (222 << 16) | (33 << 8) | (44);
	
		switch(selection) {
		
		case 1:
				//weird thing 1 --diamonds
				for(int i=0;i<bi.getWidth();i++){
					for(int j=0;j<bi.getHeight();j++){
						bi.setRGB(i, j, col2);
						col2 = (a << 24) | ((r+i*j)%256 << 16) | ((g+i*j)%256 << 8) | (b+i*j)%256;
					}
				}
				break;
				
		case 2:
				//weird thing 2 --blocks
				for(int i=0;i<bi.getWidth();i++){
					for(int j=0;j<bi.getHeight();j++){
						bi.setRGB(i, j, col2);
						col2 = (a << 24) | ((r*i^2+j)%256 << 16) | (g << 8) | ((b*i^2+j)%256);
					}
				}
				break;
				
		case 3:
				//weird thing 3 --square wave sound
				r =0; // red component 0...255
				g =0; // green component 0...255
				b =0; // blue component 0...255
				for(int i=0;i<bi.getWidth();i++){
					for(int j=0;j<bi.getHeight();j++){
						bi.setRGB(i, j, col2);
						col2 = (a << 24) | ((r*j^3)%256 << 16) | (g%256 << 8) | b%256;
						r+=5;
						g+=4;
						b+=11;
					}
				}
				break;
				
		case 4:
				//weird thing 4 --diamonds
				for(int i=0;i<bi.getWidth();i++){
					for(int j=0;j<bi.getHeight();j++){
						bi.setRGB(i, j, col2);
						col2 = (0xFF << 24) | ((j*i)%256 << 16) | ((j/(i+1))%256 << 8) | ((j^i))%256;
					}
					
				}
				break;
				
		case 5:
				//lissojous
				for(int i=0;i<(bi.getHeight()*bi.getWidth());i++)
				{
					x = (int) ((bi.getWidth()/2)*Math.sin(A*i+Math.PI/2))+(bi.getWidth()/2);
					y = (int) ((bi.getHeight()/2)*Math.sin(B*i))+(bi.getHeight()/2);
					//if(i > (bi.getHeight()*bi.getWidth())/2)
						//bi.setRGB(y%bi.getWidth(), x%bi.getHeight(), 6666666);
					//else {
						
						
						bi.setRGB(x%bi.getWidth(), y%bi.getHeight(), col);
						bi.setRGB((x+1)%bi.getWidth(), (y+1)%bi.getHeight(), col & 0xA5A5);
						//bi.setRGB((x-1)%bi.getWidth(), (y-1)%bi.getHeight(), col & 0x5A5A);
					//}
		
				}
				break;
				
		case 6:
				//square wave
				boolean sw = false;
				int bb = random.nextInt(random.nextInt(100)+1);
				bb++;
				int subtractor = -1;
				int subtractor2 = 0;
				for(int i=0;i<bi.getWidth();i++){
						if(i%15 == 0) sw = !sw;
						subtractor++;
						subtractor=subtractor%255;
					for(int j=0;j<bi.getHeight();j++){
						
						if(j%bb == 0) sw = !sw;
						//System.out.println("sw is "+sw+" and j is "+j+" j mod 100 is "+j%100);
						
						if(sw){
							col2 = (a << 24) | (255-subtractor << 16) | (255-subtractor << 8) | 255-subtractor;
							bi.setRGB(i, j, col2);
						}
						else {
							col2 = (a << 24) | (subtractor2 << 16) | (subtractor2 << 8) | subtractor2;
							bi.setRGB(i, j, col2);
							subtractor2++;
							subtractor2=subtractor2%255;
						}
					}
				}
				break;
		case 7:
			//Linear pattern
			  for(int i=0; i<bi.getWidth(); i++)
			  {
					for(int j=0; j<bi.getHeight();j++){
						r =random.nextInt(random.nextInt(256)+1); // red component 0...255
						g =random.nextInt(random.nextInt(256)+1); // green component 0...255
						b =random.nextInt(random.nextInt(256)+1); // blue component 0...255
						a = 0xff;//random.nextInt(random.nextInt(256)+1); // blue component 0...255
						col2 = (a << 24) | (r << 16) | (g << 8) | b;
						x = (int) (127*Math.sin(3*i+Math.PI/2))+127;
						y = (int) (127*Math.sin(4*j))+127;
						//System.out.println("x "+x+" y "+y);
						try{
							bi.setRGB(i, j, col2);
						}catch(ArrayIndexOutOfBoundsException e)
						{
							System.out.println("ERROR: "+x+" "+y);
						}
					}
				}
			  break;
		case 8:
			//read sound file
			byte[] rgb = {0,0,0};
			//System.out.println(din.getFrameLength()+" frame length");

			int numbytesread = 0;
			int numframesread = 0;
			int totalframesread = 0;
			
//			try {
//				while((numbytesread = din.read(audiobytes)) != -1){
//					numframesread = numbytesread / bytesperframe;
//					totalframesread += numframesread;
//					System.out.println("numbytesread = "+numbytesread+"\nnumframesread = "+numframesread);
//				}
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}

			try {
				if(din.available() == 0){
					try {
					 din.close();
					
					AudioInputStream in = AudioSystem.getAudioInputStream(wavfile);
					AudioFormat baseFormat = in.getFormat();
					AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
		                    baseFormat.getSampleRate(),
		                    16,
		                    baseFormat.getChannels(),
		                    baseFormat.getChannels() * 2,
		                    baseFormat.getSampleRate(),
		                    false);
					din = AudioSystem.getAudioInputStream(decodedFormat, in);
					} catch (UnsupportedAudioFileException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
			
			 for(int i=0; i<bi.getWidth(); i++)
			  {
				numbytesread = din.read(audiobytes);
				numframesread = numbytesread / 4096;
				totalframesread += numframesread;
				//System.out.println("audiobytes size="+audiobytes.length+"\naudiobytes["+i+"]"+audiobytes[i]+"\nnumbytesread="+numbytesread+"\nnumframesread="+numframesread+"\ntotalframesread="+totalframesread);
				int jj=0;
				for(int j=0; j<bi.getHeight();j++){
					try{
						col2 = (0xFF << 24) | Math.abs(audiobytes[jj]) << 16 | Math.abs(audiobytes[jj+1]) << 8 | Math.abs(audiobytes[jj+2]);	
						jj+=3;
						//System.out.println("\tcol2= "+Integer.toHexString(col2));
						bi.setRGB(i, j, col2);
					}catch(ArrayIndexOutOfBoundsException e)
					{
						System.out.println("ERROR: "+i+" "+j);
					}
				}
				
			  }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 break;
		}
		
		
       
		//System.out.println("bi rgb = "+bi.getRGB(0, 0));
		
		File f = new File("D:\\workspace\\test1.jpg");
		//File f2 = new File("C:\\Users\\cvogel.NDC\\Desktop\\test\\test.wav");
		
		try {
			ImageIO.write(bi, "bmp", f);
			try{
            	InputStream in = new FileInputStream("D:\\workspace\\test1.jpg");
            	//System.out.println("in byte = ");
            	as = new AudioInputStream(in, format, 4*bi.getHeight()*bi.getWidth());
            	
            	//AudioSystem.write(as, AudioFileFormat.Type.WAVE, f2);
            	//as = ais;
            	
            	
//				DataBufferInt db = (DataBufferInt) bi.getRaster().getDataBuffer();
//				ByteBuffer bb = ByteBuffer.allocate(db.getSize()*4);
//				IntBuffer intBuffer = bb.asIntBuffer();
//				intBuffer.put(db.getData());
		
//            	byte[] t = bb.array();

            	
		
//				try {
//					clip = AudioSystem.getClip();
//				} catch (LineUnavailableException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}	
//				
//				System.out.println(clip);
//				
//				try {
//					clip.open(format, t, 0, t.length);
//				} catch (LineUnavailableException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				//sdl.write(t, 0, t.length);
				//AudioSystem.getClip().open(format, t, 0, t.length);;
				
		
            	

            }catch(SecurityException fnf){
            	
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return bi;
    }

    public static void main(String[] args) throws Exception {
    	
    	
    	
    	
    	
    	
    	
    	
        Runnable r = new Runnable() {
            @Override
            public void run() {
                JFrame f = new JFrame("Noise Image Viewer");
                // TODO Fix kludge to kill the Timer
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                final NoiseImageViewer viewer = new NoiseImageViewer();
                f.setContentPane(viewer.getGui());

                f.pack();
                f.setLocationByPlatform(true);
                f.setVisible(true);
                
               

               // try {
                	
					//sdl = AudioSystem.getSourceDataLine(format);
					//sdl.open();
					//sdl.start();
				//} catch (LineUnavailableException e) {
					//e.printStackTrace();
				//}
                
                
                animate = new ActionListener() {

                Random random = new Random();
                    
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        viewer.setImage(getRandomImage(random));
                        AudioPlayer.player.start(as);
                        
                    	AudioPlayer.player.stop();
                    	
                        //clip.start();
                        
                    }
                };
       	   	 //Timer timer = new Timer(600,animate);
              //timer.start();
              itr++;
     	   	
                //AudioPlayer.player.stop(as);
               // clip.stop();
            }
        };
        SwingUtilities.invokeLater(r);
       
    }
    

    
 private class IncreaseSelectionAction extends AbstractAction {
    	
    	public void actionPerformed(ActionEvent e){
    		//selection = 8;
    		if (selection <8) {
    			selection++;
    			animate.actionPerformed(e);
    		}
    		
    	}
    }
   private class DecreaseSelectionAction extends AbstractAction {
   	
   	public void actionPerformed(ActionEvent e){
   		if (selection >1) {
   			selection--;
   			animate.actionPerformed(e);
   		}
   		
   	}
   }
   private class IncrementFrameAction extends AbstractAction {
	   	
	   	public void actionPerformed(ActionEvent e){
	   		
	   		animate.actionPerformed(e);
//	   	 Timer timer = new Timer(600,animate);
//         timer.start();
//         itr++;
	   	
	   }
   }
    
    
    
    
}