import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MedianFilterParallel
{
    public BufferedImage originalImage;
    public BufferedImage resultImage;
    public String inputImageName;
    public String outputImageName;
    public int windowWidth;

    public static void main(String[] args)
    {
        MedianFilterParallel medianFilterParallel = new MedianFilterParallel("./src/image_input.jpg",
                "./src/image_output_median_parallel.jpg","5");
        medianFilterParallel.readImageInfo();

        long startTimeMedianParallel = System.currentTimeMillis();
        final ForkJoinPool pool = new ForkJoinPool();
        ArrayList<int[]> arr = medianFilterParallel.settingImage();
        medianFilterThread medianFilterThread = new medianFilterThread(arr, medianFilterParallel.outputImageName, medianFilterParallel.windowWidth, 0, arr.size());
        pool.invoke(medianFilterThread);
        ArrayList<int[]> output = medianFilterThread.output;
        long endTimeMedianParallel = System.currentTimeMillis();
        long durationMedianParallel = endTimeMedianParallel - startTimeMedianParallel;

        System.out.println("The processing time for the mean filter parallel is: " + durationMedianParallel + " milliseconds.");
        BufferedImage resultImage = new BufferedImage(output.size(), output.get(0).length, BufferedImage.TYPE_INT_RGB);



        for (int i=0;i<output.size();i++)
        {
            for (int j=0;j<output.get(0).length;j++)
            {
                resultImage.setRGB(i,j,output.get(i)[j]);
            }
        }

        try
        {
            ImageIO.write(resultImage,"jpg",new File(medianFilterParallel.outputImageName));
        }

        catch (IOException e)
        {
            System.out.println("Picture cannot be processed.");
            System.exit(0);
        }
    }

    public MedianFilterParallel(String inputImageName, String outputImageName, String windowWidth)
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
            System.out.println("Picture not found");
            //System.out.println(e);
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
                pixels[y]=originalImage.getRGB(x,y);
            }
            input.add(pixels);
        }
        return input;
    }



    public static class medianFilterThread extends RecursiveTask<ArrayList<int[]>>
    {
        int hi,lo;
        String outputImageName;

        private static final int SEQUENTIAL_CUTOFF = 500;
        ArrayList<int[]> input;
        int windowWidth;
        ArrayList<int[]> output = new ArrayList<>();

        public medianFilterThread(ArrayList<int[]> inputImg, String outputImageName, int windowWidth, int lo, int hi)
        {
            this.input=inputImg;
            this.outputImageName=outputImageName;
            this.windowWidth=windowWidth;
            this.hi=hi;
            this.lo=lo;
        }

        public ArrayList<int[]> compute()
        {
            int width= input.size();
            int height= input.get(0).length;
            int size = windowWidth*windowWidth;
            int median = (size)/2;
            System.out.println(hi+" "+lo);

            if (hi-lo < SEQUENTIAL_CUTOFF)
            {
                for (int i=lo; i<hi; i++)
                {
                    int[] pixels = new int[height];
                    for (int j=0; j<height; j++)
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
                                if (x + i < width && y + j < height)
                                {
                                    int pixel = input.get(i + x)[j + y];
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
            }

            else
            {
                int middle = (hi+lo)/2;
                medianFilterThread left = new medianFilterThread(input, outputImageName, windowWidth, lo, middle);
                medianFilterThread right = new medianFilterThread(input, outputImageName, windowWidth, middle, hi);
                left.fork();
                ArrayList<int[]> rightAns = right.compute();
                ArrayList<int[]> leftAns = left.join();
                output.addAll(leftAns);
                output.addAll(rightAns);
            }
            return output;
        }
    }
}