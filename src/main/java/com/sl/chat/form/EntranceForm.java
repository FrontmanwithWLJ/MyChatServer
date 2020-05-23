package com.sl.chat.form;

import javax.swing.*;
import com.sl.chat.form.EntranceForm.*;

public class EntranceForm {
    private JPanel jPanel;
    private JTextField runLog;
    private JButton ChooseFile;
    private JTextField password;
    private JTextField port;
    private JButton run;
    private JButton test;
    private JLabel portMsg;

    public static void main(String[] args) {
        JFrame frame = new JFrame("EntranceForm");
        frame.setContentPane(new EntranceForm().jPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
