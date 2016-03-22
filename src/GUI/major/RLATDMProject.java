/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.major;

import coreEngine.Seed;

/**
 *
 * @author jltrask
 */
public class RLATDMProject extends FREEVALProject {

    public final Seed seed;

    public RLATDMProject(Seed seed) {
        super(MainWindow.TOOLBOX_RL_ATDM);
        this.seed = seed;
    }

}
