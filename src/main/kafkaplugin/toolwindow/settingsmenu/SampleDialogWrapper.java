package kafkaplugin.toolwindow.settingsmenu;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class SampleDialogWrapper extends DialogWrapper {

    private JLabel bootstrapLabel = new JLabel("Bootstrap address");
    private JTextField boostrapField = new JTextField(20);

    public SampleDialogWrapper() {
        super(true);
        init();
        setTitle("Kafka Broker Setup");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        bootstrapLabel.setPreferredSize(new Dimension(100, 100));
        dialogPanel.add(bootstrapLabel, BorderLayout.CENTER);
        boostrapField.setPreferredSize(new Dimension(100, 100));
        dialogPanel.add(boostrapField, BorderLayout.CENTER);

        return dialogPanel;
    }

}