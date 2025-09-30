# To-Do List App (Java Swing)

A **GUI-based To-Do List application** written in **Java using Swing**.  
This program allows users to **add, view, and delete tasks** with a user-friendly graphical interface.  
It demonstrates practical usage of **Java Swing components, event handling, and robust input validation**.

---

## Features

- **Add Task**  
  - Enter a task in the text field and click the **Add** button (or press Enter).  
  - Prevents adding **empty or duplicate tasks**.  
  - Automatically selects and scrolls to the newly added task.

- **Delete Task**  
  - Select one or multiple tasks and click the **Delete** button (or press the Delete key).  
  - Handles cases where **no task is selected** safely.  
  - Confirms deletion when multiple tasks are selected.

- **Input Validation & Error Handling**  
  - Prevents empty or duplicate tasks.  
  - Provides **friendly dialogs** instead of crashing.  
  - Handles all corner cases like deleting from an empty list or invalid indices.

- **User-Friendly Interface**  
  - Modern GUI with **JFrame, JButton, JTextField, JList**, and **DefaultListModel**.  
  - Keyboard shortcuts:  
    - **Enter** to add a task.  
    - **Delete** key to remove selected tasks.  
    - **Ctrl + N** to focus input field.  
    - **Escape** to clear selection and focus input.

- **Robust & Modular Design**  
  - Clear separation of methods for **adding, deleting, and validating tasks**.  
  - Proper exception handling to prevent unexpected crashes.  
  - Comments throughout for maintainability.

---

## Project Structure

```
TodoApp/
|- TodoApp.java    # Main Java Swing program
|- README.md       # Project documentation
```

---

## How to Run

1. **Clone or download** the project.  

2. Open a terminal and navigate to the project folder.  

3. Compile and run the program:  

```bash
javac TodoApp.java
java TodoApp
```

---

## Example Usage

**Adding a Task:**

```text
Enter a task in the text field: Finish project report
Click Add or press Enter
Task added successfully!
```

**Deleting a Task:**

```text
Select one or more tasks in the list
Click Delete button or press Delete key
Selected task(s) removed successfully!
```

**Error Handling Example (Empty Task):**

```text
Attempting to add an empty task
Warning dialog: "Task cannot be empty."
```

**Error Handling Example (Duplicate Task):**

```text
Attempting to add a task that already exists
Warning dialog: "Task already exists."
```

---

## Concepts Showcased

- **Java Swing GUI Components**  
  - `JFrame`, `JButton`, `JTextField`, `JList`, `DefaultListModel`.  
  - Scrollable lists with `JScrollPane` for better user experience.

- **Event Handling**  
  - Action listeners for buttons and text fields.  
  - List selection listener for enabling/disabling the Delete button.

- **Input Validation & Exception Handling**  
  - Prevent empty and duplicate tasks.  
  - Safe handling of corner cases without crashing the app.  
  - Friendly messages via `JOptionPane`.

- **Modular Code Design**  
  - Methods separated by functionality (`addTask`, `deleteSelectedTasks`, `containsDuplicate`, etc.).  
  - Easy to maintain and extend.

---

## Future Enhancements

- Add **persistent storage** (e.g., save tasks to a file or database).  
- Implement **task editing** (modify existing tasks).  
- Add **due dates, priorities, or categories** for tasks.  
- Apply **modern themes** or **custom styling** for better UX.  
- Add **drag-and-drop reordering** for tasks.  
- Convert to a **full-featured desktop productivity app**.

---