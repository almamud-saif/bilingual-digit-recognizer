package digitdrawandclassify;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class DigitDrawAndClassify extends JPanel {

    private BufferedImage canvas;
    private Graphics2D g2;
    private final int drawWidth = 250, drawHeight = 350;
    private JLabel predictionLabel;
    private JLabel uploadedImageLabel;

    public DigitDrawAndClassify(JLabel predictionLabel, JLabel uploadedImageLabel) {
        this.predictionLabel = predictionLabel;
        this.uploadedImageLabel = uploadedImageLabel;

        canvas = new BufferedImage(drawWidth, drawHeight, BufferedImage.TYPE_BYTE_GRAY);
        g2 = canvas.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, drawWidth, drawHeight);
        g2.setColor(Color.BLACK);

        setPreferredSize(new Dimension(drawWidth, drawHeight));
        setBorder(BorderFactory.createTitledBorder("Draw Here"));
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                g2.fillOval(e.getX(), e.getY(), 8, 8);
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                g2.fillOval(e.getX(), e.getY(), 8, 8);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, null);
    }

    public void clearCanvas() {
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, drawWidth, drawHeight);
        g2.setColor(Color.BLACK);
        predictionLabel.setText("Draw a digit or upload an image");
        uploadedImageLabel.setIcon(null);
        repaint();
    }

    
    public void saveImage(String path) throws Exception {
        int targetWidth = 28;
        int targetHeight = 28;

        Image tmp = canvas.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        ImageIO.write(resized, "jpg", new File(path));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Digit Recognition (Bangla/English)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout(10, 10));
            frame.getContentPane().setBackground(new Color(245, 245, 245));

            JLabel predictionLabel = new JLabel("Draw a digit or upload an image");
            predictionLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            predictionLabel.setHorizontalAlignment(SwingConstants.CENTER);
            predictionLabel.setForeground(new Color(0, 102, 204));
            predictionLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel uploadedImageLabel = new JLabel();
            uploadedImageLabel.setPreferredSize(new Dimension(250, 350));
            uploadedImageLabel.setBorder(BorderFactory.createTitledBorder("Uploaded Image"));
            uploadedImageLabel.setHorizontalAlignment(SwingConstants.CENTER);

            DigitDrawAndClassify panel = new DigitDrawAndClassify(predictionLabel, uploadedImageLabel);

            JButton clearBtn = new JButton(" Clear");
            JButton classifyBtn = new JButton("️ Classify Drawing");
            JButton uploadBtn = new JButton(" Upload Image");

            Font btnFont = new Font("Segoe UI", Font.BOLD, 14);
            for (JButton btn : new JButton[]{clearBtn, classifyBtn, uploadBtn}) {
                btn.setFont(btnFont);
                btn.setFocusPainted(false);
                btn.setBackground(new Color(230, 230, 250));
            }

            clearBtn.addActionListener(e -> panel.clearCanvas());

            classifyBtn.addActionListener(e -> {
                try {
                    String imagePath = "drawn_digit.jpg";
                    panel.saveImage(imagePath);
                    String prediction = classifyImage(imagePath);
                    predictionLabel.setText("Prediction (Drawn): " + prediction);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    predictionLabel.setText("Error: " + ex.getMessage());
                }
            });

            uploadBtn.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("E:\\images"));  // Set default folder to E:\images

                int option = fileChooser.showOpenDialog(frame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        BufferedImage img = ImageIO.read(file);
                        if (img == null) throw new Exception("Invalid image file");

                        // Show original uploaded image scaled to 250x350
                        Image scaledDisplay = img.getScaledInstance(250, 350, Image.SCALE_SMOOTH);
                        uploadedImageLabel.setIcon(new ImageIcon(scaledDisplay));

                        String prediction = classifyImage(file.getAbsolutePath());
                        predictionLabel.setText("Prediction (Uploaded): " + prediction);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        predictionLabel.setText("Error: " + ex.getMessage());
                    }
                }
            });

            JPanel btnPanel = new JPanel(new FlowLayout());
            btnPanel.add(clearBtn);
            btnPanel.add(classifyBtn);
            btnPanel.add(uploadBtn);
            btnPanel.setBackground(new Color(245, 245, 245));
            btnPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
            centerPanel.setBackground(new Color(245, 245, 245));
            centerPanel.add(panel);
            centerPanel.add(uploadedImageLabel);

            frame.add(predictionLabel, BorderLayout.NORTH);
            frame.add(centerPanel, BorderLayout.CENTER);
            frame.add(btnPanel, BorderLayout.SOUTH);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public static String classifyImage(String imagePath) throws Exception {
        int targetWidth = 28;
        int targetHeight = 28;

        BufferedImage originalImg = ImageIO.read(new File(imagePath));
        if (originalImg == null) {
            throw new Exception("Could not read image file");
        }

        // Resize to 28x28 
        Image tmp = originalImg.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImg = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = resizedImg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        // Load model and ARFF structure
        Classifier cls = (Classifier) weka.core.SerializationHelper.read(
            "D:\\Student\\classroom google\\2025\\Spring\\Ai LAb\\Project\\Models\\ok.model");

        Instances data = new DataSource(
            "D:\\Student\\classroom google\\2025\\Spring\\Ai LAb\\Project\\arff\\ok.arff"
        ).getDataSet();
        data.setClassIndex(data.numAttributes() - 1);

        double[] values = new double[targetWidth * targetHeight + 1];
        int idx = 0;
        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                int pixel = resizedImg.getRGB(x, y) & 0xFF;
                values[idx++] = pixel;
            }
        }

        values[values.length - 1] = weka.core.Utils.missingValue();

        Instance inst = new DenseInstance(1.0, values);
        inst.setDataset(data);

        double result = cls.classifyInstance(inst);
        return data.classAttribute().value((int) result);
    }
}
