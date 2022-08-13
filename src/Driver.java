import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public class Driver {

    public static final String SOURCE_FILE = "./src/image_input.jpg";
    public static final String DESTINATION_FILE_ONE = "./src/image_output_mean_serial.jpg";
    public static final String DESTINATION_FILE_TWO = "./src/image_output_median_serial.jpg";
    public static final String DESTINATION_FILE_THREE = "./src/image_output_mean_parallel.jpg";
    public static final String DESTINATION_FILE_FOUR = "./src/image_output_median_parallel.jpg";


    public static void main(String[] args) throws IOException {

        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

        MeanFilterSerial meanFilterSerial = new MeanFilterSerial(originalImage, resultImage);
        MedianFilterSerial medianFilterSerial = new MedianFilterSerial(originalImage, resultImage);
        MeanFilterParallel meanFilterParallel = new MeanFilterParallel(originalImage, resultImage, 4);
        MedianFilterParallel medianFilterParallel = new MedianFilterParallel(originalImage, resultImage, 2);
        meanFilterParallel.invoke();
        medianFilterParallel.invoke();

        long startTimeMeanSerial = System.currentTimeMillis();
        long startTimeMedianSerial = System.currentTimeMillis();
        long startTimeMeanParallel = System.currentTimeMillis();
        long startTimeMedianParallel = System.currentTimeMillis();

        meanFilterSerial.recolorSingleThreaded(originalImage, resultImage);
        medianFilterSerial.recolorSingleThreaded(originalImage, resultImage);
        meanFilterParallel.compute();
        medianFilterParallel.compute();


        long endTimeMeanSerial = System.currentTimeMillis();
        long endTimeMedianSerial = System.currentTimeMillis();
        long endTimeMeanParallel = System.currentTimeMillis();
        long endTimeMedianParallel = System.currentTimeMillis();

        long durationMeanSerial = endTimeMeanSerial - startTimeMeanSerial;
        long durationMedianSerial = endTimeMedianSerial - startTimeMedianSerial;
        long durationMeanParallel = endTimeMeanParallel - startTimeMeanParallel;
        long durationMedianParallel = endTimeMedianParallel - startTimeMedianParallel;



        File outputFileOne = new File(DESTINATION_FILE_ONE);
        File outputFileTwo = new File(DESTINATION_FILE_TWO);
        File outputFileThree = new File(DESTINATION_FILE_THREE);
        File outputFileFour = new File(DESTINATION_FILE_FOUR);
        ImageIO.write(resultImage, "jpg", outputFileOne);
        ImageIO.write(resultImage, "jpg", outputFileTwo);
        ImageIO.write(resultImage, "jpg", outputFileThree);
        ImageIO.write(resultImage, "jpg", outputFileFour);

        System.out.println("The duration taken to process the mean serial filtered image is: " + String.valueOf(durationMeanSerial));
        System.out.println("The duration taken to process the median serial filtered image is: " + String.valueOf(durationMedianSerial));
        System.out.println("The duration taken to process the mean parallel filtered image is: " + String.valueOf(durationMeanParallel));
        System.out.println("The duration taken to process the median parallel filtered image is: " + String.valueOf(durationMedianParallel));
    }
}

