import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class MedianFilterSerial
{
        public String inputImageName;
        public String outputImageName;
        public int windowWidth;
        public BufferedImage originalImage;
        public BufferedImage resultImage;

    public static void main(String[] args)
    {
        MedianFilterSerial medianFilterSerial = new MedianFilterSerial("./src/image_input.jpg",
                "./src/image_output_median_serial.jpg", 3);
        medianFilterSerial.readImageInfo();
        double startTime = System.currentTimeMillis();
        ArrayList<int[]> myArrayList = medianFilterSerial.medianFilterImpl(medianFilterSerial.settingImage());
        double endTime = System.currentTimeMillis();


        System.out.println("Median Filter Serial took " + (endTime - startTime) + " milliseconds.");
        BufferedImage image = new BufferedImage(myArrayList.size(),myArrayList.get(0).length,BufferedImage.TYPE_INT_RGB);


        for (int i=0;i<myArrayList.size();i++)
        {
            for (int j=0;j<myArrayList.get(0).length;j++) {
                image.setRGB(i, j, myArrayList.get(i)[j]);
            }
        }
        try
        {
            ImageIO.write(image,"jpg",new File(medianFilterSerial.outputImageName));
        }
        catch (IOException e)
        {
            System.out.println("The image cannot be processed");
            System.exit(0);
        }
    }

    public MedianFilterSerial(String inputImageName, String outputImageName, int windowWidth) {
        this.inputImageName = inputImageName;
        this.outputImageName = outputImageName;
        this.windowWidth = windowWidth;
    }


    public void readImageInfo()
    {
        try
        {
            originalImage = ImageIO.read(new File(inputImageName));

        }
        catch (IOException e)
        {

            System.out.println("The image cannot be retrieved");
            System.exit(0);
        }
            System.out.println(originalImage);
            System.out.println(inputImageName+" "+outputImageName+" "+windowWidth);
        }


    public ArrayList<int[]> settingImage()
    {
        int height = originalImage.getHeight();
        int width = originalImage.getWidth();
        ArrayList<int[]> input = new ArrayList<>();


        for (int x=0;x<width;x++)
        {
            int[] pixels = new int[height];
            for (int y=0;y<height;y++)
            {
                pixels[y]= originalImage.getRGB(x,y);
            }

            input.add(pixels);
        }
        return input;

    }
    public ArrayList<int[]> medianFilterImpl(ArrayList<int[]> inputImg)
    {
        int size = this.windowWidth * this.windowWidth;
        int median = (size/2);
        ArrayList<int[]> output = new ArrayList<>();


        for (int i=0;i<originalImage.getWidth();i++)
        {
            int[] pixels = new int[originalImage.getHeight()];
            for (int j=0;j<originalImage.getHeight();j++)
            {
                int count=0;
                int[] alpha = new int[size];
                int[] red = new int[size];
                int[] blue = new int[size];
                int[] green = new int[size];


                for (int x=0;x<windowWidth;x++)
                {
                    for (int y=0;y<windowWidth;y++)
                    {
                        if (x + i < originalImage.getWidth() && y + j < originalImage.getHeight())
                        {
                            int pixel = inputImg.get(i + x)[j + y];
                            alpha[count] = (pixel >> 24) & 0xff;
                            red[count] = (pixel >> 16) & 0xff;
                            blue[count] = pixel & 0xff;
                            green[count] = (pixel >> 8) & 0xff;
                            count++;
                        }
                    }
                }
                Arrays.sort(alpha);
                Arrays.sort(red);
                Arrays.sort(blue);
                Arrays.sort(green);
                pixels[j] = alpha[median]<<24 | red[median]<<16 | green[median]<<8 | blue[median];
            }
            output.add(pixels);
        }
        return output;
    }
}
