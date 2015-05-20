package edu.bupt.sv.entity;

public class LinkRelation implements EntityConstants {
	// ��ǰ��linkID
	private Integer id;
	//0��1��2�ֱ�Ϊֱ�У���ת����ת��û�У���Ϊ��
	private Integer[] nextlinks;
	
	public LinkRelation(Integer id, Integer[] nextlinks) {
		super();
		this.id = id;
		this.nextlinks = nextlinks;
	}
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer[] getNextlinks() {
		return nextlinks;
	}
	public void setNextlinks(Integer[] nextlinks) {
		this.nextlinks = nextlinks;
	}
	
	public Integer getNextLink(int direction) {
		return nextlinks[direction];
	}
}
