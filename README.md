# JSONMessage

A modern version of Fanciful

This library aims to completely replace [Fanciful](https://bukkit.org/threads/lib-fanciful-pleasant-chat-message-formatting.195148/), which sadly has stopped being updated. I figured I'd take a stab at re-creating it, and even making it better.

## Installation

### Maven
For those of you using Maven, just add the following to your `pom.xml` file:

```xml
<repository>
    <id>rayzr-repo</id>
    <url>https://rayzr.dev/repo/</url>
</repository>
```

```xml
<dependency>
    <groupId>me.rayzr522</groupId>
    <artifactId>jsonmessage</artifactId>
    <version>1.2.0</version>
</dependency>
```

### Gradle
If you're using Gradle, add this to your `build.gradle` file:

```gradle
repositories {
    maven { url 'https://rayzr.dev/repo/' }
}
```

```gradle
dependencies {
    compile 'me.rayzr522:jsonmessage:1.2.0'
}
```

### Other

Otherwise, just drag n' drop the [single class file](https://github.com/Rayzr522/JSONMessage/blob/master/src/main/java/me/rayzr522/jsonmessage/JSONMessage.java) into your project.
> If you are not yet using Maven, you should be. Really, you should. It's amazing.

## Usage

This library is extremely easy to use! It uses a nearly identical system as Fanciful. Example:

```java
JSONMessage.create("Hello!").color(ChatColor.RED);
```

This creates a simple message saying "Hello!", with red text. Now you might say to yourself, why use this? I can easily make red text and send it to the player! Well my friend, there are many more features. Be patient :wink:

Here's a more complicated example, showing the true power of JSONMessage:

```java
JSONMessage.create("Go to the ")
              .color(ChatColor.GOLD)
            .then("best website ever!")
              .color(ChatColor.BLUE)
              .tooltip("Click to go")
              .openURL("http://www.youtube.com/");
```

This example creates a message in two parts, one gold, one blue, and gives the second part a tooltip as well as an action! When you click on this, it will open YouTube.

One thing to note about tooltips is that they *themselves* can actually take a JSONMessage as a parameter:

```java
JSONMessage.create("This has a fancy tooltip!")
              .color(ChatColor.GOLD)
              .tooltip(JSONMessage.create("Green text!")
                                    .color(ChatColor.GREEN)
                                    .style(ChatColor.BOLD)
                                  .then("\nUse the newline character to\nadd lines to the tooltip")
                                    .color(ChatColor.YELLOW))
```

You can also make text which runs commands when you click on it:

```java
JSONMessage.create("Click")
              .color(ChatColor.GOLD)
            .then(" here ")
              .color(ChatColor.RED)
              .runCommand("/warp spawn")
            .then("to go to spawn")
              .color(ChatColor.GOLD);
```

To send a JSONMessage, it's quite simple:

```java
JSONMessage.create("Hello!")
              .tooltip("World!")
            .send(player);
```

You can pass as many players as you want into `send`, meaning you could send this to huge groups of players.

> Note: If you don't have the following features then please make sure to download the latest version of the class file

You can now send titles and subtitles too. It's actually very much like send:

```java
JSONMessage.create("I am a title")
              .color(ChatColor.GREEN)
              .style(ChatColor.ITALIC)
            .title(10, 20, 10, player);
```

Explanation time. What are those numbers? They are quite simply the `fadeIn`, `stay`, and `fadeOut` variables. These control various aspects of the timing of the title, and they're measured in ticks. So for this example it fades in over 0.5 seconds, stays visible for 1 second, then fades out again over 0.5 seconds.

Adding a subtitle to this is just as easy:

```java
JSONMessage.create("I am a title")
              .color(ChatColor.GREEN)
              .style(ChatColor.ITALIC)
            .title(10, 20, 10, player);
```

```java
JSONMessage.create("A wild subtitle has appeared!")
              .color(ChatColor.GOLD)
            .subtitle(player);
```

The only thing to note is that for subtitles you don't pass in times, as that's completely handled by the original title.

> Note: If you don't have the following features then please make sure to download the latest version of the class file

Actionbar messages can be sent as well, however there's some oddities with them. They don't use the new JSON format, they actually use the legacy format which uses actual color codes. As such, they don't support click events, hover events, that sort of thing.

There are two ways that JSONMessage allows you to send action bars; a static method:

```java
JSONMessage.actionbar("Hello", player);
```

To add coloring to this you can use `&` color codes:

```java
JSONMessage.actionbar("&6I got &ccolors!", player);
```

The other way to send action bars is by creating a JSONMessage like you normally would and then send it to the player:

```java
JSONMessage.create("I am a title")
              .color(ChatColor.GREEN)
              .style(ChatColor.ITALIC)
            .actionbar(player);
```

This will convert the JSON format to the legacy format and then send it to the player.

If you want to see all the available methods, you can find them just below this.

## Methods overview

### Text/Styling
Method             | Description
------------------ | -----------
`bar()`            | Creates a horizontal divider bar 53 characters long. This is perfect for the default chat window width
`bar(int)`         | Creates a horizontal divider bar of the given length
`create(String)`   | Creates a new JSONMessage with the given text as a starting point
`color(ChatColor)` | Sets the color of the current message part
`color(String)`    | Same as `color(ChatColor)` but allows the usage of HEX colors in 1.16 and newer
`font(String)`     | Changes the font used for the text. Only usable in 1.16 and newer
`newline()`        | Inserts a newline. It really isn't necessary, you can just use `\n` if you want
`style(ChatColor)` | Adds a style to the current message part
`then(String)`     | Adds another part to the message

### HoverAction
Method                 | Description
---------------------- | -----------
`achievement(String)`  | Shows an achievement with the given ID
`tooltip(JSONMessage)` | Shows the given JSON as text (works just like the rest of this system)
`tooltip(String)`      | Shows the given text

### ClickAction
Method                   | Description
------------------------ | -----------
`changePage(int)`        | Changes the page of a book to the given page
`copyText(String)`       | Copies the provided text into the Player's clipboard (1.15+ only. Will default to `suggestCommand(String)`)
`openURL(String)`        | Opens the given URL
`runCommand(String)`     | Runs the given command
`suggestCommand(String)` | Suggests the given command by inserting it into the player's chat area

### Sending
Method                                  | Description
--------------------------------------- | -----------
`actionbar(Player...)`                  | Converts the JSONMessage to the legacy format and sends it to one or multiple players
`(static) actionbar(String, Player...)` | Sends an action-bar message to one or multiple players
`send(Player...)`                       | Sends the JSONMessage to one or multiple players
`subtitle(Player...)`                   | Sends the JSONMessage as a subtitle to one or multiple players
`title(int, int, int, Player...)`       | Sends the JSONMessage as a title to one or multiple players. Int parameters are `fadeIn`, `stay`, and `fadeOut`

### Others
Method       | Description
------------ | -----------
`toJSON()`   | Converts the JSONMessage to a `JsonObject` (Google's Gson library, comes with Bukkit)
`toString()` | Converts the JSONMessage to a String, usable in things like `/tellraw`. This is an alias of `toJSON().toString()`

### Method Notes

- `color(ChatColor)` and `style(ChatColor)` both use ChatColors but require different types. Attempting to pass the wrong type in (e.g. doing `style(ChatColor.GREEN)`, or doing `color(ChatColor.BOLD)`) will result in an IllegalArgumentException.
- `tooltip(JSONMessage)` takes another JSONMessage instance, allowing you to create fancy text for your tooltips.
