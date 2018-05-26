package com.test.excel;

import com.test.excel.biz.ConfigExcelProcess;
import com.test.excel.biz.ExecutorCsvProcess;
import com.test.excel.biz.TransforExcelProcess;
import com.test.excel.config.BeanConfig;
import com.test.excel.domain.ItemMapDO;
import com.test.excel.domain.RequestExc;
import com.test.excel.util.ItemGHMapUtils;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.io.*;


/**
 * @author banmo
 * @create 2018-05-09
 **/
public class MainStageController extends AbstractJavaFxApplicationSupport {
    private static final Logger logger = LoggerFactory.getLogger(MainStageController.class);
    public static AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(BeanConfig.class);

    public static void main(String[] args) {
        launchApp(MainStageController.class,MainStageView.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        doSomeThing(primaryStage, root);

        primaryStage.setTitle("nuonuo专用Excel转换器");
        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/main.css").toString());
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(getClass().getResource("/icon.png").toString(), 100, 150, false, false));
        primaryStage.show();
    }

    public void doSomeThing(Stage primaryStage, Parent root) {

        Button upbutton = (Button)root.lookup("#uploadExecel");
        Label uploadProcess = (Label)root.lookup("#uploadProcess");
        TextField uploadInfo = (TextField)root.lookup("#uploadInfo");

        TransforExcelProcess transforExcelProcess = (TransforExcelProcess) ctx.getBeanFactory().getBean("transforExcelProcess");
        ConfigExcelProcess configExcelProcess = (ConfigExcelProcess) ctx.getBeanFactory().getBean("configExcelProcess");
        ExecutorCsvProcess executorCsvProcess = (ExecutorCsvProcess) ctx.getBeanFactory().getBean("executorCsvProcess");

        RequestExc transforRequest = transforExcelProcess.buildRequest();
        RequestExc configRequest = configExcelProcess.buildRequest();
        RequestExc executorCsvRequest = executorCsvProcess.buildRequest();

        upbutton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
                uploadProcess.setText("上传中...");
                transforExcelProcess.setLogText(new StringBuilder(""));
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xls, *.xlsx, *.csv)", "*.xls", "*.xlsx", "*.csv");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(primaryStage);

                if (null != file) {
                    try {
                        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
                        transforRequest.setFis(fis);
                        transforRequest.setFileName(file.getName());
                        transforExcelProcess.execute(transforRequest);
                        uploadProcess.setText(transforExcelProcess.getLogText().append("转化完成，请您开心的下载吧~~~啦啦啦").toString());
                    } catch (Exception e) {
                        uploadProcess.setText(transforExcelProcess.getLogText().append(ExceptionUtils.getStackTrace(e)).toString());
                        logger.error(e.getMessage() + ExceptionUtils.getStackTrace(e));
                    }
                }
                System.out.println(file);
            }
        });

        Button dwbutton = (Button)root.lookup("#downloadExecel");
        dwbutton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
                uploadProcess.setText("下载中...");
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("颤抖吧！！！");
                File file = fileChooser.showSaveDialog(primaryStage);
                transforRequest.setDownloadDir(file.getParent());
                transforExcelProcess.download(transforRequest);
                uploadProcess.setText("下载完成，开心吧！！！！");
                System.out.println(file);

            }
        });

        Button uploadMapButton = (Button)root.lookup("#uploadMapExecel");
        uploadMapButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
                uploadProcess.setText("上传中...");
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xls, *.xlsx)", "*.xls", "*.xlsx");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(primaryStage);

                if (null != file) {
                    try {
                        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
                        configRequest.setFis(fis);
                        configRequest.setFileName(file.getName());
                        configExcelProcess.execute(configRequest);
                        uploadProcess.setText("上传配置信息成功，万事俱备只欠东风了，加油+++");
                    } catch (Exception e) {
                        logger.error(e.getMessage() + ExceptionUtils.getStackTrace(e));
                    }
                }
                System.out.println(file);

            }
        });

        Button executorCSV = (Button)root.lookup("#executorCSV");
        executorCSV.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
                uploadProcess.setText("上传中...");
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.csv)", "*.csv");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(primaryStage);

                if (null != file) {
                    try {
                        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
                        executorCsvRequest.setFis(fis);
                        executorCsvRequest.setFileName(file.getName());
                        executorCsvRequest.setDownloadDir(file.getParent());
                        executorCsvProcess.execute(executorCsvRequest);
                        uploadProcess.setText("处理成功，请上传配置文件+++");
                    } catch (Exception e) {
                        logger.error(e.getMessage() + ExceptionUtils.getStackTrace(e));
                    }
                }
                System.out.println(file);

            }
        });
    }
}
