import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;


public class MeanFilterParallel extends RecursiveAction {

    public BufferedImage originalImage;
    public BufferedImage resultImage;
    public int numberOfThreads;

    public MeanFilterParallel (BufferedImage originalImage, BufferedImage resultImage, int numberOfThreads) {
        this.originalImage = originalImage;
        this.resultImage = resultImage;
        this.numberOfThreads = numberOfThreads;
    }

    //recolorMultiThreaded(originalImage, resultImage);
    @Override
    protected void compute() {
        {
            // if the task is too large then we split it and execute the tasks in parallel

            if (numberOfThreads > 1){
                System.out.println("Parallel execution and split the task...");

                MeanFilterParallel meanFilterParallel1 = new MeanFilterParallel(originalImage,resultImage,numberOfThreads/2);
                MeanFilterParallel meanFilterParallel2 = new MeanFilterParallel(originalImage,resultImage,numberOfThreads/2);

                meanFilterParallel1.fork();
                meanFilterParallel2.fork();

            } else{
                System.out.println("The task is rather small so sequential execution is fine...");
                System.out.println("The size of the task:" + numberOfThreads);

            }

        }

        List<Thread> threads = new ArrayList<>();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight() / numberOfThreads;

        for(int i = 0; i < numberOfThreads ; i++) {
            final int threadMultiplier = i;

            Thread thread = new Thread(() -> {
                int xOrigin = 0;
                int yOrigin = height * threadMultiplier;

                recolorImage(originalImage, resultImage, xOrigin, yOrigin, width, height);
            });

            threads.add(thread);

        }

        for(Thread thread : threads) {
            thread.start();
        }

        for(Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
    }

    public static void recolorImage(BufferedImage originalImage, BufferedImage resultImage, int leftCorner, int topCorner,
                                    int width, int height) {
        for(int x = leftCorner ; x < leftCorner + width && x < originalImage.getWidth() ; x++) {
            for(int y = topCorner ; y < topCorner + height && y < originalImage.getHeight() ; y++) {
                recolorPixel(originalImage, resultImage, x , y);
            }
        }
    }

    public static void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);

        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed;
        int newGreen;
        int newBlue;

        if(isShadeOfGray(red, green, blue)) {
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }
        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(resultImage, x, y, newRGB);
    }

    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }

    public static boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs( green - blue) < 30;
    }

    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;

        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000;

        return rgb;
    }

    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }

}

