package com.hamed.postmantoretrofit2v2.forms;

import com.hamed.postmantoretrofit2v2.PluginService;
import com.hamed.postmantoretrofit2v2.PluginState;
import com.hamed.postmantoretrofit2v2.Utils;
import com.hamed.postmantoretrofit2v2.datacontext.DataContextWrapper;
import com.hamed.postmantoretrofit2v2.eventlisteners.MyPsiTreeChangeListener;
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
import com.robohorse.robopojogenerator.action.GeneratePOJOAction;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClassPickerDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField inputOveriewTextField;
    private JComboBox classListComboBox;
    private JTextField outputOverviewTextField;
    private JButton generateANewClassButton;
    private final Project mProject;
    private final Editor mEditor;

    public ClassPickerDialog(Project project, Editor editor, String result, String method) {
        mProject = project;
        mEditor = editor;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        System.out.println("CheckPickerDialog");

        inputOveriewTextField.setText(result);
        inputOveriewTextField.setCaretPosition(0);
        if (classListComboBox.getSelectedItem() != null)
            outputOverviewTextField.setText(result.replace(method + "Response", (String) classListComboBox.getSelectedItem()));
        outputOverviewTextField.setCaretPosition(0);

        classListComboBox.addActionListener(e ->
        {
            if (e.getActionCommand().equals(classListComboBox.getActionCommand()))
            {
                String className = (String) classListComboBox.getSelectedItem();
                if (className != null)
                    outputOverviewTextField.setText(result.replace(method + "Response", className));

                outputOverviewTextField.setCaretPosition(0);
            }
        });

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        generateANewClassButton.addActionListener(e -> onGenerateNewClass());

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
        // add your code here
        System.out.println("CheckPickerDialog: onOK");
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        System.out.println("CheckPickerDialog: onCancel");
        classListComboBox.setSelectedItem(null);
        dispose();
    }

    private void onGenerateNewClass()
    {
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
                Utils.restartIde();
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

        PluginState state = PluginService.getInstance(mProject).getState();
        VirtualFile responseTypeClassesDirVirtualfile = LocalFileSystem.getInstance().findFileByPath(state.getResponseTypeClassesDirectory());
        PsiDirectory psiDirectory = PsiManager.getInstance(mProject).findDirectory(responseTypeClassesDirVirtualfile);

        Map<String, Object> map = new HashMap<>();
        map.put(CommonDataKeys.NAVIGATABLE.getName(), psiDirectory);
        map.put(LangDataKeys.VIRTUAL_FILE.getName(), responseTypeClassesDirVirtualfile);

        DataContext dataContext = DataContextWrapper.getContext(map, DataManager.getInstance().getDataContext(mEditor.getComponent()));

        // Prepare a listener to handle any new files that might be added by RoboPojoGenerator plugin
        PsiManager.getInstance(mProject).addPsiTreeChangeListener(new MyPsiTreeChangeListener(file -> {
            if (file.getName().contains(".java"))
            {
                System.out.println("MyPsiTreeChangeListener in ClassPickerDialog");
                String filename = file.getName().replace(".java", "");
                if (file.getParent().equals(psiDirectory)) {
                    // Use setSelectedItem behaviour to check if the filename already exists in the combo
                    // box list. setSelectedItem doesn't do anything if filename does not exist.
                    classListComboBox.setSelectedItem(filename);

                    // Check if setSelectedItem change the selected item
                    if (classListComboBox.getSelectedItem() == null || !classListComboBox.getSelectedItem().equals(filename)) {
                        classListComboBox.addItem(filename);
                        classListComboBox.setSelectedItem(filename);
                        System.out.println("Added file: " + filename + " to Combo Box list");
                    }
                }
            }
        }));

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

    public String getClassName()
    {
        return (String) classListComboBox.getSelectedItem();
    }

    private void createUIComponents() {
        Objects.requireNonNull(mProject, "mProject must be initiated before createUIComponents");
        // TODO: place custom component creation code here
        classListComboBox = new ComboBox<String>();

        PluginState state = PluginService.getInstance(mProject).getState();
        // Get the list of classes in the project
        for (String cls: state.getResponseTypeClassesList())
            classListComboBox.addItem(cls);
    }
}
