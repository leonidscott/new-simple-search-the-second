

** WE NEED TO LOOK AT THIS **
Put some thought into that write-up. Is your description of your idea coherent? Will we understand it? Is your idea compelling? Does it raise additional ideas and/or questions that you want to capture for future consideration?


Our Hill Climber Search had these results:

For knapPI_11_20_1000_1 with 100,000 tweaks, we got: 1428, 1326, 1428, 1428

For knapPI_13_20_1000_1 with 100,000 tweaks, we got: 1716, 1677, 1638, 1599

For knapPI_16_20_1000_1 with 100,000 tweaks, we got: 2090, 2172, 2167, 2172


Random Search had these results:

For knapPI_11_20_1000_1 with 100,000 tweaks, we got: 1224, 1428, 1428, 1326

For knapPI_13_20_1000_1 with 100,000 tweaks, we got: 1599, 1599, 1560, 1560

For knapPI_16_20_1000_1 with 100,000 tweaks, we got: 2185, 2126, 2013, 2162


Hill Climber:
Our hill climber search starts by taking the answer of our original class method random search. This is giving us the best random :score of ‘max-tries’ attempted to begin with, and we assign it as ‘current’. ‘current’ is handed to our ‘tweaker’ method [discussed later] and is assigned as the ‘tweaker-instance’. From there we compare their scores and the better recurses through our hill climber search method. We recurse through ‘max-tries’ times. 

Tweaker:
Tweaker is our mutating function. It takes in an instance and creates a new instance of either adding (0 to 1) or removing (1 to 0) an item from ‘:choices’. This item is chosen randomly. 

Remove Item:
In ‘remove-item’ we map choices and multiply each element by it’s index (plus one for zero, we subtract one from each element after) to create a new vector. That allows us to randomly chose one of these elements without causing a stackoverflow error, and flipping the 1 to a 0 in :choices.

Add Item:
In ‘add-item’ we use the result of ‘remove-item’ and map it with a vector of each element is its index (plus one for zero, we subtract one from each element after). In this map we remove items from the index vector, which results in only the zero indices from :choices. Like ‘remove-item’, this allows us to randomly chose one of these elements without causing a stackoverflow error, and flipping the 0 to a 1 in :choices.

Make Instance :
In ‘make-instance’ the process from creating an instance in ‘random-answer’ is repeated. Now, we give it our new choices and the instance. This is handed to ‘included-items’, which grabs the appropriate elements from the knapsack; allowing us to recreate elements in the new instance.
