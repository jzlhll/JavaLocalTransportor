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
    private String mSendFile;

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
            sender.setInfoCallback((info)-> {
                System.out.println(info);
                Platform.runLater(()-> senderInfo.setText(info));
            });
            connector = sender;
            sender.init();
            sender.prepare(mSendFile);
        });

        receiveConnectBtn.setOnMouseClicked(e->{
            IConnect c = connector;
            if (c != null) {
                c.destroy();
            }

            String ip = receiveIpTextField.getText();
            String port = receivePortTextField.getText();
            if (ip != null && ip.length() > 0 && port != null && port.length() > 0) {
                int portInt;
                try{
                    portInt = Integer.parseInt(port);
                } catch (Exception e1) {
                    receiveInfo.setText("ip和port没有正确填写");
                    return;
                }

                Receiver receiver = new Receiver();
                receiver.setInfoCallback(s->{
                    Platform.runLater(()->{
                        receiveInfo.setText(s);
                    });
                });
                receiver.init();
                receiver.connect(ip, portInt, Consts.NET_COMING);
                connector = receiver;
            } else {
                receiveInfo.setText("ip和port没有正确填写");
            }
        });

        receiveRecFileBtn.setOnMouseClicked(e->{
            IConnect c = connector;
            if (c == null) {
                receiveInfo.setText("尚未连接，请点击连接按钮！");
            } else if (c instanceof Receiver r) {
                r.receiveFile(receiveFileTextField.getText());
            }
        });
    }

    public void setFile(String file) {
        System.out.println(file);
        mSendFile = file;

        senderInfo.setText("设置了文件：" + file);
    }
}