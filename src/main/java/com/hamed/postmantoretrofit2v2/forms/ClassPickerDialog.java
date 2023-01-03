package com.hamed.postmantoretrofit2v2.forms;

import com.hamed.postmantoretrofit2v2.Constants;
import com.hamed.postmantoretrofit2v2.datacontext.DataContextWrapper;
import com.hamed.postmantoretrofit2v2.forms.listeners.ClassPickerDialogReturnedData;
import com.hamed.postmantoretrofit2v2.forms.listeners.DialogClosedListener;
import com.hamed.postmantoretrofit2v2.messaging.Message;
import com.hamed.postmantoretrofit2v2.messaging.MessageBroker;
import com.hamed.postmantoretrofit2v2.messaging.MessageSubscriber;
import com.hamed.postmantoretrofit2v2.messaging.NewClassInfoAddedMessage;
import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.enums.Language;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginService;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginState;
import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.enums.ReturnTypeRadioButton;
import com.hamed.postmantoretrofit2v2.utils.ClassInfo;
import com.hamed.postmantoretrofit2v2.utils.DependencyPluginHelper;
import com.hamed.postmantoretrofit2v2.utils.Utils;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.robohorse.robopojogenerator.action.GeneratePOJOAction;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClassPickerDialog extends JDialog implements MessageSubscriber {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox<ClassInfo> classListComboBox;
    private JButton generateANewClassButton;
    @SuppressWarnings("unused")
    private JScrollPane inputOverviewAreaScrollView;
    @SuppressWarnings("unused")
    private JScrollPane outputOverviewAreaScrollView;
    private JCheckBox makeReturnTypeAListCheckBox;
    private JComboBox<String> changeReturnTypeComboBox;
    private JButton buttonKeepDefault;
    private RSyntaxTextArea inputOverviewTextArea;
    private RSyntaxTextArea outputOverviewTextArea;
    private final Project mProject;
    private final Editor mEditor;
    private final String mRetrofitAnnotatedMethod;
    private DialogClosedListener dialogClosedListener;

    public ClassPickerDialog(JDialog parentDialog, Project project, Editor editor, String retrofitAnnotatedMethods) {
        super(parentDialog);
        this.mProject = project;
        this.mEditor = editor;
        this.mRetrofitAnnotatedMethod = retrofitAnnotatedMethods;
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;

        MessageBroker.getInstance().addSubscriber(this);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        setupActionListeners();

        // Disable the class list check box and change to list button if the return type doesn't include a class
        if (Constants.listOfReturnTypesWithoutClass.contains(state.getReturnType())) {
            classListComboBox.setEnabled(false);
            makeReturnTypeAListCheckBox.setEnabled(false);
        }

        inputOverviewTextArea.setText(Utils.removeHashesAroundReturnType(mRetrofitAnnotatedMethod));
        inputOverviewTextArea.setCaretPosition(0);
        if (classListComboBox.getSelectedItem() != null) {
            outputOverviewTextArea.setText(replaceReturnTypeInRetrofitAnnotatedMethod((ClassInfo) classListComboBox.getSelectedItem(), false));
            outputOverviewTextArea.setCaretPosition(0);
        }
        else
            outputOverviewTextArea.setText(Utils.removeHashesAroundReturnType(mRetrofitAnnotatedMethod));
    }

    private void setupActionListeners() {

        classListComboBox.addActionListener(e -> {
            if (classListComboBox.getSelectedItem() != null) {
                outputOverviewTextArea.setText(replaceReturnTypeInRetrofitAnnotatedMethod((ClassInfo) classListComboBox.getSelectedItem(), makeReturnTypeAListCheckBox.isSelected()));
                outputOverviewTextArea.setCaretPosition(0);
            }
        });

        changeReturnTypeComboBox.addActionListener(e -> {
            if (Constants.listOfReturnTypesWithoutClass.contains((String) changeReturnTypeComboBox.getSelectedItem())) {
                classListComboBox.setEnabled(false);
                makeReturnTypeAListCheckBox.setEnabled(false);
            }
            else {
                classListComboBox.setEnabled(true);
                makeReturnTypeAListCheckBox.setEnabled(true);
            }

            outputOverviewTextArea.setText(replaceReturnTypeInRetrofitAnnotatedMethod((ClassInfo) classListComboBox.getSelectedItem(), makeReturnTypeAListCheckBox.isSelected()));
            outputOverviewTextArea.setCaretPosition(0);
        });

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        buttonKeepDefault.addActionListener(e -> onKeepDefault());

        generateANewClassButton.addActionListener(e -> onGenerateNewClass());
        makeReturnTypeAListCheckBox.addActionListener(e -> {
            if (e.getID() == ActionEvent.ACTION_PERFORMED)
            {
                ClassInfo classInfoToDisplay = null;
                if (classListComboBox.getSelectedItem() != null)
                    classInfoToDisplay = (ClassInfo) classListComboBox.getSelectedItem();

                if (makeReturnTypeAListCheckBox.isSelected())
                    outputOverviewTextArea.setText(replaceReturnTypeInRetrofitAnnotatedMethod(classInfoToDisplay, true));
                else
                    outputOverviewTextArea.setText(replaceReturnTypeInRetrofitAnnotatedMethod(classInfoToDisplay, false));
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { onCancel(); }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        dispose();
        dialogClosedListener.onUserConfirm(new ClassPickerDialogReturnedData(replaceReturnTypeInRetrofitAnnotatedMethod((ClassInfo) classListComboBox.getSelectedItem(), makeReturnTypeAListCheckBox.isSelected())));
    }

    private void onKeepDefault() {
        dispose();
        dialogClosedListener.onUserConfirm(new ClassPickerDialogReturnedData(mRetrofitAnnotatedMethod));
    }

    private void onCancel() {
        dispose();
        dialogClosedListener.onCancelled();
    }

    private void onGenerateNewClass() {

        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;

        if (!DependencyPluginHelper.isPluginUsable(mProject, "RoboPojoGenerator", "com.robohorse.robopojogenerator"))
            return;

        // RoboPojoGenerator expects the source directory to create the class in. The directory as
        // a virtual file and a PSI directory needs to be passed in the data context.
        VirtualFile returnTypeClassesDirVirtualFile = LocalFileSystem.getInstance().findFileByPath(state.getReturnTypeClassesDirectory());
        assert returnTypeClassesDirVirtualFile != null;
        PsiDirectory psiDirectory = PsiManager.getInstance(mProject).findDirectory(returnTypeClassesDirVirtualFile);

        // Fill in the DataKeys in the data context which are required in the action event passed to
        // GeneratePOJOAction.
        // See: https://github.com/robohorse/RoboPOJOGenerator/blob/c54bf938fc0b454cc87bf03173a2a6167f932e28/core/src/main/kotlin/com/robohorse/robopojogenerator/delegates/EnvironmentDelegate.kt#L24
        // and  https://github.com/robohorse/RoboPOJOGenerator/blob/c54bf938fc0b454cc87bf03173a2a6167f932e28/core/src/main/kotlin/com/robohorse/robopojogenerator/delegates/EnvironmentDelegate.kt#L43
        Map<String, Object> map = new HashMap<>();
        map.put(LangDataKeys.VIRTUAL_FILE.getName(), returnTypeClassesDirVirtualFile);
        map.put(CommonDataKeys.NAVIGATABLE.getName(), psiDirectory);

        DataContext dataContext = DataContextWrapper.getContext(map, DataManager.getInstance().getDataContext(mEditor.getComponent()));

        try {
            new GeneratePOJOAction().actionPerformed(
                    new AnActionEvent(null, dataContext,
                            ActionPlaces.UNKNOWN, new Presentation(),
                            ActionManager.getInstance(), 0)
            );
        } catch (NoClassDefFoundError ex) {
            // Restart the IDE
            Messages.showMessageDialog(mProject, "Error calling RoboPojoGenerator plugin, please restart the IDE", "Plugin Requires Restart", Messages.getWarningIcon());
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        MessageBroker.getInstance().removeSubscriber(this);
    }

    public void setOnDialogClosedListener(DialogClosedListener dialogClosedListener) {
        this.dialogClosedListener = dialogClosedListener;
    }

    @Override
    public void onMessageReceived(Message message) {

        System.out.println("ClassPickerDialog: onMessageReceived");

        if (message instanceof NewClassInfoAddedMessage) {
            ClassInfo classInfo = (ClassInfo) message.getContent();

            // Use setSelectedItem behaviour to check if the filename already exists in the combo
            // box list. setSelectedItem doesn't do anything if filename does not exist.
            classListComboBox.setSelectedItem(classInfo);

            // Check if setSelectedItem change the selected item
            if (classListComboBox.getSelectedItem() == null || !classListComboBox.getSelectedItem().equals(classInfo)) {
                classListComboBox.addItem(classInfo);
                classListComboBox.setSelectedItem(classInfo);
                System.out.println("Added file: " + classInfo.getClassName() + " to Combo Box list");
            }
        }
    }

    private String replaceReturnTypeInRetrofitAnnotatedMethod(ClassInfo returnTypeClassInfo, boolean makeReturnTypeAList) {

        // Modify the retrofit annotated method declaration
        // 1- As is => replace the return type with the selected class
        // 2- As a list => replace the return type with a list of the selected class
        // 3- selected class is null and as is => replace the return type with the default type
        // 4- selected class is null and as a list => replace the return type with a list of the default return type

        String returnTypeFormat = (String) changeReturnTypeComboBox.getSelectedItem();
        assert returnTypeFormat != null;

        String returnTypeClass;
        if (returnTypeClassInfo != null) {
            // 1- As is => replace the return type with the selected class
            returnTypeClass = returnTypeClassInfo.getClassName();
        }
        else {

            // Get default return type from mRetrofitAnnotatedMethod
            // Use replace all as a shortcut to only get the return type class name
            returnTypeClass = String.join("", mRetrofitAnnotatedMethod.split("\n")).replaceAll(".*##(?:\\w+<)*?(\\w+Response).*##.*", "$1");
            System.out.println("Return type class: " + returnTypeClass);
        }

        if (makeReturnTypeAList)
            returnTypeClass = "List<" + returnTypeClass + ">";

        String returnType = returnTypeFormat.replaceAll("T", returnTypeClass);
        return mRetrofitAnnotatedMethod.replaceAll("##.*##", returnType);
    }

    private void createUIComponents() {
        Objects.requireNonNull(mProject, "mProject must be initiated before createUIComponents");
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;

        // Initialise the combo-box to change the return type
        changeReturnTypeComboBox = new ComboBox<>();
        String[] returnTypeList;
        if (state.getReturnTypeRadioButton() == ReturnTypeRadioButton.BUTTON_RETROFIT_RAW_TYPES)
            returnTypeList = Constants.retrofit2RawTypes;
        else if (state.getReturnTypeRadioButton() == ReturnTypeRadioButton.BUTTON_RXJAVA_TYPES)
            returnTypeList = Constants.rxJavaReturnTypes;
        else
            returnTypeList = Constants.retrofit2RawTypesKotlinCoroutines;

        for (String returnType: returnTypeList)
            changeReturnTypeComboBox.addItem(returnType);

        changeReturnTypeComboBox.setSelectedItem(state.getReturnType());

        // Initialise the input and the output text areas
        String syntaxStyle = SyntaxConstants.SYNTAX_STYLE_JAVA;
        if (state.getLanguage() == Language.KOTLIN)
            syntaxStyle = SyntaxConstants.SYNTAX_STYLE_KOTLIN;

        inputOverviewTextArea = new RSyntaxTextArea();
        inputOverviewTextArea.setSyntaxEditingStyle(syntaxStyle);
        inputOverviewTextArea.setEditable(false);
        inputOverviewTextArea.setEnabled(false);
        inputOverviewTextArea.setMargin(JBUI.insets(8));
        inputOverviewTextArea.setCodeFoldingEnabled(true);
        inputOverviewAreaScrollView = new JBScrollPane(inputOverviewTextArea);

        outputOverviewTextArea = new RSyntaxTextArea();
        outputOverviewTextArea.setSyntaxEditingStyle(syntaxStyle);
        outputOverviewTextArea.setEditable(false);
        outputOverviewTextArea.setEnabled(false);
        outputOverviewTextArea.setMargin(JBUI.insets(8));
        outputOverviewTextArea.setCodeFoldingEnabled(true);
        outputOverviewAreaScrollView = new JBScrollPane(outputOverviewTextArea);

        // Get the list of classes in the project
        classListComboBox = new ComboBox<>();
        for (ClassInfo classFileInfo: state.getReturnTypeClassInfoList())
            classListComboBox.addItem(classFileInfo);
    }
}
