![Alt Text](https://i.imgur.com/Hz8FRfn.png)

# ChatBot
![Alt Text](https://i.imgur.com/4LEHuUj.png)

Introducing ChatBot, a lightweight, fully in-game configurable, plug & play plugin 

ChatBot is a bot that answers messages in the chat. When a player asks or says something, the bot reads it and, if you configurated a response for this message, it will answer. 
You can set multiple responses to multiple messages, separated by keys. You can also set permission, so then you can set up permission responses and no permission responses.

Let's suppose that you configurated this in the YAML: 
```
ip:
  messages:
    . 'What is the ip'
    - 'How can i enter to the server'
  permission: 'chatbot.ip'
  permission-responses: 
    - '%prefix% &e@%player% &bThe ip is ...'
    - '%prefix% &bIP: ...'
  no-permission-responses:
    - '%prefix% &cI won't tell you'
  ```

When a player writes in the chat "What is the ip?", or any message that contains this message (for example, "Hey guys, anyone knows what is the IP? Please!"), if the player has 'chatbot.ip' permission then the bot will choose a random response from permission-responses and say that. Otherwise, if player has not this permission, the bot will say "%prefix% &cI won't tell you". 

It is also in-game configurable. You can get more info in the [Wiki](https://github.com/MatiRosen/chatbot/wiki/Wiki). It is fully customizable and translatable. 

PlaceholderAPI: You can use PAPI and use any placeholder you want in your responses!


![Alt Text](https://i.imgur.com/T7dZJrX.png)
* **/chatbot menu** (Opens the configuration menu)
* **/chatbot help** (Shows all commands in game)
* **/chatbot reload** (Reloads all the files.)

![Alt Text](https://i.imgur.com/c6XKbDZ.png)
* **chatbot.menu** (Allows players to open configuration menu and add messages or responses)
* **chatbot.help** (Allows players to use help command)
* **chatbot.reload** (Allows players to use reload command)
* **chatbot.bypass.cooldown** (Allows players to bypass the chatbot cooldown.)

![Alt Text](https://i.imgur.com/F1Bijy7.png)

![Alt Text](https://imgur.com/Arq4WAC.png)
![Alt Text](https://imgur.com/0tglEfL.png)
![Alt Text](https://imgur.com/2eS6yAl.png)
![Alt Text](https://imgur.com/Bk7QzhI.png)
![Alt Text](https://imgur.com/oKQu7zo.png)

![Alt Text](https://i.imgur.com/865JxZg.png)
## * **[Download](https://www.spigotmc.org/resources/chatbot-in-game-configurable.93347/)**
## * **[Wiki](https://github.com/MatiRosen/chatbot/wiki/Wiki)**
## * **[Discord](https://discord.gg/cvagVTztZZ) server for help**
## * **Drawings by [AlanAMB](https://www.instagram.com/alanadmaba/)**
