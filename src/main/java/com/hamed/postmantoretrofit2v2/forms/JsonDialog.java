package com.hamed.postmantoretrofit2v2.forms;

import com.hamed.postmantoretrofit2v2.Collection;
import com.hamed.postmantoretrofit2v2.Model;
import com.hamed.postmantoretrofit2v2.PluginService;
import com.hamed.postmantoretrofit2v2.PluginState;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static javax.swing.JFileChooser.APPROVE_SELECTION;
import static javax.swing.JFileChooser.FILES_ONLY;

public class JsonDialog extends JDialog  {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea jsonTextArea;
    private JCheckBox dynamic_header;
    private JButton buttonShowSelectFileDialog;
    private JButton buttonOptions;
    private final JFileChooser fileChooser;
    private final Model mModel;
    private final OptionsDialog optionsDialog;
    private final Project mProject;

    public JsonDialog(Project project, Editor editor) {
        mProject = project;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        mModel = new Model(project, editor);
        optionsDialog = new OptionsDialog(this, project);

        // Open the last directory that the user navigated to
        PluginState state = PluginService.getInstance(project).getState();
        if (!state.getLastVisitedDir().isEmpty())
            fileChooser = new JFileChooser(state.getLastVisitedDir());
        else
            fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(FILES_ONLY);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".json");
            }

            @Override
            public String getDescription() {
                return "JSON file (*.json)";
            }
        });
        fileChooser.addActionListener(this::onFileSelected);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        buttonShowSelectFileDialog.addActionListener(e -> onShowSelectFileDialog());
        buttonOptions.addActionListener(e -> onDisplayOptionsDialog());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onFileSelected(ActionEvent e) {
        {
            if (e.getActionCommand().equals(APPROVE_SELECTION))
            {
                File selectedFile = fileChooser.getSelectedFile();

                // Save the current directory
                PluginState state = PluginService.getInstance(mProject).getState();
                String currentDirectory = fileChooser.getCurrentDirectory().toString();
                state.setLastVisitedDir(currentDirectory);
                try {
                    String fileContent = Files.readString(selectedFile.toPath());
                    jsonTextArea.setText(fileContent);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private void onOK() {
        Collection collection = mModel.parsePostman(jsonTextArea.getText());
        System.out.println("RxJava Response Format: " + optionsDialog.getRxJavaResponseFormat());
        try {
            if (collection != null)
                mModel.generateRxJavaCode(collection.getItem(), dynamic_header.isSelected(), optionsDialog.getRxJavaResponseFormat());
            else if (!jsonTextArea.getText().isEmpty()) {

                Notification notification = new Notification("Error Report"
                        , "Parsing error"
                        , "Failed to parse the postman collection, please check if the postman collection is correct " +
                        "or Create an issue " +
                        " <a href=\"https://github.com/Elhussein-Hamed/PostmanToRetrofit2-v2/issues\">here</a>"
                        , NotificationType.ERROR);

                notification.setListener(NotificationListener.URL_OPENING_LISTENER);
                notification.notify(mProject);
            }
        }
        catch (Exception e)
        {
            dispose();
            // Throw the exception again to be handled in PluginErrorReportSubmitter
            throw e;
        }

        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void onShowSelectFileDialog() {
        fileChooser.showOpenDialog(getRootPane());
    }

    private void onDisplayOptionsDialog()
    {
        optionsDialog.pack();
        optionsDialog.setTitle("Options");
        optionsDialog.setSize(600, 400);
        optionsDialog.setLocationRelativeTo(null);

        optionsDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent we) {
                buttonOK.requestFocusInWindow();
            }
        });

        optionsDialog.setVisible(true);
        optionsDialog.toFront();
        optionsDialog.requestFocus();
    }

    private void createUIComponents() {
        dynamic_header = new JCheckBox();
    }
}
