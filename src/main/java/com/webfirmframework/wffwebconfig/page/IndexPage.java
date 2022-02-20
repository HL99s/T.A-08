package com.webfirmframework.wffwebconfig.page;

import com.webfirmframework.wffweb.common.URIEventInitiator;
import com.webfirmframework.wffweb.server.page.BrowserPage;
import com.webfirmframework.wffweb.server.page.BrowserPageSession;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.event.ServerMethod;
import com.webfirmframework.wffweb.wffbm.data.WffBMObject;
import com.webfirmframework.wffwebconfig.AppSettings;
import com.webfirmframework.wffwebconfig.server.constants.ServerConstants;
import com.webfirmframework.ui.page.layout.IndexPageLayout;

import java.util.logging.Logger;

public class IndexPage extends BrowserPage {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(IndexPage.class.getName());

    // this is a standard interval
    public static final int HEARTBEAT_TIME_MILLISECONDS = 25000;

    private static final int WS_RECONNECT_TIME = 1000;

    private final BrowserPageSession session;
    private final String contextPath;

    public IndexPage(String contextPath, BrowserPageSession session, String uri) {
        super.setURI(uri);
        this.session = session;
        this.contextPath = contextPath;
    }

    @Override
    public String webSocketUrl() {
        return ServerConstants.DOMAIN_WS_URL
                .concat(ServerConstants.CONTEXT_PATH.concat(ServerConstants.INDEX_PAGE_WS_URI));
    }

    // this is new since 3.0.1
    @Override
    protected void beforeRender() {
        // Write BrowserPage configurations code here.
        // The following code can also be written inside render() method but
        // writing here will be a nice separation of concern.
        super.setWebSocketHeartbeatInterval(HEARTBEAT_TIME_MILLISECONDS);
        super.setWebSocketReconnectInterval(WS_RECONNECT_TIME);
        super.setExecutor(AppSettings.CACHED_THREAD_POOL);
    }

    @Override
    public AbstractHtml render() {
        // Here you can return layout template based on condition, Eg if the
        // user is logged in then return DashboardPageLayout otherwise
        // return LoginPageLayout
        return new IndexPageLayout(this, session, contextPath);
    }

    @Override
    protected void afterRender(AbstractHtml rootTag) {
        super.addServerMethod("urlPathChanged", new ServerMethod() {
            @Override
            public WffBMObject invoke(Event event) {
                IndexPage.super.setURI((String) event.data().getValue("path"));
                return null;
            }
        });
    }

    // this is new since 3.0.18
    @Override
    protected void onInitialClientPing(AbstractHtml rootTag) {
        IndexPageLayout layout = (IndexPageLayout) rootTag;
        // to build main div tags only if there is a client communication
        layout.buildMainDivTags();
    }

    public BrowserPageSession getSession() {
        return session;
    }

    @Override
    protected void beforeURIChange(String uriBefore, String uriAfter, URIEventInitiator initiator) {
        LOGGER.info("IndexPage.beforeURIChange");
        LOGGER.info("uriBefore = " + uriBefore);
        LOGGER.info("uriAfter = " + uriAfter);
        LOGGER.info("initiator = " + initiator);
    }

    @Override
    protected void afterURIChange(String uriBefore, String uriAfter, URIEventInitiator initiator) {
        LOGGER.info("IndexPage.afterURIChange");
        LOGGER.info("uriBefore = " + uriBefore);
        LOGGER.info("uriAfter = " + uriAfter);
        LOGGER.info("initiator = " + initiator);
    }
}
