package org.openremote.manager.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.Window;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;
import org.openremote.manager.client.i18n.ManagerConstants;
import org.openremote.manager.client.i18n.ManagerMessages;
import org.openremote.manager.client.presenter.*;
import org.openremote.manager.client.rest.AssetRestService;
import org.openremote.manager.client.rest.LoginRestService;
import org.openremote.manager.client.rest.MapRestService;
import org.openremote.manager.client.service.*;
import org.openremote.manager.client.view.*;

public class MainModule extends AbstractGinModule {

    @Override
    protected void configure() {
        // App Wiring
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(PlaceHistoryMapper.class).to(HistoryMapper.class).in(Singleton.class);
        bind(AppController.class).to(AppControllerImpl.class).in(Singleton.class);

        // Views
        bind(HeaderView.class).to(HeaderViewImpl.class).in(Singleton.class);
        bind(LoginView.class).to(LoginViewImpl.class).in(Singleton.class);
        bind(AppLayout.class).to(AppLayoutImpl.class).in(Singleton.class);
        bind(MapView.class).to(MapViewImpl.class).in(Singleton.class);
        bind(AssetListView.class).to(AssetListViewImpl.class).in(Singleton.class);
        bind(AssetDetailView.class).to(AssetDetailViewImpl.class).in(Singleton.class);
        bind(LeftSideView.class).to(LeftSideViewImpl.class).in(Singleton.class);

        // Activities
        bind(AssetDetailActivity.class);
        bind(MapActivity.class);

        // Services
        bind(SecurityService.class).to(SecurityServiceImpl.class).in(Singleton.class);
        bind(CookieService.class).to(CookieServiceImpl.class).in(Singleton.class);
        bind(ValidatorService.class).to(ValidatorServiceImpl.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    @Named("MainContentManager")
    public ActivityManager getMainContentActivityMapper(
            MainContentActivityMapper activityMapper, EventBus eventBus) {
        return new ActivityManager(activityMapper, eventBus);
    }

    @Provides
    @Singleton
    @Named("LeftSideManager")
    public ActivityManager getLeftSideActivityMapper(
            LeftSideActivityMapper activityMapper, EventBus eventBus) {
        return new ActivityManager(activityMapper, eventBus);
    }

    @Provides
    @Singleton
    public ManagerConstants getConstants() {
        return GWT.create(ManagerConstants.class);
    }

    @Provides
    @Singleton
    public ManagerMessages getMessages() {
        return GWT.create(ManagerMessages.class);
    }

    @Provides
    @Singleton
    public LoginRestService getLoginRestService() {
        String baseUrl = GWT.getHostPageBaseURL();
        LoginRestService loginService = GWT.create(LoginRestService.class);
        ((RestServiceProxy) loginService).setResource(new Resource(baseUrl));
        return loginService;
    }

    @Provides
    @Singleton
    public MapRestService getMapRestService() {
        String baseUrl = GWT.getHostPageBaseURL() + Window.Location.getPath().substring(1);
        MapRestService mapRestService = GWT.create(MapRestService.class);
        ((RestServiceProxy) mapRestService).setResource(new Resource(baseUrl));
        return mapRestService;
    }

    @Provides
    @Singleton
    public AssetRestService getAssetRestService() {
        String baseUrl = GWT.getHostPageBaseURL();
        AssetRestService assetRestService = GWT.create(AssetRestService.class);
        ((RestServiceProxy) assetRestService).setResource(new Resource(baseUrl));
        return assetRestService;
    }

    @Provides
    @Singleton
    public PlaceController getPlaceController(SecurityService securityService, PlaceHistoryMapper historyMapper, EventBus eventBus) {
        return new AppControllerImpl.AppPlaceController(securityService, historyMapper, eventBus);
    }

    @Provides
    @Singleton
    public PlaceHistoryHandler getHistoryHandler(PlaceController placeController,
                                                 PlaceHistoryMapper historyMapper,
                                                 EventBus eventBus) {
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, new OverviewPlace());
        return historyHandler;
    }
}