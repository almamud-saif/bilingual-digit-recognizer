package imagetoarffsinglefolder;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class ImageToArffSingleFolder {
    public static void main(String[] args) throws Exception {
        int width = 28;
        int height = 28;

        File folder = new File("D:\\Student\\classroom google\\2025\\Spring\\Ai LAb\\Project\\images");
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("No image files found in the folder.");
            return;
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter("ok.arff"));

        // ARFF header
        bw.write("@RELATION digits\n\n");
        for (int i = 0; i < width * height; i++) {
            bw.write("@ATTRIBUTE pixel" + i + " NUMERIC\n");
        }
        bw.write("@ATTRIBUTE class {");
        for (int d = 0; d <= 9; d++) {
            bw.write("english_" + d + ",bangla_" + d);
            if (d < 9) bw.write(",");
        }
        bw.write("}\n\n");
        bw.write("@DATA\n");

        int count = 0;
        for (File file : files) {
            if (file.getName().toLowerCase().endsWith(".jpg")) {
                String name = file.getName();

                // Extract digit label
                int label = -1;
                for (int i = name.length() - 1; i >= 0; i--) {
                    char c = name.charAt(i);
                    if (Character.isDigit(c)) {
                        label = Character.getNumericValue(c);
                        break;
                    }
                }
                if (label == -1) {
                    System.out.println("No digit found in filename: " + name);
                    continue;
                }

                // Detect language case-insensitive
                String upperName = name.toUpperCase();
                String lang = "unknown";
                if (upperName.contains("-B") || upperName.contains("_B")) lang = "bangla";
                else if (upperName.contains("-E") || upperName.contains("_E")) lang = "english";
                else {
                    System.out.println("Unknown language for file: " + name);
                    continue;
                }

                BufferedImage img = ImageIO.read(file);
                if (img == null) {
                    System.out.println("Could not read image: " + name);
                    continue;
                }

                // Resize original image (250x350) to 28x28 directly (with smooth scaling)
                Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

                Graphics2D g2d = resized.createGraphics();
                g2d.drawImage(tmp, 0, 0, null);
                g2d.dispose();

                // Extract grayscale pixel values (0-255)
                StringBuilder sb = new StringBuilder();
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pixel = resized.getRGB(x, y) & 0xFF;
                        sb.append(pixel).append(",");
                    }
                }

                // Append class label
                sb.append(lang).append("_").append(label);

                bw.write(sb.toString() + "\n");

                System.out.println("Processed: " + name + " => Label: " + lang + "_" + label);
                count++;
            }
        }

        bw.close();
        System.out.println("\nARFF file created");
        System.out.println("Total images processed: " + count);
    }
}
