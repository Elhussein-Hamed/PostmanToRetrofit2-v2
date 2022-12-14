package com.hamed.postmantoretrofit2v2.forms;

import com.hamed.postmantoretrofit2v2.Constants;
import com.hamed.postmantoretrofit2v2.pluginstate.Language;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginService;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginState;
import com.hamed.postmantoretrofit2v2.pluginstate.ReturnTypeRadioButton;
import com.hamed.postmantoretrofit2v2.utils.ClassInfo;
import com.hamed.postmantoretrofit2v2.utils.ProjectUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import static javax.swing.JFileChooser.APPROVE_SELECTION;
import static javax.swing.JFileChooser.DIRECTORIES_ONLY;

public class OptionsDialog extends JDialog {
    private JPanel contentPane;
    @SuppressWarnings("unused")
    private JLabel generationOptionsLabel;
    private JComboBox<String> returnTypeComboBox;
    private JButton buttonCancel;
    private JButton buttonOk;
    private JCheckBox promptToSelectClassCheckBox;
    private JTextField selectedDirTextField;
    private JButton browseButton;
    private JComboBox<String> languageComboBox;
    private JRadioButton retrofitTypesRadioButton;
    private JRadioButton rxJavaTypesRadioButton;
    private JRadioButton retrofitCoroutinesRadioButton;
    private ButtonGroup returnTypeButtonGroup;
    private String storedSelectedReturnType;
    private String initialSelectedReturnType;
    private String initialSelectedDirectory;
    private Boolean isInitialPromptToSelectClassCheckBoxStateSelected;

    private String initialLanguage;

    private String initialSelectedReturnTypeRadioButton;

    private final JFileChooser fileChooser;

    private final Project mProject;

    public OptionsDialog(JDialog owner, Project project) {
        super(owner);
        mProject = project;
        PluginState state = PluginService.getInstance(project).getState();
        assert state != null;

        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOk);

        storedSelectedReturnType = "";
        initialSelectedDirectory = "";

        // Create custom action command to make it easier to know which button is which
        retrofitTypesRadioButton.setActionCommand(ReturnTypeRadioButton.BUTTON_1.name()); // BUTTON_1
        rxJavaTypesRadioButton.setActionCommand(ReturnTypeRadioButton.BUTTON_2.name()); // BUTTON_2
        retrofitCoroutinesRadioButton.setActionCommand(ReturnTypeRadioButton.BUTTON_3.name()); // BUTTON_3

        // Set the listeners for the GUI components
        buttonOk.addActionListener(this::onOK);
        buttonCancel.addActionListener(e -> onCancel());
        languageComboBox.addActionListener(this::onLanguageComboBoxChanged);
        returnTypeComboBox.addActionListener(this::onReturnTypeComboBoxChanged);
        promptToSelectClassCheckBox.addActionListener(this::onPromptToSelectClassCheckBoxStateChanged);
        retrofitTypesRadioButton.addItemListener(e -> onRadioButtonSelected(e, Constants.retrofit2RawTypes));
        rxJavaTypesRadioButton.addItemListener(e -> onRadioButtonSelected(e, Constants.rxJavaReturnTypes));
        retrofitCoroutinesRadioButton.addItemListener(e -> onRadioButtonSelected(e, Constants.retrofit2RawTypesKotlinCoroutines));

        // Update the GUI components to reflect the restored state
        if (state.getLanguage() == Language.JAVA)
            languageComboBox.setSelectedIndex(0);
        else
            languageComboBox.setSelectedIndex(1);

        // Display the last return type selected by the user
        if (!state.getReturnType().isEmpty()) {

            for (Iterator<AbstractButton> it = returnTypeButtonGroup.getElements().asIterator(); it.hasNext(); ) {
                AbstractButton button = it.next();
                if (button.getActionCommand().equals(state.getReturnTypeRadioButton().name()))
                    button.doClick();
            }

            returnTypeComboBox.setSelectedItem(state.getReturnType());
            storedSelectedReturnType = state.getReturnType();
        }

        if (state.getPromptToSelectClassForReturnType())
            promptToSelectClassCheckBox.doClick();

        // Setting up the Directory chooser
        if (state.getReturnTypeClassesDirectory().isEmpty()) {
            fileChooser = new JFileChooser(project.getBasePath());
            selectedDirTextField.setText(project.getBasePath());
        }
        else {
            selectedDirTextField.setText(state.getReturnTypeClassesDirectory());
            fileChooser = new JFileChooser(state.getReturnTypeClassesDirectory());
        }

        fileChooser.setFileSelectionMode(DIRECTORIES_ONLY);
        fileChooser.addActionListener(this::onDirectorySelected);

        browseButton.addActionListener( e -> fileChooser.showOpenDialog(getRootPane()));

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

    private void onLanguageComboBoxChanged(ActionEvent e) {
        if (e.getActionCommand().equals(languageComboBox.getActionCommand()))
        {
            if (languageComboBox.getSelectedIndex() == 0) { // Java

                // Disable the coroutines option as it is not applicable to java
                retrofitCoroutinesRadioButton.setEnabled(false);

                // If it was selected unselect it and select the first one instead
                if (returnTypeButtonGroup.getSelection().getActionCommand().equals(retrofitCoroutinesRadioButton.getActionCommand())) {
                    retrofitTypesRadioButton.setSelected(true);
                }
            }
            else { // Kotlin

                // Enable the coroutines option
                retrofitCoroutinesRadioButton.setEnabled(true);
            }
        }
    }

