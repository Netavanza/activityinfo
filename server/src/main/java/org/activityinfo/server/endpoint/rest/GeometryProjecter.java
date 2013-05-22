package org.activityinfo.server.endpoint.rest;

import java.util.Arrays;

import org.activityinfo.server.report.generator.map.TiledMap;
import org.activityinfo.shared.report.content.AiLatLng;
import org.activityinfo.shared.report.content.Point;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.util.GeometryTransformer;

public class GeometryProjecter extends GeometryTransformer {

    private TiledMap map;

    public GeometryProjecter(TiledMap map) {
        super();
        this.map = map;
    }

    @Override
    protected CoordinateSequence transformCoordinates(CoordinateSequence coords, Geometry parent) {

        int n = coords.size();
        Coordinate outCoords[] = new Coordinate[n];
        int outIndex = 0;

        int lastX = Integer.MIN_VALUE;
        int lastY = Integer.MIN_VALUE;
        for (int i = 0; i != n; ++i) {
            Point px = map.fromLatLngToPixel(new AiLatLng(coords.getY(i), coords.getX(i)));
            int x = px.getX();
            int y = px.getY();
            if (x != lastX || y != lastY || i == n - 1) {
                outCoords[outIndex] = new Coordinate(px.getDoubleX(), px.getDoubleY());
                outIndex++;
                lastX = x;
                lastY = y;
            }
        }
        return new CoordinateArraySequence(Arrays.copyOf(outCoords, outIndex));

    }

}
