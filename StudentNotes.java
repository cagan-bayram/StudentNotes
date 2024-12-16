import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class StudentNotes extends JFrame {
    private JTextArea noteArea;
    private JButton saveButton, viewButton, deleteButton, newDirButton, newNoteButton;
    private JTree directoryTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private File baseDir;

    public StudentNotes() {
        // Set the FlatLaf Dark theme
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Student Notes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ensure the base directory exists
        baseDir = new File("Notes");
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }

        noteArea = new JTextArea();
        noteArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        noteArea.setBackground(new Color(43, 43, 43));
        noteArea.setForeground(Color.WHITE);

        saveButton = new JButton("Save Note");
        viewButton = new JButton("View Notes");
        deleteButton = new JButton("Delete Note");
        newDirButton = new JButton("New Directory");
        newNoteButton = new JButton("New Note");

        rootNode = new DefaultMutableTreeNode("Notes");
        treeModel = new DefaultTreeModel(rootNode);
        directoryTree = new JTree(treeModel);
        directoryTree.setBackground(new Color(43, 43, 43));
        directoryTree.setForeground(Color.WHITE);

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

        newDirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createDirectory();
            }
        });

        newNoteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createNote();
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
        buttonPanel.add(newDirButton);
        buttonPanel.add(newNoteButton);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(directoryTree), BorderLayout.WEST);

        add(panel);
    }

    private void saveNote() {
        String note = noteArea.getText();
        if (!note.isEmpty()) {
            TreePath selectedPath = directoryTree.getSelectionPath();
            if (selectedPath != null) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                File dir = new File(getNodePath(selectedNode));
                if (dir.isDirectory()) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dir, "note.txt"), true))) {
                        writer.write(note);
                        writer.newLine();
                        noteArea.setText("");
                        JOptionPane.showMessageDialog(this, "Note saved!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Selected node is not a directory!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No directory selected!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Note is empty!");
        }
    }

    private void viewNotes() {
        TreePath selectedPath = directoryTree.getSelectionPath();
        if (selectedPath != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
            File dir = new File(getNodePath(selectedNode));
            if (dir.isDirectory()) {
                File noteFile = new File(dir, "note.txt");
                if (noteFile.exists()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(noteFile))) {
                        noteArea.setText("");
                        String line;
                        while ((line = reader.readLine()) != null) {
                            noteArea.append(line + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No notes found in the selected directory!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selected node is not a directory!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No directory selected!");
        }
    }

    private void deleteNote() {
        TreePath selectedPath = directoryTree.getSelectionPath();
        if (selectedPath != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
            File dir = new File(getNodePath(selectedNode));
            if (dir.isDirectory()) {
                File noteFile = new File(dir, "note.txt");
                if (noteFile.exists()) {
                    noteFile.delete();
                    noteArea.setText("");
                    JOptionPane.showMessageDialog(this, "Note deleted!");
                } else {
                    JOptionPane.showMessageDialog(this, "No notes found in the selected directory!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selected node is not a directory!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No directory selected!");
        }
    }

    private void createDirectory() {
        TreePath selectedPath = directoryTree.getSelectionPath();
        if (selectedPath != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
            String dirName = JOptionPane.showInputDialog(this, "Enter directory name:");
            if (dirName != null && !dirName.trim().isEmpty()) {
                File dir = new File(getNodePath(selectedNode), dirName);
                if (dir.mkdir()) {
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(dirName);
                    selectedNode.add(newNode);
                    treeModel.reload(selectedNode);
                    JOptionPane.showMessageDialog(this, "Directory created!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create directory!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No directory selected!");
        }
    }

    private void createNote() {
        TreePath selectedPath = directoryTree.getSelectionPath();
        if (selectedPath != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
            String noteName = JOptionPane.showInputDialog(this, "Enter note name:");
            if (noteName != null && !noteName.trim().isEmpty()) {
                File noteFile = new File(getNodePath(selectedNode), noteName + ".txt");
                try {
                    if (noteFile.createNewFile()) {
                        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(noteName + ".txt");
                        selectedNode.add(newNode);
                        treeModel.reload(selectedNode);
                        JOptionPane.showMessageDialog(this, "Note created!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to create note!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No directory selected!");
        }
    }

    private String getNodePath(DefaultMutableTreeNode node) {
        TreeNode[] nodes = node.getPath();
        StringBuilder path = new StringBuilder(baseDir.getAbsolutePath());
        for (int i = 1; i < nodes.length; i++) {
            path.append(File.separator).append(nodes[i].toString());
        }
        return path.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StudentNotes().setVisible(true);
            }
        });
    }
}
