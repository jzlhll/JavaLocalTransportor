package com.allan.localnetworktransport;

import com.allan.localnetworktransport.arch.IConnect;
import com.allan.localnetworktransport.bean.Consts;
import com.allan.localnetworktransport.impl.Receiver;
import com.allan.localnetworktransport.impl.Sender;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    public Button receiveConnectBtn;
    public Label receiveInfo;
    public AnchorPane mainBox;
    public TextField receiveIpTextField;
    public TextField receivePortTextField;
    public TextField receiveFileTextField;
    public Button receiveRecFileBtn;

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
            sender.setInfoCallback((info)-> Platform.runLater(()-> senderInfo.setText(info)));
            connector = sender;
            sender.init();
            sender.prepare(mFile);
        });

        receiveConnectBtn.setOnMouseClicked(e->{
            IConnect c = connector;
            if (c != null) {
                c.destroy();
            }

            String ip = receiveIpTextField.getText();
            String port = receivePortTextField.getText();
            if (ip != null && ip.length() > 0 && port != null && port.length() > 0) {
                int portInt = Integer.parseInt(port);
                Receiver receiver = new Receiver();
                receiver.init();
                receiver.connect(ip, portInt, Consts.NET_COMING);
                connector = receiver;
            }
        });

        receiveRecFileBtn.setOnMouseClicked(e->{
            IConnect c = connector;
            if (c instanceof Receiver r) {
                r.receiveFile(receiveFileTextField.getText());
            }
        });
    }

    private String mFile;

    public void setFile(String file) {
        mFile = file;
    }
}