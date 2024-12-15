package com.example.listem.activator;

import com.example.listem.controller.NoteController;
import com.example.listem.implementation.NoteServiceImpl;
import com.example.listem.service.NoteService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.HttpContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

public class Activator implements BundleActivator {

    private ServiceReference<?> httpServiceRef;
    private HttpService httpService;

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Starting REST API without authentication...");

        // Obtain HttpService
        httpServiceRef = context.getServiceReference(HttpService.class.getName());
        if (httpServiceRef != null) {
            httpService = (HttpService) context.getService(httpServiceRef);

            // Initialize NoteService and Controller
            NoteService noteService = new NoteServiceImpl();
            NoteController controller = new NoteController(noteService);

            // Custom HttpContext to disable authentication
            HttpContext noAuthHttpContext = new HttpContext() {
                @Override
                public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
                    // Explicitly bypass security
                    return true;
                }

                @Override
                public URL getResource(String name) {
                    return null;
                }

                @Override
                public String getMimeType(String name) {
                    return "application/json";
                }
            };

            // Register servlet with no security
            httpService.registerServlet("/notes", controller, null, noAuthHttpContext);
            System.out.println("REST API registered at /notes without authentication.");
        } else {
            System.err.println("HttpService not available. Could not register REST API.");
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Stopping REST API...");

        // Unregister servlet and release HttpService
        if (httpService != null) {
            httpService.unregister("/notes");
        }
        if (httpServiceRef != null) {
            context.ungetService(httpServiceRef);
        }
        System.out.println("REST API stopped successfully.");
    }
}
