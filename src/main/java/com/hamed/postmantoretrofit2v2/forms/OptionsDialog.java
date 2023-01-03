package com.hamed.postmantoretrofit2v2.forms;

import com.hamed.postmantoretrofit2v2.Constants;
import com.hamed.postmantoretrofit2v2.forms.listeners.DialogClosedListener;
import com.hamed.postmantoretrofit2v2.forms.listeners.OptionsDialogReturnedData;
import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.*;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginService;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginState;
import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.enums.Framework;
import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.enums.Language;
import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.enums.ReturnTypeRadioButton;
import com.hamed.postmantoretrofit2v2.utils.ClassInfo;
import com.hamed.postmantoretrofit2v2.utils.ProjectUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.common.primitives.Ints.max;
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
    private JCheckBox automaticGenerationCheckBox;
    private JPanel automaticGenerationOptionsPanel;
    private JComboBox<String> frameworkComboBox;
    private ButtonGroup returnTypeButtonGroup;
    private String storedSelectedReturnType;
    private String initialSelectedReturnType;
    private String initialSelectedDirectory;
    private Boolean isInitialPromptToSelectClassCheckBoxStateSelected;

    private String initialLanguage;

    private String initialSelectedReturnTypeRadioButton;

    private JFileChooser fileChooser;

    private final Project mProject;

    private AutomaticClassGenerationOptions automaticClassGenerationOptions;

    private DialogClosedListener dialogClosedListener;

    public OptionsDialog(JDialog owner, Project project) {
        super(owner);
        mProject = project;
        automaticClassGenerationOptions = new AutomaticClassGenerationOptions();

        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOk);
        setModal(true);

        storedSelectedReturnType = "";
        initialSelectedDirectory = "";

        // Create custom action command to make it easier to know which button is which
        retrofitTypesRadioButton.setActionCommand(ReturnTypeRadioButton.BUTTON_RETROFIT_RAW_TYPES.name()); // BUTTON_1
        rxJavaTypesRadioButton.setActionCommand(ReturnTypeRadioButton.BUTTON_RXJAVA_TYPES.name()); // BUTTON_2
        retrofitCoroutinesRadioButton.setActionCommand(ReturnTypeRadioButton.BUTTON_RETROFIT_AND_COROUTINES.name()); // BUTTON_3

        setupDirectorySelector();
        setupActionListeners();
        restoreGuiStateFromPluginState();
    }

    private void setupDirectorySelector() {
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;

        // Setting up the Directory chooser
        if (state.getReturnTypeClassesDirectory().isEmpty()) {
            fileChooser = new JFileChooser(mProject.getBasePath());
            selectedDirTextField.setText(mProject.getBasePath());
        }
        else {
            selectedDirTextField.setText(state.getReturnTypeClassesDirectory());
            fileChooser = new JFileChooser(state.getReturnTypeClassesDirectory());
        }

        fileChooser.setFileSelectionMode(DIRECTORIES_ONLY);
        fileChooser.addActionListener(e -> {
            if (e.getActionCommand().equals(APPROVE_SELECTION))
            {
                File selectedDirectory = fileChooser.getSelectedFile();
                System.out.println("selected directory: " + selectedDirectory.getAbsolutePath());
                selectedDirTextField.setText(selectedDirectory.getAbsolutePath());
            }
        });
    }

    private void setupActionListeners() {

        // Set the listeners for the GUI components
        buttonOk.addActionListener(this::onOK);
        buttonCancel.addActionListener(e -> onCancel());

        languageComboBox.addItemListener( e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (languageComboBox.getSelectedIndex() == 0) { // Java

                    // Disable the coroutines option as it is not applicable to java
                    retrofitCoroutinesRadioButton.setEnabled(false);

                    // If it was selected unselect it and select the first one instead
                    if (returnTypeButtonGroup.getSelection().getActionCommand().equals(retrofitCoroutinesRadioButton.getActionCommand())) {
                        retrofitTypesRadioButton.setSelected(true);
                    }

                    // Also load the corresponding framework options
                    frameworkComboBox.removeAllItems();
                    for (int i = 0; i < Constants.javaSupportedJsonFrameworks.length; i++)
                        frameworkComboBox.addItem(Constants.javaSupportedJsonFrameworks[i]);
                } else { // Kotlin
                    // Enable the coroutines option
                    retrofitCoroutinesRadioButton.setEnabled(true);

                    // Also load the corresponding framework options
                    frameworkComboBox.removeAllItems();
                    for (int i = 0; i < Constants.kotlinSupportedJsonFrameworks.length; i++)
                        frameworkComboBox.addItem(Constants.kotlinSupportedJsonFrameworks[i]);
                }
            }
        });

        returnTypeComboBox.addActionListener(e -> {
            if (getReturnTypeComboBoxItem() != null) {
                if (!storedSelectedReturnType.equals(getReturnTypeComboBoxItem())) {
                    storedSelectedReturnType = getReturnTypeComboBoxItem();
                }
                // else do nothing
            }
        });

        promptToSelectClassCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                browseButton.setEnabled(true);
                selectedDirTextField.setEnabled(true);
            } else {
                browseButton.setEnabled(false);
                selectedDirTextField.setEnabled(false);
            }
        });

        retrofitTypesRadioButton.addItemListener(e -> onRadioButtonSelected(e, Constants.retrofit2RawTypes));
        rxJavaTypesRadioButton.addItemListener(e -> onRadioButtonSelected(e, Constants.rxJavaReturnTypes));
        retrofitCoroutinesRadioButton.addItemListener(e -> onRadioButtonSelected(e, Constants.retrofit2RawTypesKotlinCoroutines));

        browseButton.addActionListener( e -> fileChooser.showOpenDialog(getRootPane()));

        automaticGenerationCheckBox.addItemListener( e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                frameworkComboBox.setEnabled(true);
                for (Component component : automaticGenerationOptionsPanel.getComponents()) {
                    component.setEnabled(true);
                }
            }
            else {
                frameworkComboBox.setEnabled(false);
                for (Component component : automaticGenerationOptionsPanel.getComponents()) {
                    component.setEnabled(false);
                }
            }
        });

        frameworkComboBox.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        System.out.println("Selected item: " + e.getItem());
                        setupAutomaticClassGenerationOptionsPanel(Language.valueOf((getLanguageComboBoxItem()).toUpperCase()), !automaticGenerationCheckBox.isSelected());
                    }
                }
        );

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

    private void onRadioButtonSelected(ItemEvent e, String [] returnTypesList) {
        if (e.getStateChange() == ItemEvent.SELECTED)
        {
            returnTypeComboBox.removeAllItems();
            for (String returnType: returnTypesList)
                returnTypeComboBox.addItem(returnType);
        }
    }

    private void restoreGuiStateFromPluginState() {
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;

        // Update the GUI components to reflect the restored state
        if (state.getLanguage() == Language.JAVA)
            languageComboBox.setSelectedIndex(0);
        else
            languageComboBox.setSelectedIndex(1);

        // Display the last return type selected by the user
        if (!state.getReturnType().isEmpty()) {

            for (Iterator<AbstractButton> it = returnTypeButtonGroup.getElements().asIterator(); it.hasNext(); ) {
                AbstractButton button = it.next();
                if (state.getReturnTypeRadioButton() != null)
                    if (button.getActionCommand().equals(state.getReturnTypeRadioButton().name())) {
                        button.doClick();
                        break;
                    }
                    else {
                        if (List.of(Constants.retrofit2RawTypes).contains(state.getReturnType()))
                            retrofitTypesRadioButton.doClick();
                        else if (List.of(Constants.rxJavaReturnTypes).contains(state.getReturnType()))
                            rxJavaTypesRadioButton.doClick();
                        else
                            retrofitCoroutinesRadioButton.doClick();
                    }
            }

            returnTypeComboBox.setSelectedItem(state.getReturnType());
            storedSelectedReturnType = state.getReturnType();
        }


        if (state.getPromptToSelectClassForReturnType())
            promptToSelectClassCheckBox.doClick();

        // Set up the Automatic Class Generation Options Panel which contains the
        // generations options
        setupAutomaticClassGenerationOptionsPanel(state.getLanguage(), true);

        // Restore the state of automatic class generations check box
        // Initially set this to be deselected and send an event to disable the components
        // related to this check box.
        if (state.isAutomaticallyGenerateClassFromResponses())
            automaticGenerationCheckBox.doClick();
        else {
            automaticGenerationCheckBox.setSelected(true);
            automaticGenerationCheckBox.doClick();
        }

        frameworkComboBox.setSelectedItem(state.getFramework().toString());
    }

    public void setDialogClosedListener(DialogClosedListener dialogClosedListener) {
        this.dialogClosedListener = dialogClosedListener;
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            // Is there a better way to do this? This is done to restore the initial state when
            // the user click cancel (i.e. the settings are not saved). Without this, the UI will
            // show the wrong UI state.
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

        state.setAutomaticallyGenerateClassFromResponses(automaticGenerationCheckBox.isSelected());
        state.setFramework(Framework.fromString((String) frameworkComboBox.getSelectedItem()));

        System.out.println("automaticClassGenerationOptions: " + automaticClassGenerationOptions);
        if (dialogClosedListener != null)
            dialogClosedListener.onUserConfirm(new OptionsDialogReturnedData(automaticClassGenerationOptions));

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

        if (dialogClosedListener != null)
            dialogClosedListener.onCancelled();

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

    private void createUIComponents() {
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;

        if (state.getLanguage() == Language.JAVA)
            frameworkComboBox = new ComboBox<>(Constants.javaSupportedJsonFrameworks);
        else
            frameworkComboBox = new ComboBox<>(Constants.kotlinSupportedJsonFrameworks);
    }

    private void setupAutomaticClassGenerationOptionsPanel(Language language, boolean initialiseAsDisabled)
    {
        ArrayList<JCheckBox> optionsCheckBoxes;

        String framework = (String) frameworkComboBox.getSelectedItem();
        assert framework != null;

        // Reset automaticClassGenerationOptions
        automaticClassGenerationOptions = new AutomaticClassGenerationOptions();
        optionsCheckBoxes = createListOfCheckBoxesBasedOnOptions(language, framework, automaticClassGenerationOptions, initialiseAsDisabled);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.insets = JBUI.insetsLeft(8);
        int contentPaneWidthMinusMargin = contentPane.getMinimumSize().width - 50; // Added a margin to make sure no component parts appear out of the screen

        automaticGenerationOptionsPanel.removeAll();
        automaticGenerationOptionsPanel.revalidate();
        automaticGenerationOptionsPanel.repaint();

        //System.out.println("contentPaneWidthMinusMargin: " + contentPaneWidthMinusMargin);
        for (JCheckBox currentOptionsCheckBox : optionsCheckBoxes) {

            int automaticGenerationOptionsPanelWidth = automaticGenerationOptionsPanel.getMinimumSize().width;
            //System.out.println("optionsCheckBox: " + currentOptionsCheckBox.getMinimumSize().width);
            //System.out.println("automaticGenerationOptionsPanelWidth before component addition: " + automaticGenerationOptionsPanelWidth);

            // If the Panel first row was filled, move to the next column.
            // NOTE: This is not a fool-proof solution as it only works for the first row. After that,
            // it will move to a new column with every added CheckBox
            if (automaticGenerationOptionsPanelWidth + currentOptionsCheckBox.getMinimumSize().width >= contentPaneWidthMinusMargin) {

                System.out.println("Move to next column");
                gbc.gridy += 1;

                // To make the layout look good, span the added CheckBox into multiple cells if needed.
                int previousRawMaxCheckBoxWidth = retrievePreviousRowMaxCheckBoxWidth(optionsCheckBoxes, currentOptionsCheckBox);
                System.out.println("Previous row check box max width: " + previousRawMaxCheckBoxWidth);
                if (previousRawMaxCheckBoxWidth < currentOptionsCheckBox.getMinimumSize().width) {
                    gbc.gridwidth += 1;
                }
                else {
                    gbc.gridwidth = 1;
                }
            }

            automaticGenerationOptionsPanel.add(currentOptionsCheckBox, gbc);
            automaticGenerationOptionsPanel.revalidate();
        }
   }

   private int retrievePreviousRowMaxCheckBoxWidth(ArrayList<JCheckBox> checkBoxArrayList, JCheckBox currentCheckBox)
   {
       int maxWidth = 0;
       for (JCheckBox checkBox : checkBoxArrayList)
       {
           if (checkBox != currentCheckBox)
               maxWidth = max(maxWidth, checkBox.getMinimumSize().width);
           else
               break;
       }

       return maxWidth;
   }

   private ArrayList<JCheckBox> createListOfCheckBoxesBasedOnOptions(Language language, String framework, AutomaticClassGenerationOptions generationOptions, boolean initialiseAsDisabled) {

        ArrayList<JCheckBox> optionsCheckBoxes;

       if (language == Language.JAVA) {
           if (framework.equals(Framework.LOMBOK.toString())) {
               optionsCheckBoxes = createListOfCheckBoxes(2,
                       new String[]{"Use Java primitive fields", "Use @Value"},
                       new ItemListener[]{
                               e -> generationOptions
                                       .setUseJavaPrimitiveOptions(e.getStateChange() == ItemEvent.SELECTED),
                               e -> generationOptions
                                       .setUseAtValue(e.getStateChange() == ItemEvent.SELECTED)
                       }, initialiseAsDisabled);
           }
           else if (framework.equals(Framework.NONE_RECORDS.toString()) ||
                   framework.equals(Framework.GSON_RECORDS.toString()) ||
                   framework.equals(Framework.JACKSON_RECORDS.toString()) ||
                   framework.equals(Framework.LOGAN_SQUARE_RECORDS.toString()) ||
                   framework.equals(Framework.MOSHI_RECORDS.toString()) ||
                   framework.equals(Framework.FASTJSON_RECORDS.toString())) {
               optionsCheckBoxes = createListOfCheckBoxes(1,
                       new String[]{"Use Java primitive fields"},
                       new ItemListener[]{
                               e -> generationOptions
                                       .setUseJavaPrimitiveOptions(e.getStateChange() == ItemEvent.SELECTED)
                       }, initialiseAsDisabled);
           }
           else if (framework.equals(Framework.AUTO_VALUE.toString())) {
               optionsCheckBoxes = new ArrayList<>(); // No options
           }
           else {
               optionsCheckBoxes = createListOfCheckBoxes(4,
                       new String[]{"Use Java primitive fields", "Create setters", "Create getters", "Override toString()"},
                       new ItemListener[]{
                               e -> generationOptions
                                       .setUseJavaPrimitiveOptions(e.getStateChange() == ItemEvent.SELECTED),
                               e -> generationOptions
                                       .setCreateSetters(e.getStateChange() == ItemEvent.SELECTED),
                               e -> generationOptions
                                       .setCreateGetters(e.getStateChange() == ItemEvent.SELECTED),
                               e -> generationOptions
                                       .setOverrideToString(e.getStateChange() == ItemEvent.SELECTED)
                       },
                       initialiseAsDisabled);
           }
       }
       else {
           if (framework.equals(Framework.MOSHI.toString())) {
               optionsCheckBoxes = createListOfCheckBoxes(5,
                       new String[]{"Use Data classes", "Single file", "Nullable Fields", "Parcelable (Android)", "Generate Adapter"},
                       new ItemListener[]{
                               e -> generationOptions
                                       .setUseDataClasses(e.getStateChange() == ItemEvent.SELECTED),
                               e -> generationOptions
                                       .setSingleFile(e.getStateChange() == ItemEvent.SELECTED),
                               e -> generationOptions
                                       .setNullableFields(e.getStateChange() == ItemEvent.SELECTED),
                               e -> generationOptions
                                       .setParcelableAndroid(e.getStateChange() == ItemEvent.SELECTED),
                               e -> generationOptions
                                       .setGenerateAdapter(e.getStateChange() == ItemEvent.SELECTED)
                       }, initialiseAsDisabled);
           }
           else {
               optionsCheckBoxes = createListOfCheckBoxes(4,
                       new String[]{"Use Data classes", "Single file", "Nullable Fields", "Parcelable (Android)"},
                       new ItemListener[]{
                               e -> generationOptions
                                       .setUseDataClasses(e.getStateChange() == ItemEvent.SELECTED),
                               e -> generationOptions
                                       .setSingleFile(e.getStateChange() == ItemEvent.SELECTED),
                               e -> generationOptions
                                       .setNullableFields(e.getStateChange() == ItemEvent.SELECTED),
                               e -> generationOptions
                                       .setParcelableAndroid(e.getStateChange() == ItemEvent.SELECTED)
                       }, initialiseAsDisabled);
           }
       }

       return optionsCheckBoxes;
   }

   private ArrayList<JCheckBox> createListOfCheckBoxes(int num, String[] labels, ItemListener[] listeners, boolean initialiseAsDisabled)
   {
       assert num == labels.length;
       assert num == listeners.length;
       ArrayList<JCheckBox> checkBoxArrayList = new ArrayList<>();
       for (int i = 0; i < num; i++) {
           JCheckBox checkBox = new JCheckBox();
           checkBox.setEnabled(!initialiseAsDisabled);
           checkBox.setText(labels[i]);
           checkBox.addItemListener(listeners[i]);
           checkBoxArrayList.add(checkBox);
       }
       return checkBoxArrayList;
   }
}
