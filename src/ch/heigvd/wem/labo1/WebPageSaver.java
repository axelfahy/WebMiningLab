package ch.heigvd.wem.labo1;

import ch.heigvd.wem.interfaces.Index;

import java.util.HashMap;

public class WebPageSaver extends Index {

    public WebPageSaver() {
        this.index = new HashMap<>();
        this.invertedIndex = new HashMap<>();
    }

}
