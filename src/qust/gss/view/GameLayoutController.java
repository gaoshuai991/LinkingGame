package qust.gss.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import qust.gss.model.GridItem;
import qust.gss.model.Point;

public class GameLayoutController {
	// 网格大小
	public static final int GRIDSIZE = 8;
	// 图片数量
	public static final int IMAGECOUNT = 15;
	// 难度等级
	public static int LEVEL = 35;
	// 当前选择的网格单元对象
	public static GridItem currentSelected;
	// 当前选择的网格单元对应的ImageView
	public static ImageView currentImageView;
	// 用于表示是否重新开始
	public static boolean isRestart;

	// 网格布局面板对象
	@FXML
	private GridPane grid;
	// 网格面板所在的边界布局面板
	@FXML
	private BorderPane borderPane;
	// 进度条对象
	@FXML
	private ProgressBar progressBar;
	// 进度条任务对象
	private Task<Double> worker;
	// 进度条线程
	private Thread workThread;
	// 用于控制进度条的变量，数据为double型
	private DoubleProperty barNum;
	// 所有的网格单元抽象成二维数组
	private GridItem gridItems[][];
	// 已成功匹配次数
	private int pairNum = 0;
	// 表示是否暂停
	private boolean isPause;
	// 是否开始游戏
	private boolean isStart;
	// 播放音效
	private AudioClip audioClip;
	// 存放所有的ImageView
	private List<ImageView> imgViews;
	private Random random = new Random();
	
	public GameLayoutController() {
		audioClip = new AudioClip("file:Resource/Sounds/aou.wav");
		barNum = new SimpleDoubleProperty(LEVEL);
		imgViews = new ArrayList<>();
		isStart = true;
	}

	/**
	 * 此控制器类被加载后自动调用的方法
	 */
	@FXML
	private void initialize() {
		setGridItem();
		addClickListener();
		initKeyListener();
		while (!isStart) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		isStart = false;
		isRestart = false;
		isPause = false;
		initProgressBar();
	}

	/**
	 * 初始化进度条
	 */
	public void initProgressBar() {
		worker = new Task<Double>() {
			@Override
			protected Double call() throws Exception {
				while (barNum.get() >= 0) {
					if (isRestart) {
						isStart = true;
						return 0.0;
					}
					if (isPause) {
						Thread.sleep(1000);
						continue;
					}
					updateProgress(barNum.get() / LEVEL, 1);
					barNum.set(barNum.get() - 1);
					Thread.sleep(1500);
				}
				updateMessage("GG");
				return 0.0;
			}
		};
		worker.messageProperty().addListener((observable, oldValue, newValue) -> {
			gameOver();
		});
		progressBar.progressProperty().unbind();
		progressBar.setProgress(1);
		progressBar.progressProperty().bind(worker.progressProperty());
		workThread = new Thread(worker);
		workThread.start();
	}

	/**
	 * 重新开始游戏，各数据的初始化
	 */
	@FXML
	public void restart() {
		currentSelected = null;
		currentImageView = null;
		pairNum = 0;
		barNum.set(LEVEL);
		isRestart = true;
		grid.getChildren().removeAll(imgViews);
		initialize();
	}

