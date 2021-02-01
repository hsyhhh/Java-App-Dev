import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
import java.util.*;
import java.io.*;

public class Main extends JFrame
{
	Main() {
		ToolPanel tp = new ToolPanel();
		CanvasPanel cp = new CanvasPanel();
		setTitle("miniCAD");
		setSize(800, 500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(tp, BorderLayout.WEST);
		add(cp, BorderLayout.CENTER);
		setVisible(true);
		// create menu
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		JMenuItem openItem = new JMenuItem("Open");
		fileMenu.add(openItem);
		JMenuItem saveItem = new JMenuItem("Save");
		fileMenu.add(saveItem);
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("here in open.");
				try {
					FileDialog fd = createFileDialog("Open");
					fd.setVisible(true);
					FileInputStream fis;
					ObjectInputStream ois;
					if(fd.getDirectory() != null && fd.getFile() != null) {
						fis = new FileInputStream(fd.getDirectory() +  fd.getFile());
						ois = new ObjectInputStream(fis);
						System.out.println("opening file " + fd.getDirectory() +  fd.getFile());
						List<Shape> ls = (List<Shape>) ois.readObject();
						Model.setShapeList(ls);
						cp.repaint();
					}
				} catch(Exception exception) {
					System.out.println("exception in open.");
					exception.printStackTrace();
			 	}
			}
		});
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("here in save.");
				try {
					FileDialog fd = createFileDialog("Save");
					fd.setVisible(true);
					FileOutputStream fos;
					ObjectOutputStream oos;
					if(fd.getDirectory() != null && fd.getFile() != null) {
						fos = new FileOutputStream(fd.getDirectory() +  fd.getFile());
						oos = new ObjectOutputStream(fos);
						oos.writeObject(Model.getShapeList());
						fos.flush();
					}
				} catch(Exception exception) { 
					System.out.println("exception in save.");
					exception.printStackTrace();
				}
			}
		});
	}

	public static void main(String[] args) {
	    /* main module */
	    SwingUtilities.invokeLater(()->{
			new Main();
		});
	}

	public FileDialog createFileDialog(String s) {
		FileDialog fd;
		if(s.equals("Save")) {
			fd = new FileDialog(this, "Save", FileDialog.SAVE);
		} else if(s.equals("Open")) {
			fd = new FileDialog(this, "Open", FileDialog.LOAD);
		} else {
			fd = null;
		}
		return fd;
	}
}

class CanvasPanel extends JPanel
{
	static boolean isSelected = false;
	Point s,f;

	CanvasPanel() {
		setBackground(Color.white);
		// setBorder(BorderFactory.createEtchedBorder());
		s = new Point();
		f = new Point();
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				requestFocus();
				if(isSelected) {
					s.x = f.x;
					s.y = f.y;
					f.x = e.getX();
					f.y = e.getY();
					Model.dragSelectedShape(f.x - s.x, f.y - s.y);
				} else {
					System.out.println("mouseDragged");
					Shape staticShape = Model.getStaticShape();
					if(staticShape != null) {
						staticShape.setFPoint(e.getX(), e.getY());
					}
				}
				repaint();
			}

