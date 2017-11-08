package qust.gss.model;
/**
 * 网格单元类，用于抽象表示每个单元格
 * @author Administrator
 *
 */
public class GridItem {
	// 单元格所在点
	private Point point;
	// 单元格的图片名称
	private String imgName;
	// 单元格是否被选中,默认为false
	private boolean selected;

	public GridItem() {
	}

	public GridItem(Point point, String imgName) {
		this.point = point;
		this.imgName = imgName;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public boolean isEmpty() {
		return imgName == null;
	}
	public boolean isSelected(){
		return selected;
	}
	public void setIsSelected(boolean selected){
		this.selected = selected;
	}

}
