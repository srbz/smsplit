package com.ostsoft.smsplit;

import com.ostsoft.smsplit.observer.EventType;
import com.ostsoft.smsplit.util.ImageUtil;
import com.ostsoft.smsplit.xml.config.ItemBox;
import com.ostsoft.smsplit.xml.config.RectangleXML;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ItemBoxSplitter {
    private static Logger logger = Logger.getLogger(ItemBoxSplitter.class.getName());
    private final AutoData autoData;

    public ItemBoxSplitter(AutoData autoData) {
        this.autoData = autoData;
    }

    public void checkForBox(BufferedImage gameImage) {
        if (!autoData.config.itemBoxes.itemMatching || gameImage == null) {
            return;
        }
        List<ItemBox> items = autoData.config.itemBoxes.itemBox;

        Set<ItemBox> matchingItems = getMatchingItems(gameImage);
        for (ItemBox matchingItem : matchingItems) {
            if (matchingItem.timeSinceFirstMatch == 0) {
                matchingItem.timeSinceFirstMatch = System.currentTimeMillis();
            }
        }


        Set<ItemBox> itemsToSplitOn = new HashSet<>();

        Set<ItemBox> nonMatchingItems = items.stream().filter(p -> !matchingItems.contains(p)).collect(Collectors.toSet());
        for (ItemBox nonMatchingItem : nonMatchingItems) {
            if (nonMatchingItem.timeSinceFirstMatch == 0) {
                continue;
            }

            if (System.currentTimeMillis() - nonMatchingItem.timeSinceFirstMatch >= nonMatchingItem.duration) {
                // now this item is going to the splitter :-)
                itemsToSplitOn.add(nonMatchingItem);

                long duration = System.currentTimeMillis() - nonMatchingItem.timeSinceFirstMatch;
                String msg = "Matched for " + duration + "ms with pattern for " + nonMatchingItem.name;
                autoData.fireEvent(EventType.STATUS_BAR_MESSAGE, msg);
                logger.log(Level.INFO, msg);
            }

            nonMatchingItem.timeSinceFirstMatch = 0;
        }

        printOutMatchingItems(matchingItems);

        if (!itemsToSplitOn.isEmpty()) {
            autoData.getSplitter().itemSplit(itemsToSplitOn);
        }
    }

    private void printOutMatchingItems(Set<ItemBox> matchingItems) {
        if (matchingItems.isEmpty()) {
            return;
        }

        StringBuilder s = new StringBuilder();
        s.append("Matching following items: ");
        for (ItemBox matchingItem : matchingItems) {
            s.append(matchingItem.name).append(", ");
        }
        autoData.fireEvent(EventType.STATUS_BAR_MESSAGE, s.toString());
        logger.log(Level.INFO, s.toString());
    }


    private Set<ItemBox> getMatchingItems(BufferedImage gameImage) {
        double percentWidth = gameImage.getWidth() / 100d;
        double percentHeight = gameImage.getHeight() / 100d;

        Set<ItemBox> matching = new HashSet<>();

        for (ItemBox itemBox : autoData.config.itemBoxes.itemBox) {
            boolean error = false;
            for (RectangleXML rectangle : itemBox.rectangles) {
                if (ImageUtil.checkRect(gameImage
                        , (int) (percentWidth * rectangle.x)
                        , (int) (percentHeight * rectangle.y)
                        , (int) (percentWidth * rectangle.width)
                        , (int) (percentHeight * rectangle.height)
                        , rectangle.r
                        , rectangle.g
                        , rectangle.b
                        , rectangle.tolerance)) {
                    if (rectangle.invert) {
                        error = true;
                        rectangle.matching = false;
                    } else {
                        rectangle.matching = true;
                    }
                } else if (!rectangle.invert) {
                    error = true;
                    rectangle.matching = false;
                } else {
                    rectangle.matching = true;
                }
            }
            if (!error) {
                matching.add(itemBox);
            }
        }
        return matching;
    }


}
