package gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import resources.Candle;

public class StockCanvas extends Canvas {
	
	private AppPanel owner;
	
	private double min;
	private double max;
	
	private int ratioY = 150;
	private static int MIDRATIO = 150;
	private int ratioX = 5;
	private int posX = 100;
	private static int INITIAL = 3;
	
	private ArrayList<Rectangle> rectangles;
	
	
	private void listeners() {
		
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_LEFT) posX = (posX - 1 < 0) ? posX : posX - 1;
				if(key == KeyEvent.VK_RIGHT) posX = (posX + 1 > 110) ? posX : posX + 1;
				if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) repaint();
			}		
		});
		
		addMouseWheelListener(mwe -> {
			int move = -mwe.getWheelRotation();
			if(mwe.isControlDown()) ratioY = (ratioY + 3 * move >= 120 && ratioY + 3 * move <= 180) ? ratioY + 3 * move : ratioY;
			else ratioX = (ratioX + move >= 2 && ratioX + move <= 8) ? ratioX + move : ratioX;
			repaint();
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				Point point = e.getPoint();
				checkMouse(point);
			}
		
		});
		
	}
	
	private void checkMouse(Point point) {
		if(rectangles == null) return;
		for(int i = 0; i < rectangles.size(); i++) {
			if(pointInRect(point, rectangles.get(i))) {
				owner.infoPanel.setInfo(owner.stock.getCandles().get(i));
				break;
			}			
		}	
	}
	
	private static boolean pointInRect(Point point, Rectangle rect) {
		return point.x > rect.x && point.x < rect.x + rect.width && point.y > rect.y && point.y < rect.y + rect.height;
	}
	
	private void drawCandles() {
		drawGrid();
		
		ArrayList<Candle> candles = owner.stock.getCandles();
		Graphics g = getGraphics();
		rectangles = new ArrayList<>();
		
		double mid = (max - min) / 2;
		int midInt = getHeight() / 2;
		// max - high
		
		for(int index = 0; index < candles.size(); index++) {
			double high = candles.get(index).getHigh();
			double low = candles.get(index).getLow();
			int tempPosX = index * INITIAL * ratioX - posX * (candles.size() * INITIAL * ratioX - getWidth()) / 100;
			int tempPosY = midInt + (int)((mid - (max - high)) * getHeight() / (max - min));
			int tempWidth = INITIAL * ratioX;
			int tempHeight = (int)((ratioY * high - ratioY * low) / MIDRATIO * getHeight() / (max - min));
			
			Rectangle rect = new Rectangle(tempPosX, tempPosY, tempWidth, tempHeight);
			rectangles.add(rect);
			
			if(candles.get(index).getOpen() > candles.get(index).getClose()) g.setColor(Color.RED);
			else g.setColor(Color.GREEN);
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
			
			if(candles.get(index).getOpen() > candles.get(index).getClose()) g.setColor(new Color(Integer.parseInt("990000", 16)));
			else g.setColor(new Color(Integer.parseInt("009900", 16)));
			g.drawRect(rect.x, rect.y, rect.width, rect.height);
		}
	}
	
	private void drawGrid() {
		Graphics g = getGraphics();
		
		g.setColor(new Color(Integer.parseInt("E0E0E0", 16)));
		for(int i = 0; i < getHeight(); i += 30) {
			g.drawLine(0, i, getWidth(), i);
		}
		for(int i = 0; i < getWidth(); i += 30) {
			g.drawLine(i, 0, i, getHeight());
		}
		
	}
	
	@Override
	public void paint(Graphics g) {
		drawCandles();
	}
	
	public StockCanvas(AppPanel owner) {
		this.owner = owner;
		setBackground(new Color(Integer.parseInt("CCFFFF", 16)));
		setSize(900, 700);
		
		min = owner.stock.getCandles().get(0).getLow();
		max = 0;
		for(Candle candle : owner.stock.getCandles()) {
			if(candle.getHigh() < min) min = candle.getHigh();
			if(candle.getHigh() > max) max = candle.getHigh();
		}
		max *= 1.01;
		min *= 0.99;
		listeners();
	}

}
