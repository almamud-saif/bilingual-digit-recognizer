package imagetoarff;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import javax.imageio.ImageIO;

public class ImageToArff {

    static final int IMG_WIDTH = 28;
    static final int IMG_HEIGHT = 28;

    public static void main(String[] args) throws Exception {
       File folder = new File("D:\\Student\\classroom google\\2025\\Spring\\Ai LAb\\Project\\images");
        File[] files = folder.listFiles((dir, name) ->
            name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png")
        );

        if (files == null || files.length == 0) {
            System.out.println("No images found in folder: " + folder.getAbsolutePath());
            return;
        }

        Arrays.sort(files);
        FileWriter writer = new FileWriter("digits_28x28.arff");

        // ARFF Header
        writer.write("@RELATION digits_28x28\n\n");
        for (int i = 0; i < IMG_WIDTH * IMG_HEIGHT; i++) {
            writer.write("@ATTRIBUTE pixel" + i + " NUMERIC\n");
        }
        writer.write("@ATTRIBUTE class {0,1,2,3,4,5,6,7,8,9}\n\n");
        writer.write("@DATA\n");

        int total = 0;

        for (File imgFile : files) {
            BufferedImage original = ImageIO.read(imgFile);
            BufferedImage resized = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g = resized.createGraphics();
            g.drawImage(original.getScaledInstance(IMG_WIDTH, IMG_HEIGHT, Image.SCALE_SMOOTH), 0, 0, null);
            g.dispose();

            StringBuilder sb = new StringBuilder();

            for (int y = 0; y < IMG_HEIGHT; y++) {
                for (int x = 0; x < IMG_WIDTH; x++) {
                    int pixel = resized.getRGB(x, y) & 0xFF;
                    sb.append(pixel).append(",");
                }
            }

            // Extract digit label
            String name = imgFile.getName();
            int digit = extractDigit(name);
            sb.append(digit).append("\n");
            writer.write(sb.toString());
            total++;
        }

        writer.close();
        System.out.println(" ARFF file created: digits_28x28.arff");
        System.out.println("️ Total images processed: " + total);
    }

    // Extract digit from filename like G2-B3.jpg or G5_E7.jpg
    static int extractDigit(String filename) {
        String name = filename.substring(0, filename.lastIndexOf('.'));
        for (int i = name.length() - 1; i >= 0; i--) {
            if (Character.isDigit(name.charAt(i))) {
                return Character.getNumericValue(name.charAt(i));
            }
        }
        return -1; // error fallback
    }
}
