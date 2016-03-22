/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coreEngine.Helper;

import java.io.Serializable;

/**
 *
 * @author jltrask
 */
public class CECoordinate implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 23423423411L;

    /**
     * Latitude of the Coordinate
     */
    public float latitude;

    /**
     * Longitude of the coordinate
     */
    public float longitude;

    /**
     * Constructor to create a CECordinate instance.
     *
     * @param lat Latitude
     * @param lon Longitude
     */
    public CECoordinate(float lat, float lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    @Override
    public String toString() {
        return "(" + String.format("%.4f", latitude) + "," + String.format("%.4f", longitude) + ")";
    }

}
