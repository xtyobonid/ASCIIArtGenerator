import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class ASCIIArtGenerator {
	
	public static final String CHARS = " !\"#$%&\\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[]^_`abcdefghijklmnopqrstuvwxyz{|}~";
	
	private static BufferedImage input;
	public static int[] charBrightness;
	
	public static void main(String[] args) throws IOException {
		//get brightnesses of all images
		File brightness = new File("brightness.txt");
		if (!brightness.exists()) {
        	brightness.createNewFile();
        	getCharacterBrightnesses(brightness);
        } else {
        	loadCharacterBrightnesses(brightness);
        }
		
		//load necessary variables
		input = ImageIO.read(new File("input.png"));
		File output = new File("art.txt");
		if (!output.exists()) {
        	output.createNewFile();
        } else {
        	output.delete();
        	output.createNewFile();
        }
		PrintWriter out = new PrintWriter(output);
		
		//manually found, automatic detection would be cool
		int darkestCharacter = 170;
		int brightestCharacter = 255;
		
		
		//loop through input pixel by pixel, P.S. remember j is horizontal and i represents a row of pixels
		for (int i = 0; i < input.getHeight(); i+=2) {
			for (int j = 0; j < input.getWidth(); j++) {
				double pixelBrightness = getPixelBrightness(input, j, i);
				
				//compress pixel brightness to fit betw. darkest and brightest character
				pixelBrightness /= (255.0/((double)brightestCharacter - (double)darkestCharacter));
				pixelBrightness += (double)darkestCharacter;
				
				//find closest character and print it
				int index = getClosestCharacterIndex(pixelBrightness);
				out.print(CHARS.charAt(index));
			}
			out.println();
		}
		out.close();
	}

	private static int getClosestCharacterIndex(double pixelBrightness) {
		double closestValueDistance = Math.abs((double)charBrightness[0] - pixelBrightness);
		int closestValueIndex = 0;
		for (int i = 1; i < charBrightness.length; i++) {
			double distance = Math.abs((double)charBrightness[i] - pixelBrightness);
			if (distance < closestValueDistance) {
				closestValueDistance = distance;
				closestValueIndex = i;
			}
		}
		return closestValueIndex;
	}

	private static void loadCharacterBrightnesses(File brightness) throws FileNotFoundException {
		Scanner in = new Scanner(brightness);
		charBrightness = new int[95];
		for (int i = 0; i < charBrightness.length; i++) {
			int b = in.nextInt();
			in.nextLine();
			charBrightness[i] = b;
		}
		in.close();
	}

	private static void getCharacterBrightnesses(File brightness) throws IOException {
		PrintWriter out = new PrintWriter(brightness);
		charBrightness = new int[95];
		for (int i = 0; i < charBrightness.length; i++) {
			BufferedImage read = ImageIO.read(new File("ConsolaFontPictures/" + i + ".png"));
			charBrightness[i] = getImageBrightness(read);
			out.println(charBrightness[i]);
		}
		out.close();
	}
	
	private static int getImageBrightness(BufferedImage image) {
		int brightnessTotal = 0;
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				brightnessTotal += getPixelBrightness(image, i, j);
			}
		}
		int brightnessAverage = brightnessTotal/(image.getWidth()*image.getHeight());
		
		return brightnessAverage;
	}
	
	private static int getPixelBrightness(BufferedImage image, int i, int j) {
		int clr = image.getRGB(i, j);
        int red =   (clr & 0x00ff0000) >> 16;
        int green = (clr & 0x0000ff00) >> 8;
        int blue =   clr & 0x000000ff;
        
        //negative is dark, positive is bright
        int brightness = ((red + green + blue)/3);
        return brightness;
	}
}
