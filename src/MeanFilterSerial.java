import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MeanFilterSerial
{
    public String inputImageName;
    public String outputImageName;
    public int windowWidth;
    public BufferedImage originalImage;
    public BufferedImage resultImage;

    public static void main(String[] args)
    {

        MeanFilterSerial meanFilterSerial = new MeanFilterSerial("./src/image_input.jpg",
                "./src/image_output_mean_serial.jpg", "3");
        meanFilterSerial.readImageInfo();

        long startTimeMeanParallel = System.currentTimeMillis();
        int[][] myArray = meanFilterSerial.meanFilterImpl(meanFilterSerial.settingImage());
        long endTimeMeanParallel = System.currentTimeMillis();
        long durationMeanParallel = endTimeMeanParallel - startTimeMeanParallel;

        System.out.println("The processing time for the mean filter serial is: " + durationMeanParallel + " milliseconds.");
        BufferedImage image = new BufferedImage(myArray.length,myArray[0].length,BufferedImage.TYPE_INT_RGB);

        for (int i=0;i<myArray.length;i++)
        {
            for (int j=0;j<myArray[0].length;j++)
            {
                image.setRGB(i,j,myArray[i][j]);
            }
        }

        try
        {
            ImageIO.write(image,"jpg",new File(meanFilterSerial.outputImageName));
        }
        catch (IOException e)
        {
            System.out.println("The image cannot be processed");
            System.exit(0);
        }
    }

    MeanFilterSerial(String inputImageName, String outputImageName, String windowWidth)
    {
        this.inputImageName=inputImageName;
        this.outputImageName=outputImageName;
        this.windowWidth=Integer.parseInt(windowWidth);
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

    public int[][] settingImage()
    {
        int height = originalImage.getHeight();
        int width = originalImage.getWidth();
        int[][] input = new int[width][height];

        for (int x=0;x<width;x++)
        {
            for (int y=0;y<height;y++)
            {
                input[x][y]=originalImage.getRGB(x,y);
            }
        }
        return input;
    }

    public int[][] meanFilterImpl(int[][] inputImg)
    {
        int[] alpha,red,blue,green;
        int width= originalImage.getWidth();
        int height= originalImage.getHeight();
        int size = windowWidth*windowWidth;
        int[][] output = new int[width][height];

        for (int x=0; x<width; x++)
        {
            for (int y=0; y<height; y++)
            {
                int newAlpha = 0, newRed = 0, newBlue = 0, newGreen = 0, count = 0;
                alpha = new int[size];
                red = new int[size];
                blue = new int[size];
                green = new int[size];

                for (int i=0; i<windowWidth; i++)
                {
                    for (int j=0; j<windowWidth; j++)
                    {
                        if(y+i< height&&x+j < width)
                        {
                            int pixel = inputImg[x+j][y+i];
                            alpha[count] = (pixel >> 24) & 0xff;
                            newAlpha += alpha[count];
                            red[count] = (pixel >> 16) & 0xff;
                            newRed += red[count];
                            blue[count] = pixel & 0xff;
                            newBlue += blue[count];
                            green[count] = (pixel >> 8) & 0xff;
                            newGreen += green[count];
                            count++;
                        }
                    }
                }

                int newPixels = newAlpha<<24 | (newRed/size)<<16 | (newGreen/size)<<8 | (newBlue/size);
                output[x][y] = newPixels;
            }
        }

        return output;
    }
}

