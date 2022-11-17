package com.hamed.postmantoretrofit2v2;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class OptionsDialog extends JDialog {
    private JPanel panel1;
    private JLabel generationOptionsLabel;
    private JComboBox<String> responseTypeComboBox;
    private JButton buttonCancel;
    private JButton buttonOk;
    private JCheckBox useRxJavaResponseTypesCheckBox;
    private String storedSelectedResponseType;
    private String initialSelectedResponseType;
    private Boolean isInitialUseRxJavaResponseTypesCheckBoxStateSelected;

    private final String[] retrofit2RawTypes = { "Call<T>", "Call<Response<T>>" };

    private final String[] rxJavaResponseTypes = { "Observable<T>", "Observable<Response<T>>", "Observable<Result<T>>",
                                                   "Flowable<T>", "Flowable<Response<T>>", "Flowable<Result<T>>", "Single<T>", "Single<Response<T>>",
                                                   "Single<Result<T>>", "Maybe<T>", "Maybe<Response<T>>", "Maybe<Result<T>>", "Completable" };

    public OptionsDialog(JDialog owner) {
        super(owner);

        System.out.println("OptionsDialog");
        storedSelectedResponseType = "";

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

        // Display the last response type selected by the user
        PluginState state = PluginService.getInstance().getState();
        if (!state.getResponseType().isEmpty()) {
            System.out.println("state response type: " + state.getResponseType());
            if (!useRxJavaResponseTypesCheckBox.isSelected() && new ArrayList<>(List.of(rxJavaResponseTypes)).contains(state.getResponseType()))
                useRxJavaResponseTypesCheckBox.doClick();

            responseTypeComboBox.setSelectedItem(state.getResponseType());
            storedSelectedResponseType = state.getResponseType();
        }

        setContentPane(panel1);
        buttonOk.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

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
