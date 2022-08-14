import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

    public class MeanFilterParallel {
        public BufferedImage originalImage;
        public BufferedImage resultImage;
        public String inputImageName;
        public String outputImageName;
        public int windowWidth;


        public static void main(String[] args) throws IOException {
            MeanFilterParallel meanFilterParallel = new MeanFilterParallel("./src/image_input.jpg",
                    "./src/image_output_mean_parallel.jpg", "5");
            meanFilterParallel.readImageInfo();

            long startTimeMeanParallel = System.currentTimeMillis();
            final ForkJoinPool pool = new ForkJoinPool();
            ArrayList<int[]> arr = meanFilterParallel.settingImage();
            meanFilterThread meanFilterThread = new meanFilterThread(arr, meanFilterParallel.outputImageName, meanFilterParallel.windowWidth, 0, arr.size());
            pool.invoke(meanFilterThread);
            ArrayList<int[]> output = meanFilterThread.output;
            long endTimeMeanParallel = System.currentTimeMillis();
            long durationMeanParallel = endTimeMeanParallel - startTimeMeanParallel;

            System.out.println("The processing time for the mean filter parallel is: " + durationMeanParallel + " milliseconds.");
            BufferedImage resultImage = new BufferedImage(output.size(), output.get(0).length, BufferedImage.TYPE_INT_RGB);

            for (int i = 0; i < output.size(); i++) {
                for (int j = 0; j < output.get(0).length; j++) {
                    resultImage.setRGB(i, j, output.get(i)[j]);
                }
            }

            try {
                ImageIO.write(resultImage, "jpg", new File(meanFilterParallel.outputImageName));
            } catch (IOException e) {
                System.out.println("The image cannot be processed");
                System.exit(0);
            }
        }

        public MeanFilterParallel(String inputImageName, String outputImageName, String windowWidth) {
            this.inputImageName = inputImageName;
            this.outputImageName = outputImageName;
            this.windowWidth = Integer.parseInt(windowWidth);
        }

        public void readImageInfo() {
            try {
                originalImage = ImageIO.read(new File(inputImageName));
            } catch (IOException e) {
                System.out.println("The image cannot be retrieved");
                System.exit(0);
            }

            System.out.println(originalImage);
            System.out.println(inputImageName + " " + outputImageName + " " + windowWidth);
        }

        public int createRGBFromColors(int red, int green, int blue) {
            int rgb = 0;

            rgb |= blue;
            rgb |= green << 8;
            rgb |= red << 16;

            rgb |= 0xFF000000;

            return rgb;
        }

        public int getRed(int rgb) {
            return (rgb & 0x00FF0000) >> 16;
        }

        public int getGreen(int rgb) {
            return (rgb & 0x0000FF00) >> 8;
        }

        public int getBlue(int rgb) {
            return rgb & 0x000000FF;
        }

        public ArrayList<int[]> settingImage() {
            int height = originalImage.getHeight();
            int width = originalImage.getWidth();
            ArrayList<int[]> input = new ArrayList<>();

            for (int u = 0; u < width; u++) {
                int[] arr = new int[height];
                for (int w = 0; w < height; w++) {
                    arr[w] = originalImage.getRGB(u, w);
                }
                input.add(arr);
            }
            return input;
        }

        public static class meanFilterThread extends RecursiveTask<ArrayList<int[]>> {
            int hi, lo;
            String outputImageName;

            private static final int SEQUENTIAL_CUTOFF = 500;
            ArrayList<int[]> input;
            int windowWidth;
            ArrayList<int[]> output = new ArrayList<>();

            public meanFilterThread(ArrayList<int[]> input, String outputImageName, int windowWidth, int lo, int hi) {
                this.input = input;
                this.outputImageName = outputImageName;
                this.windowWidth = windowWidth;
                this.hi = hi;
                this.lo = lo;
            }

            protected ArrayList<int[]> compute() {
                int[] alpha, red, blue, green;
                int width = input.size();
                int height = input.get(0).length;
                int size = windowWidth * windowWidth;
                System.out.println(hi + " " + lo);

                if (hi - lo < SEQUENTIAL_CUTOFF) {
                    for (int x = lo; x < hi; x++) {
                        int[] pixels = new int[height];
                        for (int y = 0; y < height; y++) {
                            int newAlpha = 0, newRed = 0, newBlue = 0, newGreen = 0, count = 0;
                            alpha = new int[size];
                            red = new int[size];
                            blue = new int[size];
                            green = new int[size];

                            for (int i = 0; i < windowWidth; i++) {
                                for (int j = 0; j < windowWidth; j++) {
                                    if (y + i < height && x + j < width) {
                                        int pixel = input.get(x + j)[y + i];
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
                            pixels[y] = newAlpha << 24 | (newRed / size) << 16 | (newGreen / size) << 8 | (newBlue/ size);
                        }
                        output.add(pixels);
                    }
                } else {
                    int middle = (hi + lo) / 2;
                    meanFilterThread left = new meanFilterThread(input, outputImageName, windowWidth, lo, middle);
                    meanFilterThread right = new meanFilterThread(input, outputImageName, windowWidth, middle, hi);
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