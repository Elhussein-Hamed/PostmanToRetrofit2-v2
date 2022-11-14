package com.hamed.postmantoretrofit2v2;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static javax.swing.JFileChooser.APPROVE_SELECTION;

public class JsonDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea jsonTextArea;
    private JCheckBox dynamic_header;
    private JButton buttonSelectFile;
    private JButton buttonOptions;

    private final JFileChooser fileChooser;

    private final Model mModel;

    private final OptionsDialog optionsDialog;

    public JsonDialog(Project project, Editor editor) {

//        if (UIUtil.isUnderDarcula())
//            FlatDarculaLaf.setup();
//        else
//            FlatLightLaf.setup();

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        mModel = new Model(project, editor);
        optionsDialog = new OptionsDialog(this);

        // Open the last directory that the user navigated to
        PluginState state = PluginService.getInstance().getState();
        if (!state.getFileSelectionDir().isEmpty())
            fileChooser = new JFileChooser(state.getFileSelectionDir());
        else
            fileChooser = new JFileChooser();

        fileChooser.addActionListener(e ->
        {
            if (e.getActionCommand().equals(APPROVE_SELECTION))
            {
                File selectedFile = fileChooser.getSelectedFile();

                // Save the current directory
                String currentDirectory = fileChooser.getCurrentDirectory().toString();
                state.setFileSelectionDir(currentDirectory);
                try {
                    String fileContent = Files.readString(selectedFile.toPath());
                    jsonTextArea.setText(fileContent);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        buttonSelectFile.addActionListener( e -> onSelectFile());

        buttonOptions.addActionListener(e -> onOptions());

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
        Collection collection = mModel.parsePostman(jsonTextArea.getText());
        System.out.println("RxJava Response Format: " + optionsDialog.getRxJavaResponseFormat());
        if(collection!=null) mModel.generateRxJavaCode(collection.getItem(), dynamic_header.isSelected(), optionsDialog.getRxJavaResponseFormat());

        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void onSelectFile() {
        fileChooser.showOpenDialog(getRootPane());
    }

    private void onOptions()
    {
        optionsDialog.pack();
        optionsDialog.setTitle("Options");
        optionsDialog.setSize(600, 400);

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
