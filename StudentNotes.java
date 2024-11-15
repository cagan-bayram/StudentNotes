import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class StudentNotes extends JFrame {
    private JTextArea noteArea;
    private JButton saveButton, viewButton, deleteButton;
    private JList<String> noteList;
    private DefaultListModel<String> listModel;

    public StudentNotes() {
        // Set the FlatLaf Dark theme
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Student Notes");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        noteArea = new JTextArea();
        noteArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        noteArea.setBackground(new Color(43, 43, 43));
        noteArea.setForeground(Color.WHITE);

        saveButton = new JButton("Save Note");
        viewButton = new JButton("View Notes");
        deleteButton = new JButton("Delete Note");

        listModel = new DefaultListModel<>();
        noteList = new JList<>(listModel);
        noteList.setBackground(new Color(43, 43, 43));
        noteList.setForeground(Color.WHITE);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveNote();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewNotes();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteNote();
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(noteArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(saveButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(noteList), BorderLayout.WEST);

        add(panel);
    }

    private void saveNote() {
        String note = noteArea.getText();
        if (!note.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("notes.txt", true))) {
                writer.write(note);
                writer.newLine();
                noteArea.setText("");
                JOptionPane.showMessageDialog(this, "Note saved!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Note is empty!");
        }
    }

    private void viewNotes() {
        listModel.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("notes.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                listModel.addElement(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteNote() {
        int selectedIndex = noteList.getSelectedIndex();
        if (selectedIndex != -1) {
            listModel.remove(selectedIndex);
            saveAllNotes();
            JOptionPane.showMessageDialog(this, "Note deleted!");
        } else {
            JOptionPane.showMessageDialog(this, "No note selected!");
        }
    }

    private void saveAllNotes() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("notes.txt"))) {
            for (int i = 0; i < listModel.size(); i++) {
                writer.write(listModel.getElementAt(i));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StudentNotes().setVisible(true);
            }
        });
    }
}
