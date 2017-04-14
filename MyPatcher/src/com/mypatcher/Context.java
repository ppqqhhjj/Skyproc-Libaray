package com.mypatcher;

import skyproc.Mod;

public class Context {
	
	private Mod merger;
	private Mod patch;
	public Mod getMerger() {
		return merger;
	}
	public void setMerger(Mod merger) {
		this.merger = merger;
	}
	public Mod getPatch() {
		return patch;
	}
	public void setPatch(Mod patch) {
		this.patch = patch;
	}
	

}
