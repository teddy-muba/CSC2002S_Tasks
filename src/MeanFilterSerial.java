import java.awt.image.BufferedImage;

public class MeanFilterSerial {
         public BufferedImage originalImage;
         public BufferedImage resultImage;

         public MeanFilterSerial(BufferedImage originalImage, BufferedImage resultImage) {
              this.originalImage = originalImage;
              this.resultImage = resultImage;
        }

        public void recolorSingleThreaded(BufferedImage originalImage, BufferedImage resultImage) {
            recolorImage(originalImage, resultImage, 0, 0, originalImage.getWidth(), originalImage.getHeight());
        }

        public void recolorImage(BufferedImage originalImage, BufferedImage resultImage, int leftCorner, int topCorner,
                                        int width, int height) {
            for(int x = leftCorner ; x < leftCorner + width && x < originalImage.getWidth() ; x++) {
                for(int y = topCorner ; y < topCorner + height && y < originalImage.getHeight() ; y++) {
                    recolorPixel(originalImage, resultImage, x , y);
                }
            }
        }

        public void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
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

        public void setRGB(BufferedImage image, int x, int y, int rgb) {
            image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
        }

        public boolean isShadeOfGray(int red, int green, int blue) {
            return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs( green - blue) < 30;
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
            return rgb & 0x000000FF >> 0;
        }
    }

