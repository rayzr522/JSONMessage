/**
 * 
 */
package com.perceivedev.jsonmessage;

import org.bukkit.ChatColor;

/**
 * @author Rayzr
 *
 */
public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {

        JSONMessage hoverText = JSONMessage.create("This is green text!").color(ChatColor.GREEN).then("\nHello world");
        JSONMessage chat = JSONMessage.create("Hello! ").color(ChatColor.GREEN).then("What's up?").color(ChatColor.BLUE).tooltip(hoverText);

        System.out.println(chat.toString());

    }

}
