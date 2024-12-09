This is still a work in progress, during the late stages of the snake game the snake will sometimes freak out. However for the most part the snake will always make it to late game using an A* algorithm I've coded in java.

So far snake enters a "survival mode" whenever it traps itself, however I'm currently working on a way for the snake to find the current position of its tail and move to try and escape once it traps itself to the point where it has to look several moves ahead.
"Survival mode" though is just the snake filling up every open tile it can in hopes that an opening appears. 

After Snake gets to size 20 it now takes the longest route (what I make basically negative heuristical) to the food. FallBackMove or "survival mode" has also been adjusted a bit. Along with this the optimization of the snake game has gotten worse and I am currently working on trying to optimize my A* algorithm better.
