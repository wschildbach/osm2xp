package com.osm2xp.classification.parsing;

import com.osm2xp.core.model.osm.Node;
import com.osm2xp.core.model.osm.Relation;
import com.osm2xp.core.model.osm.Way;
import com.osm2xp.core.parsers.IOSMDataVisitor;

import math.geom2d.Box2D;

public class CompositeVisitor implements IOSMDataVisitor {

	private IOSMDataVisitor[] children;

	public CompositeVisitor(IOSMDataVisitor... children) {
		this.children = children;
	}

	@Override
	public void visit(Box2D box) {
		for (IOSMDataVisitor visitor : children) {
			visitor.visit(box);
		}
	}

	@Override
	public void visit(Node node) {
		for (IOSMDataVisitor visitor : children) {
			visitor.visit(node);
		}
	}

	@Override
	public void visit(Way way) {
		for (IOSMDataVisitor visitor : children) {
			visitor.visit(way);
		}
	}

	@Override
	public void visit(Relation relation) {
		for (IOSMDataVisitor visitor : children) {
			visitor.visit(relation);
		}
	}

	@Override
	public void complete() {
		for (IOSMDataVisitor visitor : children) {
			visitor.complete();
		}
	}

}
