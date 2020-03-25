package sample;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;

public class Controller implements Initializable {

    public ChoiceBox<String> shapeChoiceBox;
    public ChoiceBox<Integer> size1ChoiceBox;
    public ChoiceBox<Integer> size2ChoiceBox;
    public ColorPicker colorPicker;
    public CheckBox stokeCheckBox;
    public Canvas canvas;
    public CheckBox pointCheckBox;
    public Label nShapesLabel;
    public TextField shapeNumberToDelete;

    // Logica e urmt: formele sunt mereu salvate in ordine si la stergerea unei forme aceasta nu se sterge din lista ci doar nu o sa mai fie afisata la ecran. Astfel se pastreaza si suprapunerea formelor
    Stack<Integer> deletedShapeIndex = new Stack<>();
    List<MyShape> shapeList = new ArrayList<>();

    // Pentru BackUp
    //Stack<Image> canvasBackUp = new Stack<>(); -- o implementare mai veche de retine backUp la canvas printr-o stiva de imagini
    String nShapeBackUp;
    List<MyShape> backUpShapeList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /// Initializari
        shapeChoiceBox.getItems().addAll("Triangle", "Rectangle", "Pentagon", "Hexagon", "RoundRect", "Circle", "Oval");
        shapeChoiceBox.setValue("Rectangle");
        size1ChoiceBox.getItems().addAll(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200);
        size1ChoiceBox.setValue(50);
        size2ChoiceBox.getItems().addAll(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200);
        size2ChoiceBox.setValue(50);
        colorPicker.setValue(Color.BLACK);
        nShapesLabel.setText("0");

        GraphicsContext gc = canvas.getGraphicsContext2D();

        canvas.setOnMouseClicked(e -> {
            String shape = shapeChoiceBox.getValue();
            Color color = colorPicker.getValue();
            double size1 = size1ChoiceBox.getValue();
            double size2 = size2ChoiceBox.getValue();
            double x = e.getX() - size1 / 2;
            double y = e.getY() - size2 / 2;
            boolean stroke = stokeCheckBox.isSelected();

            /// creez un obiect MyShape cu paramtrii din ConfigPanel si adaug forma la shapeList<MyShape>
            MyShape myShape = new MyShape(shape, color, x, y, size1, size2, stroke, pointCheckBox.isSelected());
            shapeList.add(myShape);
            /// actualizez numarul de Forme
            nShapesLabel.setText(String.valueOf(Integer.parseInt(nShapesLabel.getText()) + 1));

            /// valori de latime/lungime pentru cerc sunt egale deci voi seta in ChoiceBox ca size2 sa fie egal cu size1 automat
            if (shape.equals("Circle"))
                size2ChoiceBox.setValue((int) size1);

            /// Desenez forma
            myShape.Draw(gc);

            //canvasBackUp.push(canvas.snapshot(null, null)); -- din implementarea veche
        });


    }

    public void Exit() {
        Platform.exit();
    }

    public void SaveCanvas() {
        /// FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Files");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg"));
        fileChooser.setInitialFileName("paint.png");

        /// deschid FileChooser in stage-ul curent
        Stage stage = (Stage) canvas.getScene().getWindow();
        File selectedFile = fileChooser.showSaveDialog(stage);

        try {
            // salvez un snapshot al canvas.ului in fisier-ul selectat
            WritableImage snapshot = canvas.snapshot(null, null);
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", selectedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void LoadCanvas() throws IOException {
        /// FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        /// deschid FileChooser in stage-ul curent
        Stage stage = (Stage) canvas.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        /// desnez in canvas imaginea
        Image image = new Image(new FileInputStream(selectedFile));
        gc.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void ResetCanvas() {

        /// daca nu exista forma -> nu se poate executa RESET
        if (nShapesLabel.getText().equals("0"))
            return;

        /// Resetez canvas.ul
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // fac BackUp la forme
        nShapeBackUp = nShapesLabel.getText();
        backUpShapeList = new ArrayList<>(shapeList);

        /// sterg formele din stocare
        nShapesLabel.setText("0");
        shapeList.clear();
        deletedShapeIndex.clear();
    }

    public void UndoReset() {
        // daca nu s-a facut reset -> nu se poate face UndoReset
        if (nShapeBackUp == null)
            return;

        /// in caz se mai afla alte forme desenate de dupa ultimul reset -> le sterg -> Reset fara backup
        deletedShapeIndex.clear();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        /// iau datele din  BackUp
        nShapesLabel.setText(nShapeBackUp);
        shapeList = backUpShapeList;
        nShapeBackUp = null;

        // desenez formele ( cele !Deleted)
        for (MyShape shape : shapeList)
            if (!shape.isDeleted())
                shape.Draw(gc);
    }


    public void UndoDelete() {
        /*  Implementare Veche
        if(canvasBackUp.empty()) {
            System.out.println("Stiva goala");
            return;
        }
        gc.drawImage(canvasBackUp.peek(), 0, 0, canvas.getWidth(), canvas.getHeight());
        canvasBackUp.pop();
         */

        /// Daca nu sunt forme sterse -> nu se poate executa UndoDelete()
        if (deletedShapeIndex.empty())
            return;

        /// Actualizez numarul de forme
        nShapesLabel.setText(String.valueOf(Integer.parseInt(nShapesLabel.getText()) + 1));

        /// preiau indexul ultimului element sters
        int index = deletedShapeIndex.pop();
        shapeList.get(index).setNonDeleted();

        /// desenez formele in ordine pentru a pastra suprapunerea
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (MyShape shape : shapeList)
            if (!shape.isDeleted())
                shape.Draw(gc);

    }

    public void DeleteButton() {
        int index;
        try {
            /// incerc castarea la int
            index = Integer.parseInt(shapeNumberToDelete.getText());
        } catch (Exception e) {
            /// in caz contrar -> nu se poate executa Delete
            return;
        }
        /// index-ul trebuie sa fie intr 1 si nr de forme
        if (index > Integer.parseInt(nShapesLabel.getText()) || index < 1) {
            shapeNumberToDelete.setText(">0 & <=" + nShapesLabel.getText());
            return; // caz contrar -> nu se poate executa Delete
        }

        /// Actualizez numarul de forme
        nShapesLabel.setText(String.valueOf(Integer.parseInt(nShapesLabel.getText()) - 1));
        //canvasBackUp.push(canvas.snapshot(null, null)); -- implementare veche

        /// sterg canvasul
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        /// caut al 'index'-lea element care nu este Deleted
        int indexDeleted = -1;
        for (MyShape shape : shapeList) {
            indexDeleted++;
            if (!shape.isDeleted())
                index--;
            if (index == 0)
                break;
        }
        /// setez obiectul gasit 'Deleted' si adaug la stiva de forme sterse
        shapeList.get(indexDeleted).setDeleted();
        deletedShapeIndex.push(indexDeleted);

        /// desenez formele in ordine pentru a pastra suprapunerea
        for (MyShape shape : shapeList)
            if (!shape.isDeleted())
                shape.Draw(gc);
    }
}
