# SerialClient
Serial java client for sending data to server

This program is useful for saving and loading between UART and server

# Instructions of saving

For using this desktop app you should first send bellow command for saving 
1. name
2. score
3. heart
4. level
5. time
6. date
7. turbo
8. speed
9. gsec(for game seconds)
10. mcp(for main car position. For value you should send 4 digits number, first 2 digits for x and second 2 digits for y)
11. ecp(for enemy car position. For value like mcp and send all cars follow each other for example: 11021501 this is for 2 cars position)
* In order to unify please start x and y from 0

After sending all of the above commands please send (save) command to saving data to the server and then receive your packet ID

# Instructions of loading

For loading the game from the game menu select load option and then send (load) command with UART. In the following type the packet ID with your keypad and submit it to receive data by UART from server and sending data to your board like saving instructions.
