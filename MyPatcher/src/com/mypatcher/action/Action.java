package com.mypatcher.action;

import com.mypatcher.Context;

import skyproc.Mod;

public abstract class Action {
	protected Mod merger;
	protected Mod patch;

	public Action(Context context) {
		this.merger = context.getMerger();
		this.patch = context.getPatch();
	}
	
	public abstract void doAction();
	

}
