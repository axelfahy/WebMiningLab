package ch.heigvd.wem.labo1;

import ch.heigvd.wem.interfaces.Index;

import java.util.HashMap;

public class WebPageIndex extends Index {

    public WebPageIndex() {
        this.index = new HashMap<>();
        this.invertedIndex = new HashMap<>();
        //this.ponderation = new HashMap<>();
        //this.invertedPonderation = new HashMap<>();
        this.linkTable = new HashMap<>();
    }

}