    private void onRadioButtonSelected(ItemEvent e, String [] returnTypesList) {
        if (e.getStateChange() == ItemEvent.SELECTED)
        {
            returnTypeComboBox.removeAllItems();
            for (String returnType: returnTypesList)
                returnTypeComboBox.addItem(returnType);
        }
    }

    private void onReturnTypeComboBoxChanged(ActionEvent e) {

        if (e.getActionCommand().equals(returnTypeComboBox.getActionCommand()))
        {
            if (getReturnTypeComboBoxItem() != null) {
                if (!storedSelectedReturnType.equals(getReturnTypeComboBoxItem())) {
                    storedSelectedReturnType = getReturnTypeComboBoxItem();
                }
                // else do nothing
            }
        }
    }

    private void onDirectorySelected(ActionEvent e) {
        if (e.getActionCommand().equals(APPROVE_SELECTION))
        {
            File selectedDirectory = fileChooser.getSelectedFile();
            System.out.println("selected directory: " + selectedDirectory.getAbsolutePath());
            selectedDirTextField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void onPromptToSelectClassCheckBoxStateChanged(ActionEvent e) {

        if (e.getID() == ActionEvent.ACTION_PERFORMED)
        {
            if (promptToSelectClassCheckBox.isSelected()) {
                browseButton.setEnabled(true);
                selectedDirTextField.setEnabled(true);
            }
            else
            {
                browseButton.setEnabled(false);
                selectedDirTextField.setEnabled(false);
            }
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            initialLanguage = getLanguageComboBoxItem();
            initialSelectedReturnType = getReturnTypeComboBoxItem();
            initialSelectedReturnTypeRadioButton = returnTypeButtonGroup.getSelection().getActionCommand();
            isInitialPromptToSelectClassCheckBoxStateSelected  = promptToSelectClassCheckBox.isSelected();
            initialSelectedDirectory = selectedDirTextField.getText();
        }
        getRootPane().setDefaultButton(buttonOk);
        super.setVisible(b);
    }

    private void onOK(ActionEvent e) {
        // Save the return type
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;

        state.setReturnType(getReturnTypeComboBoxItem());
        state.setReturnTypeRadioButton(ReturnTypeRadioButton.valueOf(returnTypeButtonGroup.getSelection().getActionCommand()));
        state.setPromptToSelectClassForReturnType(promptToSelectClassCheckBox.isSelected());
        String language = (String) languageComboBox.getSelectedItem();
        assert language != null;
        state.setLanguage(Language.valueOf((language).toUpperCase()));

        if (!state.getReturnTypeClassesDirectory().equals(selectedDirTextField.getText())) {
            state.setReturnTypeClassesDirectory(selectedDirTextField.getText());

            if (promptToSelectClassCheckBox.isSelected())
            {
                ArrayList<ClassInfo> classesList = new ArrayList<>();

                ApplicationManager.getApplication().executeOnPooledThread(() -> {
                    classesList.addAll(ApplicationManager.getApplication().runReadAction(new Computable<ArrayList<ClassInfo>>() {
                        @Override
                        public ArrayList<ClassInfo> compute() {
                            VirtualFile file = LocalFileSystem.getInstance().findFileByPath(state.getReturnTypeClassesDirectory());
                            return ProjectUtils.getClassesInDirectory(mProject, file, classesList);
                        }
                    }));

                    state.setReturnTypeClassInfoList(classesList);
                });
            }
        }

        dispose();
    }

    private void onCancel() {

        // Set back the initial values
        if(!initialLanguage.equals(getLanguageComboBoxItem()))
            languageComboBox.setSelectedItem(initialLanguage);

        if(!initialSelectedReturnTypeRadioButton.equals(returnTypeButtonGroup.getSelection().getActionCommand())) {
            for (Iterator<AbstractButton> it = returnTypeButtonGroup.getElements().asIterator(); it.hasNext(); ) {
                AbstractButton button = it.next();
                if (button.getActionCommand().equals(initialSelectedReturnTypeRadioButton))
                    button.doClick();
            }

            if (!initialSelectedReturnType.equals(getReturnTypeComboBoxItem()))
                returnTypeComboBox.setSelectedItem(initialSelectedReturnType);
        }

        if (!initialSelectedDirectory.equals(selectedDirTextField.getText()))
            selectedDirTextField.setText(initialSelectedDirectory);

        if (isInitialPromptToSelectClassCheckBoxStateSelected != promptToSelectClassCheckBox.isSelected())
            promptToSelectClassCheckBox.doClick();

        dispose();
    }

    private String getReturnTypeComboBoxItem()
    {
        return (String) returnTypeComboBox.getSelectedItem();
    }

    private String getLanguageComboBoxItem()
    {
        return (String) languageComboBox.getSelectedItem();
    }

    public String getRxJavaReturnFormat()
    {
        return getReturnTypeComboBoxItem();
    }
}
