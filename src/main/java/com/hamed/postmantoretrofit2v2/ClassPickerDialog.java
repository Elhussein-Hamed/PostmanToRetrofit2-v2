package com.hamed.postmantoretrofit2v2;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ClassPickerDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField inputOveriewTextField;
    private JComboBox comboBox1;
    private JTextField outputOverviewTextField;
    private final ArrayList<String> classesList;

    public ClassPickerDialog(String result, String method, ArrayList<String> classesList) {
        this.classesList = classesList;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        System.out.println("CheckPickerDialog");

        inputOveriewTextField.setText(result);
        inputOveriewTextField.setCaretPosition(0);
        if (comboBox1.getSelectedItem() != null)
            outputOverviewTextField.setText(result.replace(method + "Response", (String)comboBox1.getSelectedItem()));
        outputOverviewTextField.setCaretPosition(0);

        comboBox1.addActionListener(e ->
        {
            if (e.getActionCommand().equals(comboBox1.getActionCommand()))
            {
                String className = (String) comboBox1.getSelectedItem();
                if (className != null)
                    outputOverviewTextField.setText(result.replace(method + "Response", className));

                outputOverviewTextField.setCaretPosition(0);
            }
        });

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

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
        comboBox1.setSelectedItem(null);
        dispose();
    }

    public String getClassName()
    {
        return (String) comboBox1.getSelectedItem();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        comboBox1 = new ComboBox<String>();

        // Get the list of classes in the project
        for (String cls: classesList)
            comboBox1.addItem(cls.replace(".java", ""));
    }
}
