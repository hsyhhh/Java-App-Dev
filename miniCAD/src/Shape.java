import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.Serializable;

abstract class Shape implements Serializable
{
	protected int sx, sy, fx, fy;
	protected Color color = Color.black;
	protected float strokeValue = 1.0f;
	protected final int zoomFactor = 3;
	public boolean isSelected = false;
	Shape() {
	}

	Shape(int sx, int sy, int fx, int fy) {
		this.sx = sx; this.sy = sy;
		this.fx = fx; this.fy = fy;
	}

	public boolean drawable() {
		return (sx != fx) || (sy != fy);
	}

	public void setSPoint(int sx, int sy) {
		this.sx = sx;
		this.sy = sy;
	}

	public void setFPoint(int fx, int fy) {
		this.fx = fx;
		this.fy = fy;
	}

	public void setColor(Color c) {
		this.color = c;
	}

	public void move(int dx, int dy) {
		sx += dx; fx += dx;
		sy += dy; fy += dy;
	}

	public void increase() {
		sx -= zoomFactor; sy -= zoomFactor;
		fx += zoomFactor; fy += zoomFactor;
	}

	public void decrease() {
		if(fx > sx + 2 * zoomFactor && fy > sy + 2 * zoomFactor) {
			sx += zoomFactor; sy += zoomFactor;
			fx -= zoomFactor; fy -= zoomFactor;			
		}
	}
	public void increaseStroke() {
		strokeValue += 0.5f;
	}
	public void decreaseStroke() {
		if(strokeValue > 0.5f)
			strokeValue -= 0.5f;
	}
	public abstract void draw(Graphics2D g2d);
	public abstract boolean testSelected(int x, int y);
}

class Line extends Shape
{
	Line() {
		super();
	}

	Line(int sx, int sy, int fx, int fy) {
		super(sx, sy, fx, fy);
	}

	public void draw(Graphics2D g2d) {
		// 抗锯齿
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(isSelected) {
			g2d.setColor(Color.red);
		} else {
			g2d.setColor(color);
		}
		g2d.setStroke(new BasicStroke(strokeValue));
		g2d.drawLine(sx, sy, fx, fy);
	}

