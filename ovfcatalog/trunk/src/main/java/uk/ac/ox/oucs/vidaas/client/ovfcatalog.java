package uk.ac.ox.oucs.vidaas.client;

import uk.ac.ox.oucs.vidaas.shared.VmValue;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ovfcatalog implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side service.
	 */
	private final OvfCatalogServiceAsync ovfcatalogService = GWT
			.create(OvfCatalogService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		ovfcatalogService.getVMs(new AsyncCallback<VmValue[]>() {

			@Override
			public void onFailure(Throwable arg0) {
				arg0.printStackTrace();
			}

			@Override
			public void onSuccess(VmValue[] vms) {
				updateList("vmlistContainer", vms);
			}

		});
		
		ovfcatalogService.getTemplates(new AsyncCallback<VmValue[]>() {

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
		if(RootPanel.get(list).getWidgetCount()>0) {
			RootPanel.get(list).getWidget(0).removeFromParent();
		}
		RootPanel.get(list).add(flexTableVmlist);
		int i = 0;
		for (final VmValue vm : vms) {
			int j = 0;
			flexTableVmlist.setText(i, j++, vm.getName());
			if(vm.getStatus()!=null) {
				flexTableVmlist.setText(i, j++, "" + vm.getStatus());
			}

			String[] ips = vm.getIpAddresses();
			if(ips != null) {
				for (String ip : ips) {
					flexTableVmlist.setWidget(i, j++, new Anchor(ip, "http://" + ip + "/"));
				}
			}
			/*String[] files = vm.getFiles();
			if(files !=null) {
				for (String file : files) {
					flexTableVmlist.setWidget(i, j++, new Label(file));
				}
			}*/
			if(vm.isCreateable()) {
				flexTableVmlist.setWidget(i, j++, new Button("Buy", new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent arg0) {
						
						final DialogBox dialogBox = new DialogBox(true, true);
						VerticalPanel vp = new VerticalPanel();
						vp.add(new Label("Please enter a name for your new VM:"));
						final TextBox vmName = new TextBox();
						vp.add(vmName);
						Button okButton = new Button("Create", new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent arg0) {
								ovfcatalogService.createVM(vm.getName(), vmName.getText(), new AsyncCallback<Void>() {
									
									@Override
									public void onSuccess(Void arg0) {
										dialogBox.hide();
									}
									
									@Override
									public void onFailure(Throwable arg0) {
										arg0.printStackTrace();
									}
								});
							}
						});
						vp.add(okButton);
						dialogBox.add(vp);
						dialogBox.center();
						dialogBox.show();
						

					}
				}));
			}
			if(vm.isStoppable()) {
				
				flexTableVmlist.setWidget(i, j++, new Button("Stop", new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent arg0) {
						
						ovfcatalogService.stopVM(vm.getName(), new AsyncCallback<Void>()  {

							@Override
							public void onFailure(Throwable arg0) {
								arg0.printStackTrace();
							}

							@Override
							public void onSuccess(Void arg0) {
								// TODO Auto-generated method stub
								
							}
						});
					}
				}));
			} else {
				j++; //leave column empty for readability
			}
			if(vm.isStartable()) {
				flexTableVmlist.setWidget(i, j++, new Button("Start", new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent arg0) {
						
						ovfcatalogService.startVM(vm.getName(), new AsyncCallback<Void>()  {

							@Override
							public void onFailure(Throwable arg0) {
								arg0.printStackTrace();
							}

							@Override
							public void onSuccess(Void arg0) {
								// TODO Auto-generated method stub
								
							}
						});
					}
				}));
			} else {
				j++; //leave column empty for readability
			}

			flexTableVmlist.setWidget(i, j++, new Label("(" + vm.isBusy() + " Tasks)"));

			i++;
		}
		if("vmlistContainer".equals(list)) {
			Timer t = new Timer() {

				@Override
				public void run() {
					ovfcatalogService.getVMs(new AsyncCallback<VmValue[]>() {

						@Override
						public void onFailure(Throwable arg0) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onSuccess(VmValue[] vms) {
							updateList("vmlistContainer", vms);
						}
					});
					
				}
				
			};
			t.schedule(5000);
		}
	}
	

}
