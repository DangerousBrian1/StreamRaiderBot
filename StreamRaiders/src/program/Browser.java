package program;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

import javax.swing.JFrame;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.CefSettings.LogSeverity;
import org.cef.OS;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefCookieVisitor;
import org.cef.handler.CefAppHandlerAdapter;
import org.cef.misc.BoolRef;
import org.cef.network.CefCookie;
import org.cef.network.CefCookieManager;

public class Browser extends JFrame {

	private static final long serialVersionUID = -5570653778104813836L;
	private final CefApp     cefApp_;
	private final CefClient  client_;
	private final CefBrowser browser_;
	private final Component  browerUI_;

	public Browser(String name) {
		
		Hashtable<String, String> tab = new Hashtable<>();
		
		String startURL = "https://www.streamraiders.com/game/";
		boolean useOSR = OS.isLinux();
		boolean isTransparent = false;
		
		CefApp.addAppHandler(new CefAppHandlerAdapter(null) {});
		CefSettings settings = new CefSettings();
		settings.windowless_rendering_enabled = useOSR;
		settings.log_severity = LogSeverity.LOGSEVERITY_DISABLE;
		
		cefApp_ = CefApp.getInstance(settings);

		client_ = cefApp_.createClient();
		
		browser_ = client_.createBrowser(startURL, useOSR, isTransparent);
		browerUI_ = browser_.getUIComponent();

		
		getContentPane().add(browerUI_, BorderLayout.CENTER);
		pack();
		setSize(800,600);
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				CefCookieManager.getGlobalManager().visitAllCookies(new CefCookieVisitor() {
					@Override
					public boolean visit(CefCookie arg0, int arg1, int arg2, BoolRef arg3) {
						if(arg0.domain.contains("streamraiders")) {
							tab.put(arg0.name, arg0.value);
						}
						return true;
					}
				});
				CefApp.getInstance().dispose();
				
				dispose();
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {}
				
				NEF.saveOpt("profiles/" + name + ".app", tab);
				
				MainFrame.refresh();
			}
		});
	}
}
