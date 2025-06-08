import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Hamming SEC-DED Simülatörü");
        frame.setSize(750, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JLabel bitLabel = new JLabel("Veri Uzunluğu:");
        String[] bitOptions = {"8", "16", "32"};
        JComboBox<String> bitSizeSelector = new JComboBox<>(bitOptions);
        JLabel inputLabel = new JLabel("Veri Girişi (örnek: 10101010...):");
        JTextField inputField = new JTextField(40);
        JTextArea outputArea = new JTextArea(15, 60);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        JButton encodeButton = new JButton("➤ Kodla");
        JButton errorButton = new JButton("⚠ Hata Ekle");
        JButton correctButton = new JButton("✔ Düzelt");
        JTextField addressField = new JTextField(5);
        JButton saveButton = new JButton("💾 Belleğe Yaz");
        JButton loadButton = new JButton("📥 Bellekten Oku");

        final int[][] encoded = new int[1][];

        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        JPanel bitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        bitPanel.add(bitLabel);
        bitPanel.add(bitSizeSelector);
        inputPanel.add(inputLabel);
        inputPanel.add(inputField);

        buttonPanel.add(encodeButton);
        buttonPanel.add(errorButton);
        buttonPanel.add(correctButton);
        buttonPanel.add(new JLabel("Adres:"));
        buttonPanel.add(addressField);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        topPanel.add(bitPanel);
        topPanel.add(inputPanel);
        topPanel.add(buttonPanel);

        encodeButton.addActionListener(e -> {
            int selectedBits = Integer.parseInt((String) bitSizeSelector.getSelectedItem());
            String text = inputField.getText().trim();

            if (text.length() != selectedBits) {
                JOptionPane.showMessageDialog(frame, "Lütfen tam olarak " + selectedBits + " bitlik veri giriniz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int[] data = new int[selectedBits];
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                if (ch != '0' && ch != '1') {
                    JOptionPane.showMessageDialog(frame, "Lütfen yalnızca 0 ve 1 karakterlerini kullanınız.", "Geçersiz Girdi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                data[i] = Character.getNumericValue(ch);
            }

            encoded[0] = HammingCode.encodeData(data);
            outputArea.setText("──── Kodlanmış Veri ────\n");
            for (int bit : encoded[0]) {
                outputArea.append(bit + "");
            }
            outputArea.append("\n\n✔ Kodlama tamamlandı.");
        });

        errorButton.addActionListener(e -> {
            if (encoded[0] == null) {
                JOptionPane.showMessageDialog(frame, "Lütfen önce veri kodlayınız.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String posStr = JOptionPane.showInputDialog("Hangi bit pozisyonunda hata oluşturulsun? (0 - " + (encoded[0].length - 1) + ")");
            if (posStr == null) return;
            try {
                int pos = Integer.parseInt(posStr);
                if (pos < 0 || pos >= encoded[0].length) {
                    JOptionPane.showMessageDialog(frame, "Geçersiz pozisyon.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                HammingCode.introduceError(encoded[0], pos);
                outputArea.append("\n──── Hata Eklendi (Bit " + pos + ") ────\n");
                for (int bit : encoded[0]) {
                    outputArea.append(bit + "");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Lütfen geçerli bir sayı giriniz.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        correctButton.addActionListener(e -> {
            if (encoded[0] == null) {
                JOptionPane.showMessageDialog(frame, "Önce veri kodlayınız.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int corrected = HammingCode.detectAndCorrect(encoded[0]);
            outputArea.append("\n──── Hata Düzeltme ────\n");
            for (int bit : encoded[0]) {
                outputArea.append(bit + "");
            }

            if (corrected == -1) {
                outputArea.append("\nDurum: Çift bit hatası veya hata bulunamadı.");
            } else {
                outputArea.append("\n✔ Hatalı bit " + corrected + ". pozisyonda düzeltildi.");
            }
        });

        saveButton.addActionListener(e -> {
            if (encoded[0] == null) {
                JOptionPane.showMessageDialog(frame, "Önce veri kodlayınız.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int address = Integer.parseInt(addressField.getText());
                Memory.save(address, encoded[0]);
                outputArea.append("\n💾 Kod belleğe yazıldı (adres " + address + ").");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Lütfen geçerli bir adres giriniz.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadButton.addActionListener(e -> {
            try {
                int address = Integer.parseInt(addressField.getText());
                if (!Memory.exists(address)) {
                    JOptionPane.showMessageDialog(frame, "Bu adreste veri yok.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                encoded[0] = Memory.read(address);
                outputArea.append("\n📥 Bellekten okunan veri (adres " + address + "):\n");
                for (int bit : encoded[0]) {
                    outputArea.append(bit + "");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Lütfen geçerli bir adres giriniz.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setLayout(new BorderLayout(10, 10));
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}