package org.jenkinsci.plugins.p4.populate;

import java.io.Serializable;

import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Describable;
import jenkins.model.Jenkins;

public abstract class Populate implements ExtensionPoint, Describable<Populate>, Serializable {

	private static final long serialVersionUID = 1L;

	private final boolean have; // ! sync '-p'
	private final boolean force; // sync '-f'
	private final boolean modtime;
	private final boolean quiet; // task '-q'
	private final String pin;
	private final ParallelSync parallel;

	public Populate(boolean have, boolean force, boolean modtime, boolean quiet, String pin, ParallelSync parallel) {
		this.have = have;
		this.force = force;
		this.modtime = modtime;
		this.pin = pin;
		this.quiet = quiet;
		this.parallel = parallel;
	}

	public boolean isHave() {
		return have;
	}

	public boolean isForce() {
		return force;
	}

	public boolean isModtime() {
		return modtime;
	}

	public boolean isQuiet() {
		return quiet;
	}

	public String getPin() {
		return pin;
	}

	public ParallelSync getParallel() {
		return parallel;
	}

	public PopulateDescriptor getDescriptor() {
		return (PopulateDescriptor) Jenkins.getInstance().getDescriptor(getClass());
	}

	public static DescriptorExtensionList<Populate, PopulateDescriptor> all() {
		return Jenkins.getInstance().<Populate, PopulateDescriptor> getDescriptorList(Populate.class);
	}
}
