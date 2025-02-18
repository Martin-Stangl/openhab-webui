/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.ui.basic.internal.render;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.smarthome.core.library.types.PointType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.model.sitemap.Mapview;
import org.eclipse.smarthome.model.sitemap.Widget;
import org.eclipse.smarthome.ui.items.ItemUIRegistry;
import org.openhab.ui.basic.render.RenderException;
import org.openhab.ui.basic.render.WidgetRenderer;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * This is an implementation of the {@link WidgetRenderer} interface, which
 * can produce HTML code for Text widgets.
 *
 * @author Gaël L'hopital - Initial contribution
 *
 */
@Component(service = WidgetRenderer.class)
public class MapviewRenderer extends AbstractWidgetRenderer {

    private static final String MAP_URL = "//www.openstreetmap.org/export/embed.html?bbox=%lonminus%,%latminus%,%lonplus%,%latplus%&marker=%lat%,%lon%";
    private static final double MAP_ZOOM = 0.01;

    @Override
    @Activate
    protected void activate(BundleContext bundleContext) {
        super.activate(bundleContext);
    }

    @Override
    @Deactivate
    protected void deactivate(BundleContext bundleContext) {
        super.deactivate(bundleContext);
    }

    @Override
    public boolean canRender(Widget w) {
        return w instanceof Mapview;
    }

    @Override
    public EList<Widget> renderWidget(Widget w, StringBuilder sb) throws RenderException {
        Mapview mapview = (Mapview) w;
        String snippet = getSnippet("mapview");
        snippet = preprocessSnippet(snippet, mapview);
        // Process the color tags
        snippet = processColor(w, snippet);

        State state = itemUIRegistry.getState(mapview);
        if (state instanceof PointType) {
            PointType pointState = (PointType) state;
            double latitude = pointState.getLatitude().doubleValue();
            double longitude = pointState.getLongitude().doubleValue();
            snippet = StringUtils.replace(snippet, "%url%", MAP_URL);
            snippet = StringUtils.replace(snippet, "%lat%", Double.toString(latitude));
            snippet = StringUtils.replace(snippet, "%lon%", Double.toString(longitude));
            snippet = StringUtils.replace(snippet, "%lonminus%", Double.toString(longitude - MAP_ZOOM));
            snippet = StringUtils.replace(snippet, "%lonplus%", Double.toString(longitude + MAP_ZOOM));
            snippet = StringUtils.replace(snippet, "%latminus%", Double.toString(latitude - MAP_ZOOM));
            snippet = StringUtils.replace(snippet, "%latplus%", Double.toString(latitude + MAP_ZOOM));
        } else {
            snippet = StringUtils.replace(snippet, "%url%", "images/map-marker-off.png");
        }

        snippet = StringUtils.replace(snippet, "%map_url%", MAP_URL);
        snippet = StringUtils.replace(snippet, "%map_zoom%", Double.toString(MAP_ZOOM));

        int height = mapview.getHeight();
        if (height == 0) {
            height = 4; // set default height to something viewable
        }
        height = height * 36;
        snippet = StringUtils.replace(snippet, "%height%", Integer.toString(height));

        sb.append(snippet);
        return null;
    }

    @Override
    @Reference
    protected void setItemUIRegistry(ItemUIRegistry ItemUIRegistry) {
        super.setItemUIRegistry(ItemUIRegistry);
    }

    @Override
    protected void unsetItemUIRegistry(ItemUIRegistry ItemUIRegistry) {
        super.unsetItemUIRegistry(ItemUIRegistry);
    }

}
