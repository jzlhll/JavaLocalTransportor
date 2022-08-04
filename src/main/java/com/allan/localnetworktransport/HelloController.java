package com.allan.localnetworktransport;

import com.allan.localnetworktransport.impl.Sender;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class HelloController {
    //====
    public VBox firstVbox;
    public Button asSenderBtn;
    public Button asReceiverBtn;

    //=====
    public VBox senderBox;
    public Button senderPrepareBtn;
    public Label senderInfo;

    //===
    public VBox receiveBox;
    public Button findServersBtn;
    public Label receiveInfo;
    public AnchorPane mainBox;

    //...main data....
    private IConnect connector;

    public void init() {


        asSenderBtn.setOnMouseClicked(v->{
            firstVbox.setVisible(false);
            senderBox.setVisible(true);
        });

        asReceiverBtn.setOnMouseClicked(v->{
            firstVbox.setVisible(false);
            receiveBox.setVisible(true);
        });

        senderPrepareBtn.setOnMouseClicked(e->{
            IConnect c = connector;
            if (c != null) {
                c.destroy();
            }

            Sender sender = new Sender();
            connector = sender;
            sender.init();
            sender.prepare(mFile);
        });
    }

    private String mFile;

    public void setFile(String file) {
        mFile = file;
    }
}