	/**
	 * 初始化键盘监听器，用于控制游戏的暂停
	 */
	private void initKeyListener() {
		grid.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				isPause = true;
				Alert alert = new Alert(AlertType.NONE);
				alert.setTitle("暂停");
				alert.setContentText("游戏已暂停");
				ButtonType bt = new ButtonType("继续游戏", ButtonData.OK_DONE);
				alert.getButtonTypes().add(bt);
				alert.showAndWait();
				isPause = false;
			}
		});
	}

	/**
	 * 生成32对图片名称，用List<Integer>保存,其中保存的数据为图片名称的前缀
	 * 
	 * @return
	 */
	private List<Integer> initImgName() {
		List<Integer> list = new LinkedList<>();
		int num = 0;
		while (num++ < GRIDSIZE * GRIDSIZE / 2) {
			int n = random.nextInt(IMAGECOUNT) + 1;
			list.add(n);
			list.add(n);
		}
		return list;
	}

	/**
	 * 初始化网格面板的单元格对象
	 */
	public void setGridItem() {
		grid.setGridLinesVisible(true);
		gridItems = new GridItem[GRIDSIZE][GRIDSIZE];
		List<Integer> list = initImgName();
		for (int x = 0; x < GRIDSIZE; x++) {
			for (int y = 0; y < GRIDSIZE; y++) {
				int n = random.nextInt(list.size()); // 从list里随机选出的索引
				Integer name = list.get(n);
				list.remove(n);
				gridItems[x][y] = new GridItem(new Point(x, y), name + ".png");
				ImageView imgView = new ImageView(new Image("file:Resource/Images/" + name + ".png"));
				imgView.setFitWidth(45);
				imgView.setFitHeight(45);
				imgView.setSmooth(true);
				imgView.setCursor(Cursor.HAND);
				imgView.setOpacity(0.95);
				DropShadow shadow = new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(10, 200, 240), 10, 0.7, 0, 0);
				imgView.setOnMouseClicked(e -> {
					imgView.requestFocus();
				});

				imgView.focusedProperty().addListener((observable, oldValue, newValue) -> {
					if (newValue) {
						imgView.setEffect(shadow);
					} else {
						imgView.setEffect(null);
					}
				});
				imgView.setVisible(true);
				imgViews.add(imgView);
				grid.add(imgView, x, y);
			}
		}
	}

	/**
	 * 初始化鼠标监听器
	 */
	private void addClickListener() {
		grid.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
			for (Node node : grid.getChildren()) {
				if (node instanceof ImageView) {
					if (node.getBoundsInParent().contains(e.getSceneX() - 145, e.getSceneY() - 80)) {
						int columnIndex = GridPane.getColumnIndex(node);
						int rowIndex = GridPane.getRowIndex(node);
						if (currentSelected != null && currentSelected.getImgName() != null
								&& currentSelected.getImgName().equals(gridItems[columnIndex][rowIndex].getImgName())) {
							Map<Integer, Object> map = link(currentSelected, gridItems[columnIndex][rowIndex]);
							if (map != null) {
								currentSelected.setImgName(null);
								gridItems[columnIndex][rowIndex].setImgName(null);
								currentImageView.setImage(null);
								((ImageView) node).setImage(null);
								currentSelected = null;
								currentImageView = null;
								barNum.set(barNum.get() + 1);
								audioClip.play();
								if (++pairNum == GRIDSIZE * GRIDSIZE / 2)
									gameWin();
								break;
							}
						}
						currentSelected = gridItems[columnIndex][rowIndex];
						currentImageView = (ImageView) node;
					}
				}
			}
		});
	}

	/**
	 * 游戏失败
	 */
	private void gameOver() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Game Over");
		dialog.setContentText("Game Over!\nRestart?");
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);
		dialog.showAndWait();
		if (dialog.getResult().equals(ButtonType.YES)) {
			isStart = true;
			restart();
		} else {
			grid.getChildren().removeAll(imgViews);
		}
	}

	/**
	 * 游戏成功
	 */
	private void gameWin() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("You win");
		dialog.setContentText("You win!\nRestart?");
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);
		isPause = true;
		dialog.showAndWait();
		isPause = false;
		if (dialog.getResult().equals(ButtonType.YES)) {
			restart();
		} else {
			isPause = true;
			grid.getChildren().removeAll(imgViews);
		}
	}

	/**
	 * 连接整合算法 src和target为两个要匹配的GridItem对象
	 * 
	 * @param src
	 * @param target
	 * @return 以Map<Integer,Object>集合返回，有如下形式<br>
	 *         <li>key==0:零折连接，value:Boolean</li>
	 *         <li>key==1:一折连接，value:Point</li>
	 *         <li>key==2:两折连接，value:Map<Integer,Point></li>
	 */
	public Map<Integer, Object> link(GridItem src, GridItem target) {
		int srcColumn = src.getPoint().getX();
		int srcRow = src.getPoint().getY();
		int targetColumn = target.getPoint().getX();
		int targetRow = target.getPoint().getY();
		if (srcColumn == targetColumn && srcRow == targetRow) {
			return null;
		}
		Map<Integer, Object> map = new HashMap<>();
		if (srcColumn == targetColumn || srcRow == targetRow) {
			if (linkZero(src, target)) {
				map.put(0, true);
				return map;
			}
			Map<Integer, Point> twoMap = linkTwo(src, target);
			if (twoMap != null) {
				map.put(2, twoMap);
				return map;
			}
		} else {
			Point onePoint = linkOne(src, target);
			if (onePoint != null) {
				map.put(1, onePoint);
				return map;
			}
			Map<Integer, Point> twoMap = linkTwo(src, target);
			if (twoMap != null) {
				map.put(2, twoMap);
				return map;
			}
		}
		return null;
	}

	/**
	 * 零折连接算法
	 * 
	 * @param src
	 * @param target
	 * @return 返回是否连接成功
	 */
	public boolean linkZero(GridItem src, GridItem target) {
		int srcColumn = src.getPoint().getX();
		int srcRow = src.getPoint().getY();
		int targetColumn = target.getPoint().getX();
		int targetRow = target.getPoint().getY();
		if (srcColumn != targetColumn && srcRow != targetRow)
			return false;
		if (srcColumn == targetColumn) {
			if (srcColumn != -1 && srcColumn != GRIDSIZE) {
				int minRow = Math.min(srcRow, targetRow);
				int maxRow = Math.max(srcRow, targetRow);
				for (++minRow; minRow < maxRow; minRow++) {
					if (minRow == GRIDSIZE || minRow == -1) {
						continue;
					} else if (!gridItems[srcColumn][minRow].isEmpty()) {
						return false;
					}
				}
			}
		} else {
			if (srcRow != -1 && srcRow != GRIDSIZE) {
				int minColumn = Math.min(srcColumn, targetColumn);
				int maxColumn = Math.max(srcColumn, targetColumn);
				for (++minColumn; minColumn < maxColumn; minColumn++) {
					if (minColumn == GRIDSIZE || minColumn == -1) {
						continue;
					} else if (!gridItems[minColumn][srcRow].isEmpty()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 一折连接算法
	 * 
	 * @param src
	 * @param target
	 * @return 连接成功返回中间点，否则返回null
	 */
	public Point linkOne(GridItem src, GridItem target) {
		int srcColumn = src.getPoint().getX();
		int srcRow = src.getPoint().getY();
		int targetColumn = target.getPoint().getX();
		int targetRow = target.getPoint().getY();
		if (srcColumn == targetColumn || srcRow == targetRow)
			return null;
		Point midPoint = new Point(srcColumn, targetRow);
		if (srcColumn == GRIDSIZE || srcColumn == -1 || targetRow == GRIDSIZE || targetRow == -1) {
			GridItem mid = new GridItem();
			mid.setPoint(midPoint);
			if (linkZero(src, mid) && linkZero(mid, target))
				return midPoint;
		} else if (gridItems[srcColumn][targetRow].isEmpty()) {
			GridItem mid = new GridItem();
			mid.setPoint(midPoint);
			if (linkZero(src, mid) && linkZero(mid, target))
				return midPoint;
		}
		if (targetColumn == GRIDSIZE || targetColumn == -1 || srcRow == GRIDSIZE || srcRow == -1) {
			midPoint = new Point(targetColumn, srcRow);
			GridItem mid = new GridItem();
			mid.setPoint(midPoint);
			if (linkZero(src, mid) && linkZero(mid, target))
				return midPoint;
		} else if (gridItems[targetColumn][srcRow].isEmpty()) {
			midPoint = new Point(targetColumn, srcRow);
			GridItem mid = new GridItem();
			mid.setPoint(midPoint);
			if (linkZero(src, mid) && linkZero(mid, target))
				return midPoint;
		}
		return null;
	}

	/**
	 * 两折连接算法
	 * 
	 * @param src
	 * @param target
	 * @return 连接成功返回两个中间点，否则返回null
	 */
	public Map<Integer, Point> linkTwo(GridItem src, GridItem target) {
		int srcColumn = src.getPoint().getX();
		int srcRow = src.getPoint().getY();
		Map<Integer, Point> mapTwo;
		// 向左
		for (int x = srcColumn - 1; x > -2; x--) {
			if (x == GRIDSIZE || x == -1 || gridItems[x][srcRow].isEmpty()) {
				mapTwo = innerLinkTwo(src, target, x, srcRow);
				if (mapTwo != null)
					return mapTwo;
			} else if (!gridItems[x][srcRow].isEmpty()) {
				break;
			}

		}
		// 向右
		for (int x = srcColumn + 1; x < GRIDSIZE + 1; x++) {
			if (x == GRIDSIZE || x == -1 || gridItems[x][srcRow].isEmpty()) {
				mapTwo = innerLinkTwo(src, target, x, srcRow);
				if (mapTwo != null)
					return mapTwo;
			} else if (!gridItems[x][srcRow].isEmpty()) {
				break;
			}
		}
		// 向上
		for (int y = srcRow - 1; y > -2; y--) {
			if (y == GRIDSIZE || y == -1 || gridItems[srcColumn][y].isEmpty()) {
				mapTwo = innerLinkTwo(src, target, srcColumn, y);
				if (mapTwo != null)
					return mapTwo;
			} else if (!gridItems[srcColumn][y].isEmpty()) {
				break;
			}
		}
		// 向下
		for (int y = srcRow + 1; y < GRIDSIZE + 1; y++) {
			if (y == GRIDSIZE || y == -1 || gridItems[srcColumn][y].isEmpty()) {
				mapTwo = innerLinkTwo(src, target, srcColumn, y);
				if (mapTwo != null)
					return mapTwo;
			} else if (!gridItems[srcColumn][y].isEmpty()) {
				break;
			}

		}
		return null;
	}

	/**
	 * 两折连接内部用于判断的方法
	 * 
	 * @param src
	 * @param target
	 * @param column
	 *            当前遍历到的列数
	 * @param row
	 *            当前遍历到的行数
	 * @return
	 */
	public Map<Integer, Point> innerLinkTwo(GridItem src, GridItem target, int column, int row) {
		Point midPointa = new Point(column, row);
		GridItem mid = new GridItem();
		mid.setPoint(midPointa);
		if (linkZero(src, mid)) {
			Point midPointb = linkOne(mid, target);
			if (midPointb != null) { // 可以连接
				Map<Integer, Point> map = new HashMap<>();
				map.put(1, midPointa);
				map.put(2, midPointb);
				return map;
			}
		}
		return null;
	}

}
