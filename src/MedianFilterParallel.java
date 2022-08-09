import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;


public class MedianFilterParallel extends MeanFilterParallel {

    public MedianFilterParallel (BufferedImage originalImage, BufferedImage resultImage, int numberOfThreads) {
        super(originalImage,resultImage, numberOfThreads);
    }

    //recolorMultiThreaded(originalImage, resultImage);
    @Override
    protected void compute() {
        {
            // if the task is too large then we split it and execute the tasks in parallel

            if (numberOfThreads > 1){
                System.out.println("Parallel execution and split the task...");

                MedianFilterParallel medianFilterParallel1 = new MedianFilterParallel(originalImage,resultImage,numberOfThreads/2);
                MedianFilterParallel medianFilterParallel2 = new MedianFilterParallel(originalImage,resultImage,numberOfThreads/2);

                medianFilterParallel1.fork();
                medianFilterParallel2.fork();

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
}




