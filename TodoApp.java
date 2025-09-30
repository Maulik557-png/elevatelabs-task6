import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;

/**
 * TodoApp - A small, production-quality Java Swing To-Do List application.
 *
 * Features:
 *  - Add tasks via text field + Add button (Enter in field works too).
 *  - Delete selected tasks via Delete button (supports multi-select).
 *  - Validates input: no empty tasks, no duplicates (case-insensitive, trimmed).
 *  - Handles all corner cases gracefully with user-friendly dialogs.
 *  - Robust UI: resizable, accessible keyboard shortcuts, clean layout.
 *
 * Implementation notes:
 *  - Uses DefaultListModel and JList for task storage & display.
 *  - All UI work is executed on the Event Dispatch Thread (EDT).
 *  - Proper exception handling and defensive checks throughout.
 *
 * @author
 * @version 1.0
 */
public class TodoApp extends JFrame {

    private static final long serialVersionUID = 1L;

    // Core UI components
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> taskList = new JList<>(listModel);
    private final JTextField taskField = new JTextField();
    private final JButton addButton = new JButton("Add");
    private final JButton deleteButton = new JButton("Delete");

    // Constructor: set up UI and behavior
    public TodoApp() {
        super("To-Do List");
        initUI();
    }

    /**
     * Initialize the user interface components, layout, and listeners.
     */
    private void initUI() {
        // Basic frame settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 400));
        setMinimumSize(new Dimension(380, 260));
        setLayout(new BorderLayout(8, 8)); // spacing between borders

        // Top panel: input + Add button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(6, 6));
        taskField.setToolTipText("Enter a new task (no empty or duplicate tasks). Press Enter to add.");
        topPanel.add(taskField, BorderLayout.CENTER);

        // Add-button: mnemonic and tooltip
        addButton.setMnemonic(KeyEvent.VK_A);
        addButton.setToolTipText("Add the task (Alt + A).");
        topPanel.add(addButton, BorderLayout.EAST);

        // Center: list in a scroll pane
        taskList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        taskList.setVisibleRowCount(10);
        taskList.setFixedCellWidth(200);
        taskList.setPrototypeCellValue("This is a sample task to size cells"); // helpful for consistent width
        JScrollPane scrollPane = new JScrollPane(taskList);

        // Right/bottom control panel: delete button
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        deleteButton.setEnabled(false); // disabled until selection
        deleteButton.setToolTipText("Delete selected task(s).");
        deleteButton.setMnemonic(KeyEvent.VK_D);
        // Place delete button at the top of right panel, with padding around
        rightPanel.add(Box.createVerticalStrut(6));
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(deleteButton);
        rightPanel.add(Box.createVerticalGlue()); // pushes delete to top

        // Status bar at bottom for small hints
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        // Compose frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(statusLabel, BorderLayout.SOUTH);

        // Listeners and behaviors
        configureListeners(statusLabel);

        // Pack and center on screen
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Configure listeners for UI components including keyboard bindings.
     *
     * @param statusLabel label used for small, ephemeral messages
     */
    private void configureListeners(final JLabel statusLabel) {
        // Add action: button click or Enter key in the text field
        ActionListener addAction = e -> {
            try {
                String raw = taskField.getText();
                addTask(raw);
                statusLabel.setText("Task added.");
            } catch (IllegalArgumentException ex) {
                showWarning(ex.getMessage());
                statusLabel.setText("Add failed: " + ex.getMessage());
            } catch (Exception ex) {
                // Catch unexpected exceptions to avoid app crashes and log for maintainers
                showError("An unexpected error occurred while adding the task.");
                ex.printStackTrace();
                statusLabel.setText("Unexpected error while adding task.");
            }
        };

        addButton.addActionListener(addAction);
        taskField.addActionListener(addAction); // Enter in text field triggers add

        // Delete action
        deleteButton.addActionListener(e -> {
            try {
                deleteSelectedTasks();
                statusLabel.setText("Selected task(s) deleted.");
            } catch (Exception ex) {
                showError("An unexpected error occurred while deleting the task(s).");
                ex.printStackTrace();
                statusLabel.setText("Unexpected error while deleting task(s).");
            }
        });

        // Enable delete when items are selected
        taskList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Avoid reacting to intermediate events
                if (e.getValueIsAdjusting()) return;
                deleteButton.setEnabled(!taskList.isSelectionEmpty());
            }
        });

        // Keyboard shortcuts via InputMap/ActionMap for better UX
        // Ctrl+N focuses input field (new task)
        KeyStroke ctrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
        taskField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlN, "focusNew");
        taskField.getActionMap().put("focusNew", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                taskField.requestFocusInWindow();
                taskField.selectAll();
            }
        });

        // Delete key deletes selected tasks (safe)
        KeyStroke deleteKey = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        taskList.getInputMap(JComponent.WHEN_FOCUSED).put(deleteKey, "deleteSelected");
        taskList.getActionMap().put("deleteSelected", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (deleteButton.isEnabled()) {
                    deleteButton.doClick();
                }
            }
        });

        // Escape clears selection & input
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        taskList.getInputMap(JComponent.WHEN_FOCUSED).put(esc, "clearSelection");
        taskList.getActionMap().put("clearSelection", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                taskList.clearSelection();
                taskField.requestFocusInWindow();
            }
        });

        // Window listener to confirm exit if there are tasks present (optional safety)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!listModel.isEmpty()) {
                    int option = JOptionPane.showConfirmDialog(
                            TodoApp.this,
                            "You have " + listModel.getSize() + " task(s) in the list.\nAre you sure you want to exit?",
                            "Confirm Exit",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );
                    if (option == JOptionPane.YES_OPTION) {
                        // proceed with close
                        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    } else {
                        // cancel close
                        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    }
                }
            }
        });
    }

    /**
     * Add a task to the list after validating input.
     *
     * @param rawInput raw user input (may be null or blank)
     * @throws IllegalArgumentException if input is invalid (empty/duplicate)
     */
    private void addTask(String rawInput) {
        if (rawInput == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        String task = rawInput.trim();
        if (task.isEmpty()) {
            throw new IllegalArgumentException("Task cannot be empty.");
        }

        if (containsDuplicate(task)) {
            throw new IllegalArgumentException("Task already exists.");
        }

        // Good to go: add task
        listModel.addElement(task);
        taskField.setText("");
        taskField.requestFocusInWindow();
        // Auto-select the newly added element for visibility
        int newIndex = listModel.size() - 1;
        taskList.setSelectedIndex(newIndex);
        taskList.ensureIndexIsVisible(newIndex);
    }

    /**
     * Deletes selected tasks (supports multiple selection).
     * Confirms deletion with the user if multiple items are being removed.
     */
    private void deleteSelectedTasks() {
        int[] selectedIndices = taskList.getSelectedIndices();

        if (selectedIndices == null || selectedIndices.length == 0) {
            // Nothing selected: friendly message
            showInfo("No task selected to delete. Select a task first.");
            return;
        }

        // If user selected many tasks, confirm
        if (selectedIndices.length > 1) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete the selected " + selectedIndices.length + " tasks?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (choice != JOptionPane.YES_OPTION) {
                return; // user canceled
            }
        }

        // Remove from highest index to lowest to avoid shifting issues
        try {
            for (int i = selectedIndices.length - 1; i >= 0; i--) {
                int idx = selectedIndices[i];
                if (idx >= 0 && idx < listModel.getSize()) {
                    listModel.remove(idx);
                } // else: ignore out-of-range index defensively
            }

            // Update selection: select the item just above the first removed position if possible
            int size = listModel.getSize();
            if (size > 0) {
                int sel = Math.min(selectedIndices[0], size - 1);
                taskList.setSelectedIndex(sel);
                taskList.ensureIndexIsVisible(sel);
            }
        } catch (Exception ex) {
            // Defensive: do not crash UI; show friendly message and log
            showError("Failed to delete selected task(s). Please try again.");
            ex.printStackTrace();
        }
    }

    /**
     * Checks whether the current list already contains the candidate task.
     * The check is case-insensitive and ignores leading/trailing spaces.
     *
     * @param candidate candidate task (already trimmed)
     * @return true if a duplicate exists
     */
    private boolean containsDuplicate(String candidate) {
        if (candidate == null) return false;
        String normalized = candidate.trim().toLowerCase();
        for (int i = 0; i < listModel.getSize(); i++) {
            String existing = listModel.getElementAt(i);
            if (existing != null && existing.trim().toLowerCase().equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Show an informational message to the user.
     *
     * @param message message to show
     */
    private void showInfo(String message) {
        // Use non-blocking message if appropriate; here we use modal info dialog for visibility
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show a warning to the user (e.g., validation errors).
     *
     * @param message message to show
     */
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Show an error to the user (unexpected failure).
     *
     * @param message message to show
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Main entrypoint - launches the UI on the Event Dispatch Thread (EDT).
     *
     * @param args optional command-line args (not used)
     */
    public static void main(String[] args) {
        // Ensure UI is created on the EDT
        SwingUtilities.invokeLater(() -> {
            // Set system look and feel if available for native appearance
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // If setting L&F fails, fall back to default; don't crash
                System.err.println("Warning: failed to set system look and feel: " + e.getMessage());
            }

            TodoApp app = new TodoApp();
            app.setVisible(true);
        });
    }
}
