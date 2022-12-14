package com.hamed.postmantoretrofit2v2.forms;

import com.hamed.postmantoretrofit2v2.Constants;
import com.hamed.postmantoretrofit2v2.datacontext.DataContextWrapper;
import com.hamed.postmantoretrofit2v2.messaging.Message;
import com.hamed.postmantoretrofit2v2.messaging.MessageBroker;
import com.hamed.postmantoretrofit2v2.messaging.MessageSubscriber;
import com.hamed.postmantoretrofit2v2.messaging.NewClassInfoAddedMessage;
import com.hamed.postmantoretrofit2v2.pluginstate.Language;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginService;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginState;
import com.hamed.postmantoretrofit2v2.pluginstate.ReturnTypeRadioButton;
import com.hamed.postmantoretrofit2v2.utils.ClassInfo;
import com.hamed.postmantoretrofit2v2.utils.ProjectUtils;
import com.hamed.postmantoretrofit2v2.utils.Utils;
import com.intellij.ide.DataManager;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.PluginId;
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

    private boolean isCanceled;
    private boolean keepDefault;

    public ClassPickerDialog(JDialog parentDialog, Project project, Editor editor, String retrofitAnnotatedMethods) {
        super(parentDialog);
        this.mProject = project;
        this.mEditor = editor;
        this.mRetrofitAnnotatedMethod = retrofitAnnotatedMethods;
        this.isCanceled = false;
        this.keepDefault = false;
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;

        MessageBroker.getInstance().addSubscriber(this);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        classListComboBox.addActionListener(this::onClassListComboBoxChanged);
        changeReturnTypeComboBox.addActionListener(this::onReturnTypeComboBoxChanged);
        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        buttonKeepDefault.addActionListener(e -> onKeepDefault());
        generateANewClassButton.addActionListener(e -> onGenerateNewClass());
        makeReturnTypeAListCheckBox.addActionListener(this::onMakeReturnTypeAListCheckBoxStateChanged);

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



    private void onReturnTypeComboBoxChanged(ActionEvent e) {
        if (e.getActionCommand().equals(changeReturnTypeComboBox.getActionCommand())) {

            if (Constants.listOfReturnTypesWithoutClass.contains((String) changeReturnTypeComboBox.getSelectedItem())) {
                classListComboBox.setEnabled(false);
                makeReturnTypeAListCheckBox.setEnabled(false);
            }
            else {
                classListComboBox.setEnabled(true);
                makeReturnTypeAListCheckBox.setEnabled(true);
            }

            if (classListComboBox.getSelectedItem() != null) {
                outputOverviewTextArea.setText(replaceReturnTypeInRetrofitAnnotatedMethod((ClassInfo) classListComboBox.getSelectedItem(), makeReturnTypeAListCheckBox.isSelected()));
            }

            outputOverviewTextArea.setCaretPosition(0);
        }
    }

    private void onMakeReturnTypeAListCheckBoxStateChanged(ActionEvent e) {

        if (e.getID() == ActionEvent.ACTION_PERFORMED)
        {
            if (makeReturnTypeAListCheckBox.isSelected()) {
                outputOverviewTextArea.setText(replaceReturnTypeInRetrofitAnnotatedMethod((ClassInfo) Objects.requireNonNull(classListComboBox.getSelectedItem()), true));
            }
            else
            {
                outputOverviewTextArea.setText(replaceReturnTypeInRetrofitAnnotatedMethod((ClassInfo) Objects.requireNonNull(classListComboBox.getSelectedItem()), false));
            }
        }
    }

    private void onClassListComboBoxChanged(ActionEvent e) {

        if (e.getActionCommand().equals(classListComboBox.getActionCommand())) {
            ClassInfo className = (ClassInfo) classListComboBox.getSelectedItem();
            if (className != null) {
                outputOverviewTextArea.setText(replaceReturnTypeInRetrofitAnnotatedMethod((ClassInfo) classListComboBox.getSelectedItem(), makeReturnTypeAListCheckBox.isSelected()));
            }

            outputOverviewTextArea.setCaretPosition(0);
        }
    }

    private void onOK() {
        dispose();
    }

    private void onKeepDefault() {
        // Keep the default return type and class
        keepDefault = true;
        dispose();
    }

    private void onCancel() {
        keepDefault = true;
        isCanceled = true;
        dispose();
    }

    public boolean isCanceled()
    {
        return isCanceled;
    }

    @Override
    public void dispose() {
        super.dispose();
        MessageBroker.getInstance().removeSubscriber(this);
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

        String returnTypeClass = returnTypeClassInfo.getClassName();

        if (makeReturnTypeAList)
            returnTypeClass = "List<" + returnTypeClass + ">";

        String returnType = (String) changeReturnTypeComboBox.getSelectedItem();
        if (returnType == null)
            returnType = Objects.requireNonNull(PluginService.getInstance(mProject).getState()).getReturnType();

        returnType = returnType.replaceAll("T", returnTypeClass);

        return mRetrofitAnnotatedMethod.replaceAll("##.*##", returnType);
    }

    private void onGenerateNewClass()
    {
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;

        if (PluginManagerCore.isDisabled(PluginId.getId("com.robohorse.robopojogenerator")))
        {
            System.out.println("RoboPojoGenerator plugin is disabled");
            // Todo: Prompt the user to enable the plugin
            boolean doEnable = Messages.showYesNoDialog(
                    "The RoboPojoGenerator plugin used to generate the classes is disabled, " +
                            "would you like to enable it? Requires IDE restart.",
                    "Enable RoboPojoGenerator Plugin?",
                    "Enable",
                    "Keep Disabled",
                    Messages.getQuestionIcon()
            ) == Messages.YES;

            if (doEnable) {
                PluginManager.getInstance().enablePlugin(PluginId.getId("com.robohorse.robopojogenerator"));
                System.out.println("Enabled RoboPojoGenerator plugin");
                ProjectUtils.restartIde();
            }
            else
                return;
        }
        else if (!PluginManagerCore.isPluginInstalled(PluginId.getId("com.robohorse.robopojogenerator")))
        {
            System.out.println("Robo Pojo Generator plugin is not installed");
            Messages.showMessageDialog(mProject, "Error RoboPojoGenerator plugin is not installed, please install it from the market place", "Dependency Plugin Missing", Messages.getWarningIcon());
            return;
        }

        VirtualFile returnTypeClassesDirVirtualFile = LocalFileSystem.getInstance().findFileByPath(state.getReturnTypeClassesDirectory());
        assert returnTypeClassesDirVirtualFile != null;
        PsiDirectory psiDirectory = PsiManager.getInstance(mProject).findDirectory(returnTypeClassesDirVirtualFile);

        Map<String, Object> map = new HashMap<>();
        map.put(CommonDataKeys.NAVIGATABLE.getName(), psiDirectory);
        map.put(LangDataKeys.VIRTUAL_FILE.getName(), returnTypeClassesDirVirtualFile);

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

    public String getModifiedRetrofitAnnotatedMethod()
    {
        // If the dialog was not cancelled, the combo-box will have a valid value
        if (keepDefault)
            return mRetrofitAnnotatedMethod;
        else
            return replaceReturnTypeInRetrofitAnnotatedMethod((ClassInfo) Objects.requireNonNull(classListComboBox.getSelectedItem()), makeReturnTypeAListCheckBox.isSelected());
    }

    private void createUIComponents() {
        Objects.requireNonNull(mProject, "mProject must be initiated before createUIComponents");
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;

        changeReturnTypeComboBox = new ComboBox<>();
        String[] returnTypeList;
        if (state.getReturnTypeRadioButton() == ReturnTypeRadioButton.BUTTON_RETROFIT_RAW_TYPES) {
            returnTypeList = Constants.retrofit2RawTypes;
        }
        else if (state.getReturnTypeRadioButton() == ReturnTypeRadioButton.BUTTON_RXJAVA_TYPES) {
            returnTypeList = Constants.rxJavaReturnTypes;
        }
        else {
            returnTypeList = Constants.retrofit2RawTypesKotlinCoroutines;
        }

        for (String returnType: returnTypeList) {
            changeReturnTypeComboBox.addItem(returnType);
        }

        changeReturnTypeComboBox.setSelectedItem(state.getReturnType());

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
        for (ClassInfo classFileInfo: state.getReturnTypeClassInfoList()) {
            classListComboBox.addItem(classFileInfo);
        }
    }
}
