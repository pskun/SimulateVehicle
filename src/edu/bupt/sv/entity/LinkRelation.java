package edu.bupt.sv.entity;

public class LinkRelation {
	private Integer id;
	//0，1，2分别为直行，左转，右转，没有，则为空
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
	

}
