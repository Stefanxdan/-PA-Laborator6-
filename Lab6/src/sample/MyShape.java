package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Objects;

public class MyShape {
    String type;
    Color color;
    double x, y;
    double size1, size2;
    boolean stroke;
    boolean point;
    private boolean deleted = false;

    public MyShape(String type, Color color, double x, double y, double size1, double size2, boolean stroke, boolean point)
    {
        this.type = type;
        this.color = color;
        this.x = x;
        this.y = y;
        this.size1 = size1;
        this.size2 = size2;
        this.stroke = stroke;
        this.point = point;
    }

    public void setDeleted()
    {
        deleted = true;
    }
    public boolean isDeleted()
    {
        return deleted;
    }
    public void setNonDeleted()
    {
        deleted = false;
    }

    public void Draw(GraphicsContext gc )
    {

        switch (type) {
            case "Rectangle":
                if (stroke) {
                    gc.setStroke(color);
                    gc.strokeRect(x, y, size1, size2);
                } else {
                    gc.setFill(color);
                    gc.fillRect(x, y, size1, size2);
                }
                break;
            case "Triangle":
                if (stroke) {
                    gc.setStroke(color);
                    gc.strokePolygon(new double[]{x + size1 / 2, x, x + size1}, new double[]{y, y + size2, y + size2}, 3);
                } else {
                    gc.setFill(color);
                    gc.fillPolygon(new double[]{x + size1 / 2, x, x + size1}, new double[]{y, y + size2, y + size2}, 3);
                }
                break;
            case "Pentagon":
                /// Toate valorile/rapoartele/ coordonate sunt aproximate masurand o forma perfecta de pentagon
                size1 = size1 * 1.08;
                if (stroke) {
                    gc.setStroke(color);
                    gc.strokePolygon(new double[]{x + size1 / 2, x + size1, x + size1 - size2 * 0.21, x + size2 * 0.21, x},
                            new double[]{y, y + size2 * 0.39, y + size2, y + size2, y + size2 * 0.39}, 5);
                } else {
                    gc.setFill(color);
                    gc.fillPolygon(new double[]{x + size1 / 2, x + size1, x + size1 - size2 * 0.21, x + size2 * 0.21, x},
                            new double[]{y, y + size2 * 0.39, y + size2, y + size2, y + size2 * 0.39}, 5);
                }
                break;
            case "Hexagon":
                /// Toate valorile/rapoartele/ coordonate sunt aproximate masurand o forma perfecta de hexagon
                size1 = size1 * 1.155;
                if (stroke) {
                    gc.setStroke(color);
                    gc.strokePolygon(new double[]{x + size1 * 0.25, x + size1 * 0.75, x + size1, x + size1 * 0.75, x + size1 * 0.25, x},
                            new double[]{y, y, y + size2 / 2, y + size2, y + size2, y + size2 / 2}, 6);
                } else {
                    gc.setFill(color);
                    gc.fillPolygon(new double[]{x + size1 * 0.25, x + size1 * 0.75, x + size1, x + size1 * 0.75, x + size1 * 0.25, x},
                            new double[]{y, y, y + size2 / 2, y + size2, y + size2, y + size2 / 2}, 6);
                }
                break;
            case "RoundRect":
                if (stroke) {
                    gc.setStroke(color);
                    gc.strokeRoundRect(x, y, size1, size2, size1 / 4, size2 / 4);
                } else {
                    gc.setFill(color);
                    gc.fillRoundRect(x, y, size1, size2, size1 / 4, size2 / 4);
                }
                break;
            case "Circle":
                size2 = size1;
                if (stroke) {
                    gc.setStroke(color);
                    gc.strokeOval(x, y, size1, size2);
                } else {
                    gc.setFill(color);
                    gc.fillOval(x, y, size1, size2);
                }
                break;
            case "Oval":
                if (stroke) {
                    gc.setStroke(color);
                    gc.strokeOval(x, y, size1, size2);
                } else {
                    gc.setFill(color);
                    gc.fillOval(x, y, size1, size2);
                }
                break;
        }

        if(point) {
            /// daca forma e desenata cu stroke se pastreaza culoare
            if(stroke)
                gc.setFill(color);
            else /// in caz contrar se calculeaza culoarea opusa pentru un contrast cat mai bun
                gc.setFill(Color.rgb( 255-(int)(color.getRed()*255), 255-(int)(color.getGreen()*255), 255-(int)(color.getBlue()*255)));
            gc.fillOval(x + size1 / 2 - 2, y + size2 / 2 - 2, 4, 4);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyShape shape = (MyShape) o;
        return Double.compare(shape.size1, size1) == 0 &&
                Double.compare(shape.size2, size2) == 0 &&
                stroke == shape.stroke &&
                point == shape.point &&
                Objects.equals(type, shape.type) &&
                Objects.equals(color, shape.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color, size1, size2, stroke, point);
    }
}
