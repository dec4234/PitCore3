# PitCore3
PitCore3 is the core plugin made specifically for a server I helped create, called The Pit. The core theme of The Pit revolved around mob grinding in specific arenas A.K.A. "Pits".

This plugin served as the backend for the entire server, providing functionality for almost all features.

Player stats are [held in and managed through MongoDB.](https://github.com/dec4234/PitCore3/blob/master/src/main/java/net/dec4234/pitcore3/framework/database/MongoDatabase.java)

[Player sidebar scoreboards](https://github.com/dec4234/PitCore3/blob/master/src/main/java/net/dec4234/pitcore3/scoreboard/player/PlayerScoreboard.java) were also managed through this plugin.

As well as a [public scoreboard](https://github.com/dec4234/PitCore3/blob/master/src/main/java/net/dec4234/pitcore3/scoreboard/publicScoreboard/PublicScoreboardManager.java) that displayed the top 5 people in terms of kills for that day.

The plugin also supported [private vaults](https://github.com/dec4234/PitCore3/blob/master/src/main/java/net/dec4234/pitcore3/inventory/privatevault/PrivateVault.java) which were stored as a base 64 encoded string in the MongoDB file for that player.

The plugin also had an [extensive mob management system](https://github.com/dec4234/PitCore3/blob/master/src/main/java/net/dec4234/pitcore3/mobs/PitMob.java) which was responsible for spawning mobs in as needed after they were killed. Very similar to the system found in Hypixel Skyblock.

[Kits](https://github.com/dec4234/PitCore3/blob/master/src/main/java/net/dec4234/pitcore3/guis/kits/framework/Kit.java) were managed through the plugin as well, although it was never fully operational in this version. 
