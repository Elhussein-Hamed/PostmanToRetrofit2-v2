package com.hamed.postmantoretrofit2v2.forms;

import com.hamed.postmantoretrofit2v2.PluginService;
import com.hamed.postmantoretrofit2v2.PluginState;
import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packageDependencies.ForwardDependenciesBuilder;
import com.intellij.psi.PsiFile;

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
    private JCheckBox promptToSelectClassCheckBox;
    private JTextField selectedDirTextField;
    private JButton browseButton;
    private String storedSelectedResponseType;
    private String initialSelectedResponseType;
    private Boolean isInitialUseRxJavaResponseTypesCheckBoxStateSelected;
    private String initialSelectedDirectory;
    private Boolean isInitialPromptToSelectClassCheckBoxStateSelected;

    private final String[] retrofit2RawTypes = { "Call<T>", "Call<Response<T>>" };

    private final String[] rxJavaResponseTypes = { "Observable<T>", "Observable<Response<T>>", "Observable<Result<T>>",
                                                   "Flowable<T>", "Flowable<Response<T>>", "Flowable<Result<T>>", "Single<T>", "Single<Response<T>>",
                                                   "Single<Result<T>>", "Maybe<T>", "Maybe<Response<T>>", "Maybe<Result<T>>", "Completable" };
    private final JFileChooser fileChooser;

    private final Project mProject;

    public OptionsDialog(JDialog owner, Project project) {
        super(owner);
        mProject = project;

        setContentPane(panel1);
        getRootPane().setDefaultButton(buttonOk);
        System.out.println("OptionsDialog");
        storedSelectedResponseType = "";
        initialSelectedDirectory = "";

        // Set the listeners for the GUI components
        buttonOk.addActionListener(this::onOK);
        buttonCancel.addActionListener(e -> onCancel());
        useRxJavaResponseTypesCheckBox.addActionListener(this::onUseRxJavaResponseTypesCheckBoxStateChanged);
        responseTypeComboBox.addActionListener(this::onResponseTypeComboBoxChanged);
        promptToSelectClassCheckBox.addActionListener(this::onPromptToSelectClassCheckBoxStateChanged);

        // Update the GUI components to reflect the restored state
        PluginState state = PluginService.getInstance(project).getState();
        if (state.getPromptToSelectClassForResponseType())
            promptToSelectClassCheckBox.doClick();

        // Display the last response type selected by the user
        if (!state.getResponseType().isEmpty()) {
            System.out.println("state response type: " + state.getResponseType());
            if (!useRxJavaResponseTypesCheckBox.isSelected() && new ArrayList<>(List.of(rxJavaResponseTypes)).contains(state.getResponseType()))
                useRxJavaResponseTypesCheckBox.doClick();

            responseTypeComboBox.setSelectedItem(state.getResponseType());
            storedSelectedResponseType = state.getResponseType();
        }

        // Setting up the Directory chooser
        if (state.getResponseTypeClassesDirectory().isEmpty()) {
            fileChooser = new JFileChooser(project.getBasePath());
            selectedDirTextField.setText(project.getBasePath());
        }
        else {
            selectedDirTextField.setText(state.getResponseTypeClassesDirectory());
            fileChooser = new JFileChooser(state.getResponseTypeClassesDirectory());
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
        panel1.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onResponseTypeComboBoxChanged(ActionEvent e) {

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
    }

    private void onDirectorySelected(ActionEvent e) {
        if (e.getActionCommand().equals(APPROVE_SELECTION))
        {
            File selectedDirectory = fileChooser.getSelectedFile();
            System.out.println("selected directory: " + selectedDirectory.getAbsolutePath());
            selectedDirTextField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void onUseRxJavaResponseTypesCheckBoxStateChanged(ActionEvent e) {

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
        System.out.println("setVisible (" + b + ")");
        if (b) {
            initialSelectedResponseType = getResponseTypeComboBoxItem();
            isInitialUseRxJavaResponseTypesCheckBoxStateSelected = useRxJavaResponseTypesCheckBox.isSelected();
            isInitialPromptToSelectClassCheckBoxStateSelected    = promptToSelectClassCheckBox.isSelected();
            initialSelectedDirectory = selectedDirTextField.getText();
        }
        super.setVisible(b);
    }

    private void onOK(ActionEvent e) {
        // Save the response type
        PluginState state = PluginService.getInstance(mProject).getState();
        state.setResponseType(getResponseTypeComboBoxItem());
        state.setPromptToSelectClassForResponseType(promptToSelectClassCheckBox.isSelected());

        if (!state.getResponseTypeClassesDirectory().equals(selectedDirTextField.getText())) {
            state.setResponseTypeClassesDirectory(selectedDirTextField.getText());

            if (promptToSelectClassCheckBox.isSelected())
            {
                VirtualFile file = LocalFileSystem.getInstance().findFileByPath(state.getResponseTypeClassesDirectory());
                System.out.println("Selected classes directory virtual file: " + file.getPath());
                ArrayList<String> classesList = new ArrayList<>();

                ApplicationManager.getApplication().executeOnPooledThread(() -> {
                    classesList.addAll(ApplicationManager.getApplication().runReadAction(new Computable<ArrayList<String>>() {
                        @Override
                        public ArrayList<String> compute() {
                            ArrayList<String> javaFilesList = new ArrayList<>();
                            ForwardDependenciesBuilder forwardDependenciesBuilder = new ForwardDependenciesBuilder(mProject, new AnalysisScope(mProject, List.of(file)));
                            forwardDependenciesBuilder.analyze();
                            ArrayList<PsiFile> list = new ArrayList<>(forwardDependenciesBuilder.getDirectDependencies().keySet());
                            for (PsiFile f : list)
                                if (f.getName().contains(".java") && !classesList.contains(f.getName()))
                                    javaFilesList.add(f.getName().replace(".java", ""));
                            return javaFilesList;
                        }
                    }));

                    state.setResponseTypeClassesList(classesList);
                });
            }
        }

        dispose();
    }

    private void onCancel() {

        System.out.println("onCancel");

        // Set back the initial values
        if (isInitialUseRxJavaResponseTypesCheckBoxStateSelected != useRxJavaResponseTypesCheckBox.isSelected()) {
            useRxJavaResponseTypesCheckBox.doClick();
            responseTypeComboBox.setSelectedItem(initialSelectedResponseType);
        }
        else
        {
            responseTypeComboBox.setSelectedItem(initialSelectedResponseType);
        }

        if (!initialSelectedDirectory.equals(selectedDirTextField.getText()))
            selectedDirTextField.setText(initialSelectedDirectory);

        if (isInitialPromptToSelectClassCheckBoxStateSelected != promptToSelectClassCheckBox.isSelected())
            promptToSelectClassCheckBox.doClick();

        // add your code here if necessary
        dispose();
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
