/*
 * Copyright 2016, OpenRemote Inc.
 *
 * See the CONTRIBUTORS.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.manager.client;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.openremote.manager.client.event.EventMapper;
import org.openremote.manager.client.event.bus.EventBus;
import org.openremote.manager.client.http.ConstraintViolationReportMapper;
import org.openremote.manager.client.i18n.ManagerMessages;
import org.openremote.manager.client.interop.keycloak.Keycloak;
import org.openremote.manager.client.map.MapPlace;
import org.openremote.manager.client.mvp.AppActivityManager;
import org.openremote.manager.client.mvp.AppPlaceController;
import org.openremote.manager.client.service.*;
import org.openremote.manager.client.style.WidgetStyle;

public class ManagerModule extends AbstractGinModule {

    @Override
    protected void configure() {

        bind(PlaceHistoryMapper.class).to(ManagerHistoryMapper.class).in(Singleton.class);

        bind(CookieService.class).to(CookieServiceImpl.class).in(Singleton.class);
        bind(ValidatorService.class).to(ValidatorServiceImpl.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public AppActivityManager getActivityManager(ManagerActivityMapper activityMapper, EventBus eventBus) {
        return new AppActivityManager("AppActivityManager", activityMapper, eventBus);
    }

    @Provides
    @Singleton
    public Environment createEnvironment(SecurityService securityService,
                                         RequestService requestService,
                                         EventService eventService,
                                         PlaceController placeController,
                                         EventBus eventBus,
                                         ManagerMessages managerMessages,
                                         WidgetStyle widgetStyle) {
        return Environment.create(
            securityService,
            requestService,
            eventService,
            placeController,
            eventBus,
            managerMessages,
            widgetStyle
        );
    }

    @Provides
    @Singleton
    public PlaceController getPlaceController(SecurityService securityService,
                                              EventBus eventBus,
                                              com.google.web.bindery.event.shared.EventBus legacyEventBus,
                                              PlaceController.Delegate delegate) {
        return new AppPlaceController(securityService, eventBus, legacyEventBus, delegate);
    }

    @Provides
    @Singleton
    public EventService getEventService(SecurityService securityService, EventBus eventBus, EventMapper eventMapper) {
        EventService eventService = EventServiceImpl.create(securityService, eventBus, eventMapper);
        eventService.connect();
        return eventService;
    }


    @Provides
    @Singleton
    public SecurityService getSecurityService(
        CookieService cookieService, EventBus eventBus) {
        return new SecurityServiceImpl(getKeyCloak(), cookieService, eventBus);
    }

    public static native Keycloak getKeyCloak() /*-{
        return $wnd.keycloak;
    }-*/;

    @Provides
    @Singleton
    public RequestService getRequestService(SecurityService securityService,
                                            ConstraintViolationReportMapper constraintViolationReportMapper) {
        RequestServiceImpl.Configuration.setDefaults(securityService.getRealm());
        return new RequestServiceImpl(securityService, constraintViolationReportMapper);
    }

    @Provides
    @Singleton
    public PlaceHistoryHandler getHistoryHandler(PlaceController placeController,
                                                 PlaceHistoryMapper historyMapper,
                                                 com.google.web.bindery.event.shared.EventBus legacyEventBus) {
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, legacyEventBus, new MapPlace());
        return historyHandler;
    }
}
