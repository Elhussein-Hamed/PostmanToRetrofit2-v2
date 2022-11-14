package com.hamed.postmantoretrofit2v2;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class OptionsDialog extends JDialog {
    private JPanel panel1;
    private JLabel generationOptionsLabel;
    private JComboBox<String> rxJavaResponseTypeComboBox;
    private JButton buttonCancel;
    private JButton buttonOk;
    private String storedSelectedResponseType;

    private String initialSelectedResponseType;

    public OptionsDialog(JDialog owner) {
        super(owner);

        System.out.println("OptionsDialog");
        storedSelectedResponseType = "";

        // Display the last response type selected by the user
        PluginState state = PluginService.getInstance().getState();
        if (!state.getRxJavaResponseType().isEmpty()) {
            rxJavaResponseTypeComboBox.setSelectedItem(state.getRxJavaResponseType());
            storedSelectedResponseType = state.getRxJavaResponseType();
        }

        setContentPane(panel1);
        buttonOk.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        rxJavaResponseTypeComboBox.addActionListener(e ->
        {
            if (e.getActionCommand().equals(rxJavaResponseTypeComboBox.getActionCommand()))
            {
                if (storedSelectedResponseType.equals(getRxJavaResponseTypeComboBoxItem()))
                {
                    // Nothing changed, so do nothing
                }
                else
                {
                    storedSelectedResponseType = getRxJavaResponseTypeComboBoxItem();

                    // Save the response type
                    state.setRxJavaResponseType(storedSelectedResponseType);
                }

                rxJavaResponseTypeComboBox.hidePopup();
            }
        });

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
        if (b)
            initialSelectedResponseType = getRxJavaResponseTypeComboBoxItem();
        super.setVisible(b);
    }

    private void onOK() {
        // Do nothing as the json dialog has access to the necessary functions from here.
        setVisible(false);
    }

    private void onCancel() {

        System.out.println("onCancel");

        // Set back the initial values
        rxJavaResponseTypeComboBox.setSelectedItem(initialSelectedResponseType);

        // add your code here if necessary
        setVisible(false);
    }

    private String getRxJavaResponseTypeComboBoxItem ()
    {
        return (String) rxJavaResponseTypeComboBox.getSelectedItem();
    }

    public String getRxJavaResponseFormat()
    {
        return getRxJavaResponseTypeComboBoxItem();
    }
}