			public void mouseMoved(MouseEvent e) {
				// System.out.println(e.getX()+" "+e.getY());
			}
		});
		addMouseListener(new MouseListener() {
			public void mousePressed(MouseEvent e) {
				requestFocus();
				System.out.println("mousePressed");
				if(isSelected) {
					int x, y;
					x = e.getX();
					y = e.getY();
					f.x = x;
					f.y = y;
					isSelected = Model.testSelected(x, y);
					System.out.println(x+" "+y);
					if(!isSelected) {
						Model.clearSelected();
					}
				} else {
					Model.newStaticShape(ToolPanel.shapeType, ToolPanel.colorType, ToolPanel.s);
					Model.getStaticShape().setSPoint(e.getX(), e.getY());
					Model.getStaticShape().setFPoint(e.getX(), e.getY());
				}
				repaint();
			}

			public void mouseClicked(MouseEvent e) {
				requestFocus();
				System.out.println("mouseClicked");
				int x, y;
				x = e.getX();
				y = e.getY();
				isSelected = Model.testSelected(x, y);
				System.out.println(isSelected);
				if(!isSelected) {
					Model.clearSelected();
				}
				repaint();
			}

			public void mouseReleased(MouseEvent e) {
				requestFocus();
				if(!isSelected) {
					System.out.println("mouseReleased");
					Shape s = Model.getStaticShape();
					double distance = 0;
					if(s != null)
						distance = Math.pow(s.sx-s.fx, 2) + Math.pow(s.sy-s.fy, 2);
					if(distance >= 20) {
						System.out.println("Adding a new Shape!");
						Model.releaseStaticShape();
					} else {
						System.out.println("Abandon the drawing Shape!");
						Model.abandonStaticShape();
					}
				}
				repaint();
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}
		});
		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				requestFocus();
				System.out.println("keyTyped1");
				char key = e.getKeyChar();
				if(isSelected || key == 'e') {
					System.out.println("keyTyped2");
					switch(key) {
						case '=': case '+':
							Model.increaseSelectedShape();
							break;
						case '-': case '_':
							Model.decreaseSelectedShape();
							break;
						case 'r':
							Model.deleteSelectedShape();
							break;
						case 'e':
							Model.removeAllShape();
							break;
						case ']': case '}':
							Model.increaseStrokeOfSelectedShape();
							break;
						case '[': case '{':
							Model.decreaseStrokeOfSelectedShape();
							break;
						default:
							break;
					}
				}
				repaint();
			}

			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}
		});

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		List<Shape> shapeList = Model.getShapeList();
		for(Shape s : shapeList) {
			if(s != null && s.drawable())
				s.draw((Graphics2D)g);
		}
		System.out.println("paintComponent");
		Shape staticShape = Model.getStaticShape();
		if(staticShape != null && staticShape.drawable())
			staticShape.draw((Graphics2D)g);
	}

}
class ToolPanel extends JPanel 
{
	static ShapeType shapeType = ShapeType.LINE;
	static Color colorType = Color.black;
	static String s;
	JButton [] btn = new JButton[4];
	String[] btnPicPath = {"line.png", "rectangle.png", "circle.png", "text.png"};
	Color[] colorArray = {	Color.black, Color.gray, Color.darkGray,
							Color.blue, Color.cyan, Color.green,  
							Color.pink, Color.orange, Color.yellow,
							Color.red, Color.magenta,  Color.lightGray };
	class ColorPanel extends JPanel
	{
		class ColorButtonListener implements ActionListener
		{
			private Color colorType = Color.black;
			ColorButtonListener(Color colorType) {
				this.colorType = colorType;
			}
			public void actionPerformed(ActionEvent e) {
				ToolPanel.colorType = this.colorType;
				if(CanvasPanel.isSelected) {
					Model.changeColorOfSelectedShape(this.colorType);
				}
				System.out.println(ToolPanel.colorType);
			}
		}
		ColorPanel() {
			setLayout(new GridLayout(4, 3));
			JButton [] btnColor = new JButton[12];
			for(int i = 0; i < 12; i++) {
				btnColor[i] = new JButton();
				btnColor[i].setBackground(colorArray[i]);
				btnColor[i].addActionListener(new ColorButtonListener(colorArray[i]));
				add(btnColor[i]);
			}
		}
	}
	class ButtonListener implements ActionListener
	{
		private ShapeType shapeType = ShapeType.LINE;
		ButtonListener(ShapeType shapeType) {
			this.shapeType = shapeType;
		}
		public void actionPerformed(ActionEvent e) {
			ToolPanel.shapeType = this.shapeType;
			System.out.println(ToolPanel.shapeType);
			if(ToolPanel.shapeType == ShapeType.TEXT) {
				ToolPanel.s = JOptionPane.showInputDialog("Please enter your input: ");
			}
		}
	}
	ToolPanel() {
		ColorPanel cp = new ColorPanel();
		setLayout(new GridLayout(5, 1));
		setBorder(BorderFactory.createEtchedBorder());
		// layout 4 drawing buttons
		for(int i = 0; i < 4; i++) {
			btn[i] = new JButton();
			ImageIcon ii = new ImageIcon(".\\icon\\" + btnPicPath[i]);
			btn[i].setIcon(ii);
			btn[i].setVisible(true);
			btn[i].addActionListener(new ButtonListener(ShapeType.getEnumValue(i)));
			add(btn[i]);
		}
		// layout color buttons
		add(cp);
		cp.setVisible(true);
	}
}

enum ShapeType {
	LINE, RECTANGLE, CIRCLE, TEXT;

	public static ShapeType getEnumValue(int value) {
		switch(value) {
			case 0:
				return LINE;
			case 1:
				return RECTANGLE;
			case 2:
				return CIRCLE;
			case 3:
				return TEXT;
			default:
				return null;
		}
	}
}