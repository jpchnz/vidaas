package uk.ac.ox.oucs.vidaas.client;

import uk.ac.ox.oucs.vidaas.shared.VmValue;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ovfcatalog implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		greetingService.getVMs(new AsyncCallback<VmValue[]>() {

			@Override
			public void onFailure(Throwable arg0) {
				arg0.printStackTrace();
			}

			@Override
			public void onSuccess(VmValue[] vms) {
				updateList("vmlistContainer", vms);
			}

		});
		
		greetingService.getTemplates(new AsyncCallback<VmValue[]>() {

			@Override
			public void onFailure(Throwable arg0) {
				arg0.printStackTrace();
			}

			@Override
			public void onSuccess(VmValue[] vms) {
				updateList("templatelistContainer", vms);
			}

		});
		
	}

	private void updateList(String list, VmValue[] vms) {
		FlexTable flexTableVmlist = new FlexTable();
		RootPanel.get(list).add(flexTableVmlist);
		int i = 0;
		for (VmValue vm : vms) {
			int j = 0;
			flexTableVmlist.setText(i, j++, vm.getName());
			flexTableVmlist.setText(i, j++, "" + vm.getStatus());

			String[] ips = vm.getIpAddresses();
			if(ips != null) {
				for (String ip : ips) {
					flexTableVmlist.setWidget(i, j++, new Anchor(ip, "http://" + ip + "/"));
				}
			}
			String[] files = vm.getFiles();
			if(files !=null) {
				for (String file : files) {
					flexTableVmlist.setWidget(i, j++, new Label(file));
				}
			}
			if(vm.isCreateable()) {
				flexTableVmlist.setWidget(i, j++, new Button("buy", new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent arg0) {
						// TODO Auto-generated method stub
						
					}
				}));
			}
			i++;
		}
	}
	

}
