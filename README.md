# Bilingual Digit Recognizer (Bangla & English)

A Machine Learning pipeline developed using **Weka** to recognize handwritten numerals for both **Bangla (০-৯)** and **English (0-9)**.

---

## 📌 Project Overview
This project focuses on the classification of bilingual handwritten digits. It covers the entire machine learning lifecycle:
1. **Data Preprocessing & Feature Extraction:** Converting raw handwritten digit images into structured ARFF (Attribute-Relation File Format) with normalized pixel intensities ($28 \times 28$ grayscale format).
2. **Model Training & Evaluation (Weka GUI):** Benchmarking multiple ML algorithms (e.g., Random Forest, Naive Bayes, Multilayer Perceptron) to find the optimal decision boundary.
3. **Model Serialization:** Exporting the best-performing classifier as a `.model` file for offline/real-time inference.
4. **Prediction / Inference:** Loading the serialized model to classify unseen digit samples.

---

## 📊 Pipeline & Methodology

### 1. Dataset & Attributes
- **Input:** $28 \times 28$ grayscale pixel values (784 attributes representing normalized pixel intensities).
- **Classes:** 
  - English Digits: `0, 1, 2, ..., 9`
  - Bangla Digits: `০, ১, ২, ..., ৯`

### 2. Model Training in Weka
- Imported the `.arff` dataset into **Weka Explorer**.
- Applied 10-Fold Cross-Validation.
- Compared classifiers based on **Accuracy, Precision, Recall, and F1-Score**.
- Saved the best-performing model as a serialized Weka model file in `/models`.

---

## 📂 Repository Structure

```text
├── arff/                 # Extracted dataset files in Weka ARFF format
├── images/               # Raw handwritten digit image datasets (Bangla & English)
├── Models/               # Trained and serialized Weka classification models (.model)
├── src/                  # Java source code (image processing, GUI, inference)
├── Project report.pdf    # Detailed academic lab project report & findings
├── test_digit.jpg        # Sample test image used for real-time inference
├── weka.jar              # Weka Machine Learning library dependency
└── README.md             # Project documentation

---

## 🚀 How to Reproduce / Run

### Option A: Using Weka GUI (No Coding Required)
1. Open **Weka GUI Chooser** and select **Explorer**.
2. Go to the **Preprocess** tab and load `dataset/sample.arff`.
3. Switch to the **Classify** tab.
4. Right-click on the result list area $\to$ **Load model** $\to$ select the model from `models/`.
5. Under *Test options*, select **Supplied test set** to evaluate unseen samples or test data.

### Option B: Using Code (Inference)
If running via Java/Command Line:
```bash
# Load model and evaluate on test instances
java -cp weka.jar weka.classifiers.meta.FilteredClassifier -l models/your_model.model -T dataset/test.arff