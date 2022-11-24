package com.hamed.postmantoretrofit2v2.forms;

import com.hamed.postmantoretrofit2v2.PluginService;
import com.hamed.postmantoretrofit2v2.PluginState;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.JFileChooser.APPROVE_SELECTION;
import static javax.swing.JFileChooser.DIRECTORIES_ONLY;

public class OptionsDialog extends JDialog {
    private JPanel panel1;
    private JLabel generationOptionsLabel;
    private JComboBox<String> responseTypeComboBox;
    private JButton buttonCancel;
    private JButton buttonOk;
    private JCheckBox useRxJavaResponseTypesCheckBox;
    private JCheckBox promptToSelectACheckBox;
    private JTextField selectedDirTextField;
    private JButton browseButton;
    private String storedSelectedResponseType;
    private String initialSelectedResponseType;
    private Boolean isInitialUseRxJavaResponseTypesCheckBoxStateSelected;

    private final String[] retrofit2RawTypes = { "Call<T>", "Call<Response<T>>" };

    private final String[] rxJavaResponseTypes = { "Observable<T>", "Observable<Response<T>>", "Observable<Result<T>>",
                                                   "Flowable<T>", "Flowable<Response<T>>", "Flowable<Result<T>>", "Single<T>", "Single<Response<T>>",
                                                   "Single<Result<T>>", "Maybe<T>", "Maybe<Response<T>>", "Maybe<Result<T>>", "Completable" };
    private final JFileChooser fileChooser;

    public OptionsDialog(JDialog owner, Project project) {
        super(owner);
        setContentPane(panel1);
        getRootPane().setDefaultButton(buttonOk);
        PluginState state = PluginService.getInstance().getState();
        System.out.println("OptionsDialog");
        storedSelectedResponseType = "";

        buttonOk.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        useRxJavaResponseTypesCheckBox.addActionListener(e ->
        {
            if (e.getID() == ActionEvent.ACTION_PERFORMED)
            {
                System.out.println("useRxJavaResponseTypesCheckBox.isSelected(): " + useRxJavaResponseTypesCheckBox.isSelected());
                if (useRxJavaResponseTypesCheckBox.isSelected()) {
                    responseTypeComboBox.removeAllItems();
                    for (String rxJavaResponseType : rxJavaResponseTypes)
                        responseTypeComboBox.addItem(rxJavaResponseType);
                }
                else
                {
                    responseTypeComboBox.removeAllItems();
                    for (String retrofit2RawType : retrofit2RawTypes) responseTypeComboBox.addItem(retrofit2RawType);
                }
            }
        });

        responseTypeComboBox.addActionListener(e ->
        {
            if (e.getActionCommand().equals(responseTypeComboBox.getActionCommand()))
            {
                if (getResponseTypeComboBoxItem() != null) {
                    if (storedSelectedResponseType.equals(getResponseTypeComboBoxItem())) {
                        // Nothing changed, so do nothing
                    } else {
                        storedSelectedResponseType = getResponseTypeComboBoxItem();
                    }
                }
                responseTypeComboBox.hidePopup();
            }
        });

        promptToSelectACheckBox.addActionListener(e ->
        {
            if (e.getID() == ActionEvent.ACTION_PERFORMED)
            {
                if (promptToSelectACheckBox.isSelected()) {
                    state.setPromptToSelectClassForResponseType(true);
                    browseButton.setEnabled(true);
                    selectedDirTextField.setEnabled(true);
                }
                else
                {
                    state.setPromptToSelectClassForResponseType(false);
                    browseButton.setEnabled(false);
                    selectedDirTextField.setEnabled(false);
                }
            }
        });

        // Set the values from the stored state
        if (state.getPromptToSelectClassForResponseType())
            promptToSelectACheckBox.doClick();

        if (!state.getJavaFilesDirectory().isEmpty())
            selectedDirTextField.setText(state.getJavaFilesDirectory());
        else
            selectedDirTextField.setText(project.getBasePath());

        // Display the last response type selected by the user
        if (!state.getResponseType().isEmpty()) {
            System.out.println("state response type: " + state.getResponseType());
            if (!useRxJavaResponseTypesCheckBox.isSelected() && new ArrayList<>(List.of(rxJavaResponseTypes)).contains(state.getResponseType()))
                useRxJavaResponseTypesCheckBox.doClick();

            responseTypeComboBox.setSelectedItem(state.getResponseType());
            storedSelectedResponseType = state.getResponseType();
        }

        System.out.println("project.getWorkspaceFile(): " + project.getBasePath());
        if (state.getJavaFilesDirectory().isEmpty()) {
            fileChooser = new JFileChooser(project.getBasePath());
            state.setJavaFilesDirectory(project.getBasePath());
        }
        else
            fileChooser = new JFileChooser(state.getJavaFilesDirectory());

        fileChooser.setFileSelectionMode(DIRECTORIES_ONLY);

        fileChooser.addActionListener(e ->
        {
            if (e.getActionCommand().equals(APPROVE_SELECTION))
            {
                File selectedDirectory = fileChooser.getSelectedFile();
                System.out.println("selectedFile: " + selectedDirectory.getAbsolutePath());
                selectedDirTextField.setText(selectedDirectory.getAbsolutePath());
            }
        });

        browseButton.addActionListener( e -> fileChooser.showOpenDialog(getRootPane()));

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        panel1.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    @Override
    public void setVisible(boolean b) {
        System.out.println("setVisible");
        if (b) {
            initialSelectedResponseType = getResponseTypeComboBoxItem();
            isInitialUseRxJavaResponseTypesCheckBoxStateSelected = useRxJavaResponseTypesCheckBox.isSelected();
        }
        super.setVisible(b);
    }

    private void onOK() {
        // Save the response type
        PluginState state = PluginService.getInstance().getState();
        state.setResponseType(getResponseTypeComboBoxItem());
        state.setJavaFilesDirectory(selectedDirTextField.getText());
        state.setPromptToSelectClassForResponseType(promptToSelectACheckBox.isSelected());
        // Do nothing as the json dialog has access to the necessary functions from here.
        setVisible(false);
    }

    private void onCancel() {

        System.out.println("onCancel");

        // Set back the initial values
        if (isInitialUseRxJavaResponseTypesCheckBoxStateSelected) {

            // Initial state of the checkbox is the same is the current state
            if (useRxJavaResponseTypesCheckBox.isSelected())
                responseTypeComboBox.setSelectedItem(initialSelectedResponseType);
            else
            {
                useRxJavaResponseTypesCheckBox.doClick();
                responseTypeComboBox.setSelectedItem(initialSelectedResponseType);
            }
        }
        else
        {
            if (useRxJavaResponseTypesCheckBox.isSelected()) {
                useRxJavaResponseTypesCheckBox.doClick();
                responseTypeComboBox.setSelectedItem(initialSelectedResponseType);
            }
            else
            {
                // Initial state of the checkbox is the same is the current state
                responseTypeComboBox.setSelectedItem(initialSelectedResponseType);
            }
        }

        // add your code here if necessary
        setVisible(false);
    }

    private String getResponseTypeComboBoxItem()
    {
        return (String) responseTypeComboBox.getSelectedItem();
    }

    public String getRxJavaResponseFormat()
    {
        return getResponseTypeComboBoxItem();
    }
}
