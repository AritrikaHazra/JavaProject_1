import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ToDoList extends JFrame {
    private DefaultListModel<Task> taskModel = new DefaultListModel<>();
    private JList<Task> taskList = new JList<>(taskModel);
    private JTextField taskInput = new JTextField();

    public ToDoList() {
        super("To-Do List");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for input
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(taskInput, BorderLayout.CENTER);
        JButton addButton = new JButton("Add");
        topPanel.add(addButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Task list in center
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(taskList), BorderLayout.CENTER);

        // Bottom panel for actions
        JPanel bottomPanel = new JPanel();
        JButton doneButton = new JButton("Mark Done");
        JButton deleteButton = new JButton("Delete");
        JButton saveButton = new JButton("Save");
        JButton loadButton = new JButton("Load");

        bottomPanel.add(doneButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(saveButton);
        bottomPanel.add(loadButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> {
            String text = taskInput.getText().trim();
            if (!text.isEmpty()) {
                taskModel.addElement(new Task(text));
                taskInput.setText("");
            }
        });

        doneButton.addActionListener(e -> {
            int idx = taskList.getSelectedIndex();
            if (idx >= 0) {
                taskModel.get(idx).markDone();
                taskList.repaint();
            }
        });

        deleteButton.addActionListener(e -> {
            int idx = taskList.getSelectedIndex();
            if (idx >= 0) {
                taskModel.remove(idx);
            }
        });

        saveButton.addActionListener(e -> saveTasks());
        loadButton.addActionListener(e -> loadTasks());

        setVisible(true);
    }

    private void saveTasks() {
        try (PrintWriter writer = new PrintWriter("tasks.txt")) {
            for (int i = 0; i < taskModel.size(); i++) {
                Task t = taskModel.get(i);
                writer.println((t.isDone() ? "1" : "0") + ";" + t.getDescription());
            }
            JOptionPane.showMessageDialog(this, "Tasks saved!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving tasks.");
        }
    }

    private void loadTasks() {
        taskModel.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("tasks.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", 2);
                if (parts.length == 2) {
                    Task task = new Task(parts[1]);
                    if (parts[0].equals("1")) task.markDone();
                    taskModel.addElement(task);
                }
            }
            JOptionPane.showMessageDialog(this, "Tasks loaded!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No saved tasks found.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoList::new);
    }
}
