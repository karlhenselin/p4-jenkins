package org.jenkinsci.plugins.p4_client.tagging;

import hudson.model.AbstractBuild;
import hudson.scm.AbstractScmTagAction;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.jenkinsci.plugins.p4_client.PerforceScm;
import org.jenkinsci.plugins.p4_client.client.ClientHelper;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.perforce.p4java.client.IClientViewMapping;
import com.perforce.p4java.core.ILabelMapping;
import com.perforce.p4java.core.ViewMap;
import com.perforce.p4java.impl.generic.client.ClientView;
import com.perforce.p4java.impl.generic.core.Label;
import com.perforce.p4java.impl.generic.core.Label.LabelMapping;

public class TagAction extends AbstractScmTagAction {

	private List<String> tags = new ArrayList<String>();
	private String credential;
	private String client;
	private int change;

	public TagAction(AbstractBuild<?, ?> build) {
		super(build);
	}

	public String getIconFileName() {
		if (!getACL().hasPermission(PerforceScm.TAG))
			return null;
		return "/plugin/p4-client/icons/label.gif";
	}

	public String getDisplayName() {
		if (isTagged())
			return "Perforce Label";
		else
			return "Label This Build";
	}

	@Override
	public boolean isTagged() {
		return tags != null && !tags.isEmpty();
	}

	public void doSubmit(StaplerRequest req, StaplerResponse rsp)
			throws Exception, ServletException {

		getACL().checkPermission(PerforceScm.TAG);

		String description = req.getParameter("desc");
		String name = req.getParameter("name");
		labelBuild(name, description);

		rsp.sendRedirect(".");
	}

	public void labelBuild(String name, String description) throws Exception {

		ClientHelper p4 = new ClientHelper(credential, null, client);
		Label label = new Label();

		label.setDescription(description);
		label.setName(name);
		label.setRevisionSpec("@" + change);

		// set label view to match workspace
		ViewMap<ILabelMapping> viewMapping = new ViewMap<ILabelMapping>();
		ClientView view = p4.getClientView();
		for (IClientViewMapping entry : view) {
			String left = entry.getLeft();
			LabelMapping lblMap = new LabelMapping();
			lblMap.setLeft(left);
			viewMapping.addEntry(lblMap);
		}
		label.setViewMapping(viewMapping);

		// save label
		if (!tags.contains(name)) {
			tags.add(name);
			build.save();
		}

		// update Perforce
		p4.setLabel(label);
		p4.disconnect();
	}

	public void setChange(int change) {
		this.change = change;
	}

	public int getChange() {
		return change;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public List<String> getTags() {
		return tags;
	}

	public Label getLabel(String tag) throws Exception {
		ClientHelper p4 = new ClientHelper(credential, null, client);
		Label label = p4.getLabel(tag);
		return label;
	}
}