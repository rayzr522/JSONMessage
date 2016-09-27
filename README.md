# JSONMessage
A modern implementation of Fanciful

This library aims to completely replace [Fanciful](https://bukkit.org/threads/lib-fanciful-pleasant-chat-message-formatting.195148/), which sadly has stopped being updated. I figured I'd take a stab at re-creating it, and even making it better.

The best thing about this library? It's only a [single class file](https://github.com/Rayzr522/JSONMessage/blob/master/src/main/java/com/perceivedev/jsonmessage/JSONMessage.java), so you can just drag and drop it into your project!

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

One thing to note about tooltips is that they *themselves* can actually take a JSONMessage as a parameter:

    JSONMessage.create("This has a fancy tooltip!")
               .tooltip(JSONMessage.create("Green text!")
                                     .color(ChatColor.GREEN)
                                     .style(ChatColor.BOLD)
                                   .then("\nUse the newline character to\nadd lines to the tooltip")
                                     .color(ChatColor.YELLOW))
               .color(ChatColor.GOLD);
               
You can also make text which runs commands when you click on it:

    JSONMessage.create("Click")
                 .color(ChatColor.GOLD)
               .then(" here ")
                 .color(ChatColor.RED)
                 .runCommand("/warp spawn")
               .then("to go to spawn")
                 .color(ChatColor.GOLD);
                 
To send a JSONMessage, it's quite simple:

    JSONMessage.create("Hello!")
                 .tooltip("World!")
               .send(player);
                 
You can pass as many players as you want into `send`, meaning you could send this to huge groups of players.

If you want to see all the available methods, you can find them just below this.

## Methods overview

Method | Description
------ | -----------
`create(String)` | Creates a new JSONMessage with the given text as a starting point
`color(ChatColor)` | Sets the color of the current message part
`style(ChatColor)` | Adds a style to the current message part
`runCommand(String)` | `ClickEvent`: Runs the given command
`suggestCommand(String)` |  `ClickEvent`: Suggests the given command by inserting it into the player's chat area
`openURL(String)` | `ClickEvent`: Opens the given URL
`changePage(int)` | `ClickEvent`: changes the page of a book to the given page
`tooltip(String)` | `HoverEvent`: shows the given text
`tooltip(JSONMessage)` | `HoverEvent`: shows the given JSON as text (works just like the rest of this system)
`achievement(String)` | `HoverEvent`: shows an achievement with the given ID
`then(String)` | Adds another part to the message
`bar(int)` | Creates a horizontal divider bar of the given length
`bar()` | Creates a horizontal divider bar 53 characters long. This is perfect for the default chat window width
`newline()` | Inserts a newline. It really isn't necessary, you can just use `\n` if you want
`toJSON()` | Converts the JSONMessage to a `JsonObject` (Google's Gson library, comes with Bukkit)
`toString()` | Converts the JSONMessage to a String, useable in things like `/tellraw`. This is an alias of `toJSON().toString()`
`send(Player...)` | Sends the JSONMessage to one or many players

### Method Notes
- `color(ChatColor)` and `style(ChatColor)` both use ChatColors but require different types. Attempting to pass the wrong type in (e.g. doing `style(ChatColor.GREEN)`, or doing `color(ChatColor.BOLD)`) will result in an IllegalArgumentException.
- `tooltip(JSONMessage)` takes another JSONMessage instance, allowing you to create fancy text for your tooltips.
