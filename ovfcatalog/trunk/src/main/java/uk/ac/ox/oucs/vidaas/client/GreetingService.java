package uk.ac.ox.oucs.vidaas.client;

import uk.ac.ox.oucs.vidaas.shared.OvfCatalogException;
import uk.ac.ox.oucs.vidaas.shared.VmValue;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
  VmValue[] getVMs() throws OvfCatalogException;
  VmValue[] getTemplates() throws OvfCatalogException;
}
