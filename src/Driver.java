import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public class Driver {

    public static final String SOURCE_FILE = "./src/image_input.jpg";
    public static final String DESTINATION_FILE = "./src/image_output.jpg";

    public static void main(String[] args) throws IOException {

        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        MeanFilterParallel meanFilterParallel = new MeanFilterParallel(originalImage, resultImage, 4);
        MedianFilterParallel medianFilterParallel = new MedianFilterParallel(originalImage, resultImage, 2);

        long startTime = System.currentTimeMillis();
        System.out.println("This is for meanparallel");
        meanFilterParallel.compute();
        System.out.println("This is for medianparallel");
        medianFilterParallel.compute();

        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        File outputFile = new File(DESTINATION_FILE);
        ImageIO.write(resultImage, "jpg", outputFile);

        System.out.println(String.valueOf(duration));
    }
}

