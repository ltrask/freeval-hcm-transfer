/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.major;

import java.io.Serializable;

/**
 *
 * @author jltrask
 */
public abstract class FREEVALProject implements Serializable {

    public final String PROJECT_TYPE;
    private static final long serialVersionUID = 4681567916985L;

    public FREEVALProject(String projectType) {
        this.PROJECT_TYPE = projectType;
    }

    //public abstract Object getToolboxAuxObject();
}
