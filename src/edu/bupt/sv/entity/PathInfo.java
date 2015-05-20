package edu.bupt.sv.entity;

import java.util.List;

public class PathInfo {

	public List<Point> pathNodes;
	public List<Integer> links;
	
	public PathInfo(List<Point> pathNodes, List<Integer> links) {
		super();
		this.pathNodes = pathNodes;
		this.links = links;
	}
	
	
}
