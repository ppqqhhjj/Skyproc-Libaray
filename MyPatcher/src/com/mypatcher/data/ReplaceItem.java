package com.mypatcher.data;

import java.util.ArrayList;
import java.util.List;

import com.mypatcher.type.GenderType;

public class ReplaceItem {
	private String target;
	private List<String> resources;
	private GenderType genderType;

	public ReplaceItem(String target, String resource, GenderType genderType) {
		this.target = target;
		this.genderType = genderType;
		this.resources = new ArrayList<>();
		this.addResource(resource);
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public List<String> getResources() {
		return resources;
	}

	public void setResources(List<String> resources) {
		this.resources = resources;
	}

	public GenderType getGenderType() {
		return genderType;
	}

	public void setGenderType(GenderType genderType) {
		this.genderType = genderType;
	}

	public void addResource(String resource) {
		String[] resources = resource.split(",");
		for (String res : resources) {
			this.resources.add(res.trim());
		}
	}
	

}
