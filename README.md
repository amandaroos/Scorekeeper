# Scorekeeper
Simple, multiplayer scorekeeper



-Add players with the floating action button

-Long press to edit a player's name or to delete the player

-Tap on a player to add or subtract points

-Use the menu to reset all scores or delete all players



---


These actions are all fairly simple to implement because this app is built on a SQLite database. I made a 4-minute video describing the bascis of how the SQLite Database works in this app: https://youtu.be/RBLFM4gY5bY

Therer are three files used to make the database in this app:

-PlayerDBHelper

-PlayerContract

-PlayerProvider


The PlayerDBHelper helps create the database and the PlayerContract defines constants for the table and column names. The PlayerProvider is a layer of abstraction that provides indirect access to the database.  Functions like insert, update, delete, and query are defined in the PlayerProvider.


---


For more details, check out Udacity's Android:Data Storage class - https://www.udacity.com/course/android-basics-data-storage--ud845
