import java.util.*;
import java.awt.Color;
import javax.swing.JOptionPane;

class Model
{
	private static List<Shape> shapeList = new ArrayList<>();
	public static Shape staticShape = null;

	public static List<Shape> getShapeList() {
		return shapeList;
	}
	public static void setShapeList(List<Shape> ls) {
		shapeList = ls;
	}
	public static Shape getStaticShape() {
		return staticShape;
	}
	public static void newStaticShape(ShapeType shapeType, Color colorType, String s) {
		switch(shapeType) {
			case LINE:
				staticShape = new Line();
				break;
			case RECTANGLE:
				staticShape = new Rectangle();
				break;
			case CIRCLE:
				staticShape = new Circle();
				break;
			case TEXT:
				staticShape = new Text(s);
				break;
			default:
				break;
		}
		staticShape.setColor(colorType);
	}

	public static void releaseStaticShape() {
		if(staticShape instanceof Rectangle) {
			((Rectangle)staticShape).adjustSFPosition();
		} else if(staticShape instanceof Circle) {
			((Circle)staticShape).adjustSFPosition();
		}
		addShape(staticShape);
		staticShape = null;
	}

	public static void abandonStaticShape() {
		staticShape = null;
	}
	public static void addShape(Shape s) {
		shapeList.add(s);
	}
	public static void removeShape(Shape s) {
		shapeList.remove(s);
	}
	public static void removeAllShape() {
		shapeList.removeAll(shapeList);
	}
	public static boolean testSelected(int x, int y) {
		boolean isSelected = false; // 全局是否有选中的图形
		for(Shape s : shapeList) {
			s.isSelected = false;
			if(!isSelected && s != null) {
				if(s.testSelected(x, y)) {
					isSelected = true;
					s.isSelected = true;
				}
			}
		}
		return isSelected;
	}
	public static void clearSelected() {
		for(Shape s : shapeList) {
			s.isSelected = false;
		}
	}
	public static void dragSelectedShape(int dx, int dy) {
		for(Shape shape : shapeList) {
			if(shape.isSelected) {
				shape.move(dx, dy);
			}
		}
	}
	public static void changeColorOfSelectedShape(Color colorType) {
		for(Shape shape : shapeList) {
			if(shape.isSelected) {
				shape.setColor(colorType);
			}
		}
	}
	public static void increaseSelectedShape() {
		for(Shape shape : shapeList) {
			if(shape.isSelected) {
				shape.increase();
			}
		}
	}
	public static void decreaseSelectedShape() {
		for(Shape shape : shapeList) {
			if(shape.isSelected) {
				shape.decrease();
			}
		}
	}
	// 有线程同步问题，不能用迭代器
	public static void deleteSelectedShape() {
		for(int i = 0; i < shapeList.size(); i++) {
			if(shapeList.get(i).isSelected) {
				removeShape(shapeList.get(i));
			}
		}
	}
	public static void increaseStrokeOfSelectedShape() {
		for(Shape shape : shapeList) {
			if(shape.isSelected) {
				shape.increaseStroke();
			}
		}
	}
	public static void decreaseStrokeOfSelectedShape() {
		for(Shape shape : shapeList) {
			if(shape.isSelected) {
				shape.decreaseStroke();
			}
		}
	}
}