package com.hamed.postmantoretrofit2v2.forms;

import com.hamed.postmantoretrofit2v2.Constants;
import com.hamed.postmantoretrofit2v2.forms.listeners.DialogClosedListener;
import com.hamed.postmantoretrofit2v2.forms.listeners.JsonDialogReturnedData;
import com.hamed.postmantoretrofit2v2.forms.listeners.OptionsDialogReturnedData;
import com.hamed.postmantoretrofit2v2.forms.listeners.ReturnedData;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginService;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginState;
import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.AutomaticClassGenerationOptions;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
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
    private JCheckBox dynamicHeaderCheckBox;
    private JButton buttonShowSelectFileDialog;
    private JButton buttonOptions;
    private JFileChooser fileChooser;
    private final Project mProject;
    private DialogClosedListener dialogClosedListener;

    OptionsDialog optionsDialog;
    AutomaticClassGenerationOptions automaticClassGenerationOptions;
    Container optionsDialogContentPane;

    public JsonDialog(Project project) {
        mProject = project;
        optionsDialog = new OptionsDialog(this, mProject);
        optionsDialogContentPane = optionsDialog.getContentPane();
        automaticClassGenerationOptions = new AutomaticClassGenerationOptions();

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        setupFileChooser();
        setupActionListeners();
    }

    private void setupFileChooser() {
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;

        // Open the last directory that the user navigated to
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
        fileChooser.addActionListener( e -> {
            if (e.getActionCommand().equals(APPROVE_SELECTION))
            {
                File selectedFile = fileChooser.getSelectedFile();

                // Save the current directory
                String currentDirectory = fileChooser.getCurrentDirectory().toString();
                state.setLastVisitedDir(currentDirectory);
                try {
                    String fileContent = Files.readString(selectedFile.toPath());
                    jsonTextArea.setText(fileContent);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void setupActionListeners() {

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

    private void onOK() {
        dispose();
        if (dialogClosedListener != null)
            dialogClosedListener.onUserConfirm(new JsonDialogReturnedData(jsonTextArea.getText(), dynamicHeaderCheckBox.isSelected(), automaticClassGenerationOptions));
    }

    private void onCancel() {
        dispose();
        if (dialogClosedListener != null)
            dialogClosedListener.onCancelled();
    }

    private void onShowSelectFileDialog() {
        fileChooser.showOpenDialog(getRootPane());
    }

    private void onDisplayOptionsDialog()
    {
        optionsDialog.setContentPane(optionsDialogContentPane);
        optionsDialog.pack();
        optionsDialog.setTitle(Constants.UIConstants.OPTIONS_DIALOG_TITLE);
        optionsDialog.setSize(Constants.UIConstants.DIALOG_WIDTH, Constants.UIConstants.DIALOG_HEIGHT);
        optionsDialog.setLocation(this.getLocation());
        optionsDialog.setDialogClosedListener(new DialogClosedListener() {
            @Override
            public void onCancelled() {
                // nothing to do
            }

            @Override
            public void onUserConfirm(ReturnedData data) {
                OptionsDialogReturnedData dialogReturnedData = (OptionsDialogReturnedData) data;
                automaticClassGenerationOptions = dialogReturnedData.getAutomaticClassGenerationOptions();
            }
        });
        optionsDialog.setVisible(true);
    }

    public void setOnDialogClosedListener(DialogClosedListener dialogClosedListener)
    {
        this.dialogClosedListener = dialogClosedListener;
    }

    private void createUIComponents() {
        dynamicHeaderCheckBox = new JCheckBox();
    }
}
