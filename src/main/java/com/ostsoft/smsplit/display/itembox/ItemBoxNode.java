package com.ostsoft.smsplit.display.itembox;

import com.ostsoft.smsplit.AutoData;
import com.ostsoft.smsplit.xml.config.ItemBox;
import com.ostsoft.smsplit.xml.config.RectangleXML;

public class ItemBoxNode {
    private final AutoData autoData;
    private final ItemBox selectedItemBox;
    private final RectangleXML rectangle;

    public ItemBoxNode(AutoData autoData, ItemBox selectedItemBox, RectangleXML rectangle) {
        this.autoData = autoData;
        this.selectedItemBox = selectedItemBox;
        this.rectangle = rectangle;
    }

    public AutoData getAutoData() {
        return autoData;
    }

    public ItemBox getSelectedItemBox() {
        return selectedItemBox;
    }

    public RectangleXML getRectangle() {
        return rectangle;
    }

    @Override
    public String toString() {
        return "(" + rectangle.x + ", " + rectangle.y + ")";
    }
}