	public double PointLine_Disp(double xx, double yy, double x1, double y1, double x2, double y2){
	    double a, b, c, ang1, ang2, ang, m;
	    double result = 0;
	    //分别计算三条边的长度
	    a = Math.sqrt((x1 - xx) * (x1 - xx) + (y1 - yy) * (y1 - yy));

	    if (a == 0)
	        return -1;
	    b = Math.sqrt((x2 - xx) * (x2 - xx) + (y2 - yy) * (y2 - yy));
	    if (b == 0)
	        return -1;
	    c = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	    //如果线段是一个点则退出函数并返回距离
	    if (c == 0)
	    {
	        result = a;
	        return result;
	    }
	    //如果点(xx,yy到点x1,y1)这条边短
	    if (a < b)
	    {
	        //如果直线段AB是水平线。得到直线段AB的弧度
	        if (y1 == y2)
	        {
	            if (x1 < x2)
	                ang1 = 0;
	            else
	                ang1 = Math.PI;
	        }
	        else
	        {
	            m = (x2 - x1) / c;
	            if (m - 1 > 0.00001)
	                m = 1;
	            ang1 = Math.acos(m);
	            if (y1 >y2)
	                ang1 = Math.PI*2  - ang1;//直线(x1,y1)-(x2,y2)与折X轴正向夹角的弧度
	        }
	        m = (xx - x1) / a;
	        if (m - 1 > 0.00001)
	            m = 1;
	        ang2 = Math.acos(m);
	        if (y1 > yy)
	            ang2 = Math.PI * 2 - ang2;//直线(x1,y1)-(xx,yy)与折X轴正向夹角的弧度

	        ang = ang2 - ang1;
	        if (ang < 0) ang = -ang;

	        if (ang > Math.PI) ang = Math.PI * 2 - ang;
	        //如果是钝角则直接返回距离
	        if (ang > Math.PI / 2)
	            return a;
	        else
	            return a * Math.sin(ang);
	    }
	    else//如果(xx,yy)到点(x2,y2)这条边较短
	    {
	        //如果两个点的纵坐标相同，则直接得到直线斜率的弧度
	        if (y1 == y2)
	            if (x1 < x2)
	                ang1 = Math.PI;
	            else
	                ang1 = 0;
	        else
	        {
	            m = (x1 - x2) / c;
	            if (m - 1 > 0.00001)
	                m = 1;
	            ang1 = Math.acos(m);
	            if (y2 > y1)
	                ang1 = Math.PI * 2 - ang1;
	        }
	        m = (xx - x2) / b;
	        if (m - 1 > 0.00001)
	            m = 1;
	        ang2 = Math.acos(m);//直线(x2-x1)-(xx,yy)斜率的弧度
	        if (y2 > yy)
	            ang2 = Math.PI * 2 - ang2;
	        ang = ang2 - ang1;
	        if (ang < 0) ang = -ang;
	        if (ang > Math.PI) ang = Math.PI * 2 - ang;//交角的大小
	        //如果是对角则直接返回距离
	        if (ang > Math.PI / 2)
	            return b;
	        else
	            return b * Math.sin(ang);//如果是锐角，返回计算得到的距离
	    }
	}
	public boolean testSelected(int x, int y) {
		double distance = PointLine_Disp(x,y,sx,sy,fx,fy);
		if(distance <= 12 + strokeValue/2)
			return true;
		else
			return false;
	}
	public void increase() {
		int lineZoomFactor = 2;
		if(sx == fx) {
			if(sy < fy) {
				sy -= lineZoomFactor;
				fy += lineZoomFactor;
			} else {
				fy -= lineZoomFactor;
				sy += lineZoomFactor;
			}
		} else if(sx < fx) {
			fx += lineZoomFactor;
			fy += lineZoomFactor * (fy - sy) / (fx - sx);
			sx -= lineZoomFactor;
			sy -= lineZoomFactor * (fy - sy) / (fx - sx);
		} else {
			sx += lineZoomFactor;
			sy += lineZoomFactor * (fy - sy) / (fx - sx);
			fx -= lineZoomFactor;
			fy -= lineZoomFactor * (fy - sy) / (fx - sx);
		}
	}

	public void decrease() {
		int lineZoomFactor = 2;
		if(Math.abs(sx - fx) >= lineZoomFactor && Math.abs(sy - fy) >= lineZoomFactor) {
			if(sx == fx) {
				if(sy < fy) {
					sy += lineZoomFactor;
					fy -= lineZoomFactor;
				} else {
					fy += lineZoomFactor;
					sy -= lineZoomFactor;
				}
			} else if(sx < fx) {
				fx -= lineZoomFactor;
				fy -= lineZoomFactor * (fy - sy) / (fx - sx);
				sx += lineZoomFactor;
				sy += lineZoomFactor * (fy - sy) / (fx - sx);
			} else {
				sx -= lineZoomFactor;
				sy -= lineZoomFactor * (fy - sy) / (fx - sx);
				fx += lineZoomFactor;
				fy += lineZoomFactor * (fy - sy) / (fx - sx);
			}
		}
	}
}

class Rectangle extends Shape
{
	Rectangle() {
		super();
	}

	Rectangle(int sx, int sy, int fx, int fy) {
		super(sx, sy, fx, fy);
	}

