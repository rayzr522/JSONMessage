# JSONMessage
A modern implementation of Fanciful

This library aims to completely replace [Fanciful](https://bukkit.org/threads/lib-fanciful-pleasant-chat-message-formatting.195148/), which sadly has stopped being updated. I figured I'd take a stab at re-creating it, and even making it better.

The best thing about this library? It's only a [single class file](https://github.com/Rayzr522/JSONMessage/blob/master/src/main/java/com/perceivedev/jsonmessage/JSONMessage.java), so just drag and drop it.

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

## Methods overview

Method | Description
------ | -----------
create(String) | Creates a new JSONMessage with the given text as a starting point
color(ChatColor) | Sets the color of the current message part
style(ChatColor) | Adds a style to the current message part
runCommand(String) | ClickEvent: Runs the given command
suggestCommand(String) |  ClickEvent: Suggests the given command by inserting it into the player's chat area
openURL(String) | ClickEvent: Opens the given URL
changePage(int) | ClickEvent: changes the page of a book to the given page
tooltip(String) | HoverEvent: shows the given text
tooltip(JSONMessage) | HoverEvent: shows the given JSON as text (works just like the rest of this system)
achievement(String) | HoverEvent: shows an achievement with the given ID
then(String) | Adds another part to the message
toJSON() | Converts the JSONMessage to a `JsonObject` (Google's Gson library, comes with Bukkit)
toString() | Converts the JSONMessage to a String, useable in things like `/tellraw`. This is an alias of `toJSON().toString()`

### Method Notes
- `color(ChatColor)` and `style(ChatColor)` both use ChatColors but require different types. Attempting to pass the wrong type in (e.g. doing `style(ChatColor.GREEN)`, or doing `color(ChatColor.BOLD)`) will result in an IllegalArgumentException.
- `tooltip(JSONMessage)` takes another JSONMessage instance, allowing you to create fancy text for your tooltips.
