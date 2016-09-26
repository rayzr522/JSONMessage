# JSONMessage
A modern implementation of Fanciful

This library aims to completely replace [Fanciful](https://bukkit.org/threads/lib-fanciful-pleasant-chat-message-formatting.195148/), which sadly has stopped being updated. I figured I'd take a stab at re-creating it, and even making it better.

## Usage
This library is extremely easy to use! It uses a nearly identical system as Fanciful. Example:
    
    JSONMessage.create("Hello!").color(ChatColor.RED);
    
This creates a simple message saying "Hello!", with red text. Now you might say to yourself, why use this? I can easily make red text and send it to the player! Well my friend, there are many more features. Be patient :wink:

Here's a more complicated example, showing the true power of JSONMessage:

    JSONMessage.create("Go to the ")
                 .color(ChatColor.GOLD)
               .then("best website ever!")
                 .color(ChatColor.BLUE)
                 .tooltip("Click to go")
                 .openURL("http://www.youtube.com/");
                 
This example creates a message in two parts, one gold, one blue, and gives the second part a tooltip as well as an action! When you click on this, it will open YouTube.

*More to this coming soon...*