	public void draw(Graphics2D g2d) {
		// adjustSFPosition();
		// 抗锯齿
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(isSelected) {
			g2d.setColor(Color.red);
		} else {
			g2d.setColor(color);
		}
		g2d.setStroke(new BasicStroke(strokeValue));
		g2d.drawLine(sx, sy, sx, fy);
		g2d.drawLine(sx, fy, fx, fy);
		g2d.drawLine(fx, fy, fx, sy);
		g2d.drawLine(fx, sy, sx, sy);
	}
	public void adjustSFPosition() {
		if(sx < fx && sy > fy) {
			int tmp = sy;
			sy = fy;
			fy = tmp;
		} else if(sx > fx && sy > fy) {
			int tmp = fx;
			fx = sx;
			sx = tmp;
			tmp = fy;
			fy = sy;
			sy = tmp;
		} else if(fx < sx && fy > sy) {
			int tmp = sx;
			sx = fx;
			fx = tmp;
		}
	}
	public boolean testSelected(int x, int y) {
		double d = 10 + strokeValue/2;
		return ((x>=(sx-d))&&(x<=(sx+d))&&(y>=(sy-d))&&(y<=(fy+d))) ||
			   ((x>=(fx-d))&&(x<=(fx+d))&&(y>=(sy-d))&&(y<=(fy+d))) ||
			   ((x>=(sx+d))&&(x<=(fx-d))&&(y>=(sy-d))&&(y<=(sy+d))) ||
			   ((x>=(sx+d))&&(x<=(fx-d))&&(y>=(fy-d))&&(y<=(fy+d)));
	}
}

class Circle extends Shape
{
	Circle() {
		super();
	}

	Circle(int sx, int sy, int fx, int fy) {
		super(sx, sy, fx, fy);
	}
	
	public void draw(Graphics2D g2d) {
		// 抗锯齿
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(isSelected) {
			g2d.setColor(Color.red);
		} else {
			g2d.setColor(color);
		}
		g2d.setStroke(new BasicStroke(strokeValue));
		if(fx > sx && fy > sy)
			g2d.drawOval(sx, sy, fx - sx, fx - sx);
		else if(fx > sx && sy > fy)
			g2d.drawOval(sx, sy - fx + sx, fx - sx, fx - sx);
		else if(sx > fx && fy > sy)
			g2d.drawOval(fx, sy, sx - fx, sx - fx);
		else if(sx > fx && sy > fy)
			g2d.drawOval(fx, sy - sx + fx, sx - fx, sx - fx);
	}

	public void adjustSFPosition() {
		int d = Math.abs(fx - sx);
		if(sx < fx && sy > fy) {
			int tmp = fy;
			sy = fy;
			fx = sx + d;
			fy = sy + d;
		} else if(sx > fx && sy > fy) {
			fx = sx;
			fy = sy;
			sx = fx - d;
			sy = fy - d;
		} else if(fx < sx && fy > sy) {
			sx = sx - d;
			fx = sx + d;
			fy = sy + d;
		} else if(fx > sx && fy > sy) {
			fx = sx + d;
			fy = sy + d;
		}
	}

	public boolean testSelected(int x, int y) {
		double centerx, centery, distance;
		double r = Math.abs(sx - fx) / 2;
		centerx = sx + r;
		centery = sy + r;
		distance = Math.sqrt(Math.pow(x-centerx, 2) + Math.pow(y-centery, 2));
		if(Math.abs(distance - r) <= 5 + strokeValue/2)
			return true;
		else
			return false;
	}
}

class Text extends Shape
{
	String s = "";
	FontMetrics fm;
	Text(String s) {
		super();
		this.s = s;
	}
	public boolean drawable() {
		if(s.length() != 0)
			return true;
		else
			return false;
	}
	public void draw(Graphics2D g2d) {
		// 抗锯齿
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int fontSize = Math.min(Math.abs(sx - fx), Math.abs(sy - fy));
		g2d.setFont(new Font(null, 0, fontSize));
		if(isSelected) {
			g2d.setColor(Color.red);
		} else {
			g2d.setColor(color);
		}
		g2d.setStroke(new BasicStroke(strokeValue));
		fm = g2d.getFontMetrics();
		if(fy > sy)
			g2d.drawString(s, sx, (int)(sy + 0.75 * fontSize));
		else
			g2d.drawString(s, sx, (int)(sy - 0.25 * fontSize));
	}

	public boolean testSelected(int x, int y) {
		if(fy > sy) {
			// System.out.println("here");
			return (x >= sx) && (x <= sx + fm.stringWidth(s)) &&
				   (y <= fy) && (y >= fy - fm.getHeight());
		} else {
			return (x >= sx) && (x <= sx + fm.stringWidth(s)) &&
				   (y >= fy) && (y <= fy + fm.getHeight());
		}
	}
}