package digitdrawandclassify1;

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

public class DigitDrawAndClassify1 extends JPanel {

    private BufferedImage canvas;
    private Graphics2D g2;
    private final int width = 250, height = 350;

    public DigitDrawAndClassify1() {
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        g2 = canvas.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.BLACK);
        setPreferredSize(new Dimension(width, height));
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
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.BLACK);
        repaint();
    }

    public void drawUploadedImage(BufferedImage img) {
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.drawImage(scaledImg, 0, 0, null);
        g2.setColor(Color.BLACK);
        repaint();
    }

    public void saveImage(String path) throws Exception {
        ImageIO.write(canvas, "jpg", new File(path));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Digit Recognition (Bangla/English)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout(10, 10));
            frame.getContentPane().setBackground(new Color(245, 245, 245));

            JLabel resultLabel = new JLabel("Draw a digit or upload an image");
            resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
            resultLabel.setForeground(new Color(0, 102, 204));
            resultLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

            DigitDrawAndClassify1 panel = new DigitDrawAndClassify1();

            JLabel uploadedImageLabel = new JLabel();
            uploadedImageLabel.setPreferredSize(new Dimension(250, 350));
            uploadedImageLabel.setBorder(BorderFactory.createTitledBorder("Uploaded Image"));
            uploadedImageLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JButton clearBtn = new JButton(" Clear");
            JButton classifyBtn = new JButton(" Classify Drawing");
            JButton uploadBtn = new JButton(" Upload Image");

            Font btnFont = new Font("Segoe UI", Font.BOLD, 14);
            for (JButton btn : new JButton[]{clearBtn, classifyBtn, uploadBtn}) {
                btn.setFont(btnFont);
                btn.setFocusPainted(false);
                btn.setBackground(new Color(230, 230, 250));
            }

            clearBtn.addActionListener(e -> {
                panel.clearCanvas();
                uploadedImageLabel.setIcon(null);
                resultLabel.setText("Draw a digit or upload an image");
            });

            classifyBtn.addActionListener(e -> {
                try {
                    String imagePath = "E:\\images\\test_digit.jpg";
                    panel.saveImage(imagePath);
                    String prediction = classifyImage(imagePath);
                    resultLabel.setText("Prediction (Drawn): " + prediction);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    resultLabel.setText("Error: " + ex.getMessage());
                }
            });

            uploadBtn.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser("E:\\images\\");
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = chooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = chooser.getSelectedFile();
                    try {
                        BufferedImage uploadedImg = ImageIO.read(selectedFile);
                        panel.drawUploadedImage(uploadedImg);

                        Image scaled = uploadedImg.getScaledInstance(250, 350, Image.SCALE_SMOOTH);
                        uploadedImageLabel.setIcon(new ImageIcon(scaled));

                        String prediction = classifyImage(selectedFile.getAbsolutePath());
                        resultLabel.setText("Prediction (Uploaded): " + prediction);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        resultLabel.setText("Error: " + ex.getMessage());
                    }
                }
            });

            JPanel btnPanel = new JPanel(new FlowLayout());
            btnPanel.add(clearBtn);
            btnPanel.add(classifyBtn);
            btnPanel.add(uploadBtn);
            btnPanel.setBackground(new Color(245, 245, 245));
            btnPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JPanel centerPanel = new JPanel(new GridBagLayout());
            centerPanel.setBackground(new Color(245, 245, 245));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;
            centerPanel.add(panel, gbc);

            gbc.gridx = 1;
            centerPanel.add(uploadedImageLabel, gbc);

            frame.add(resultLabel, BorderLayout.NORTH);
            frame.add(centerPanel, BorderLayout.CENTER);
            frame.add(btnPanel, BorderLayout.SOUTH);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public static String classifyImage(String imagePath) throws Exception {
        int width = 250, height = 350;

        BufferedImage img = ImageIO.read(new File(imagePath));

        Classifier cls = (Classifier) weka.core.SerializationHelper.read(
            "D:\\Student\\classroom google\\2025\\Spring\\Ai LAb\\Project\\Models\\new_model.model"
        );

        Instances data = new DataSource(
            "D:\\Student\\classroom google\\2025\\Spring\\Ai LAb\\Project\\arff\\digits_250x350.arff"
        ).getDataSet();
        data.setClassIndex(data.numAttributes() - 1);

        double[] values = new double[width * height + 1];
        int idx = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = img.getRGB(x, y) & 0xFF;
                values[idx++] = pixel;
            }
        }

        values[values.length - 1] = Double.NaN;
        Instance inst = new DenseInstance(1.0, values);
        inst.setDataset(data);

        double result = cls.classifyInstance(inst);
        return data.classAttribute().value((int) result);
    }
}
